package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags="CustomerWorkOrderAPI", description = "客户工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/customers/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CustomerWorkOrderController {

    //private static Logger log = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;
    private WorkFlowServiceImpl workFlowService;

    @ApiModel(value = "工单信息ID")
    private class IdResponseData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @ApiModel(value = "工单及流程信息")
    private class WorkFlowBeanList {
        @ApiModelProperty(value="所属订单ID", example="111",required=true)
        public String orderId;
        @ApiModelProperty(value="凤巢appID", example="10",required=true)
        public String iAppId;

        @ApiModelProperty(value="第三方appID", example="20110843",required=true)
        public String tAppId;

        @ApiModelProperty(value="退货商品数", example="1",required=true)
        public Integer returnedNum;

        @ApiModelProperty(value="申请退款金额", example="1.1",required=false)
        public Float refundAmount;

        @ApiModelProperty(value="实际退款金额", example="1.1",required=false)
        public Float realRefundAmount;
        @ApiModelProperty(value="商户ID", example="111",required=false)
        public Long merchantId;

        @ApiModelProperty(value="工单标题", example="退货000011",required=true)
        public String title;

        @ApiModelProperty(value="工单描述", example="退货000011",required=false)
        public String description;

        @ApiModelProperty(value="客户ID", example="123",required=false)
        public String receiverId;

        @ApiModelProperty(value="客户名称", example="李四",required=false)
        public String receiverName;

        @ApiModelProperty(value="客户电话", example="13345678901",required=false)
        public String receiverPhone;

        @ApiModelProperty(value="工单状态码", example="1",required=false)
        public Integer status;

        @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11",required=false)
        public Date updateTime;

        @ApiModelProperty(value="退款完成时间", example="2019-06-16 11:11:11",required=false)
        public Date refundTime;

        @ApiModelProperty(value="快递单号", example="2019111111",required=false)
        public String expressNo;

        @ApiModelProperty(value="工单类型ID", example="123",required=true)
        public Integer typeId;

        @ApiModelProperty(value = "流程信息List", example = " ", required = true)
        public List<WorkFlowBean> result;

    }

    private WorkFlowBeanList fillFlowBeans(WorkOrder a, List<WorkFlowBean> list){
        WorkFlowBeanList b = new WorkFlowBeanList();
        b.result = list;
        b.description = a.getDescription();
        b.orderId = a.getOrderId();
        b.expressNo = a.getExpressNo();
        b.iAppId = a.getiAppId();
        b.merchantId = a.getMerchantId();
        b.realRefundAmount = a.getGuanaitongRefundAmount();
        b.receiverId = a.getReceiverId();
        b.receiverName = a.getReceiverName();
        b.receiverPhone = a.getReceiverPhone();
        b.refundAmount = a.getRefundAmount();
        b.returnedNum = a.getReturnedNum();
        b.refundTime = a.getRefundTime();
        b.status = a.getStatus();
        b.tAppId = a.gettAppId();
        b.title = a.getTitle();
        b.updateTime = a.getUpdateTime();
        b.typeId = a.getTypeId();
        return b;
    }

    @ApiModel(value = "工单信息ID")
    private class ValidNumResponseData implements Serializable {
        @ApiModelProperty(value = "validNum", example = "1", required = true)
        public Integer validNum;

    }


    @ApiModel(value = "主订单售后信息")
    private class ParentOrderRefundData implements Serializable {
        @ApiModelProperty(value = "主订单Id", example = "1", required = true)
        public Integer parentOrderId;

        @ApiModelProperty(value = "主订单付款金额(分)", example = "1", required = true)
        public Integer paymentAmount;

        @ApiModelProperty(value = "主订单退款金额(分)", example = "1", required = true)
        public int refundAmount;

        @ApiModelProperty(value = "主订单退款金额(分)", example = "1", required = true)
        public int realRefundAmount;

        public List<OrderRefundBean> result;
    }

    @Autowired
    public CustomerWorkOrderController(WorkFlowServiceImpl workFlowService,WorkOrderServiceImpl workOrderService
                                        ) {
        this.workOrderService = workOrderService;
        this.workFlowService = workFlowService;
    }

    @ApiOperation(value = "APP查询工单流程", notes = "APP查询工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows")
    public ResultObject<WorkFlowBeanList> appQueryWorkFlows(HttpServletResponse response,
                                                         @ApiParam(value="workOrderId")@RequestParam(required=false)Long workOrderId) {

        log.info("app side queryWorkFlows workOrderId ={}",workOrderId);
        if (null == workOrderId){
            response.setStatus(MyErrorMap.e400.getCode());
            return new ResultObject<>(400002,"工单号不能为空",null);
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(workOrderId);
        } catch (Exception ex) {
            StringUtil.throw400Exp(response, "400006:"+ex.getMessage());
            return null;
        }

        if (null == workOrder){
            StringUtil.throw400Exp(response, "400003:工单不存在");
            return null;
        }

        WorkFlowBean workFlowZero = new WorkFlowBean();
        workFlowZero.setStatus(WorkOrderStatusType.EDITING.getCode());
        workFlowZero.setCreateTime(workOrder.getCreateTime());
        workFlowZero.setUpdateTime(workOrder.getCreateTime());
        workFlowZero.setWorkOrderId(workOrderId);
        workFlowZero.setComments("提交申请");
        workFlowZero.setId(0L);
        workFlowZero.setOperator(workOrder.getReceiverId());

        List<WorkFlow> list;
        try {
            list = workFlowService.selectByWorkOrderId(workOrderId,null);
        }catch (Exception e) {
            response.setStatus(MyErrorMap.e400.getCode());
            return new ResultObject<>(400006,e.getMessage(),null);
        }

        List<WorkFlowBean> result = new ArrayList<>();

        for (WorkFlow a : list) {
            WorkFlowBean b = new WorkFlowBean();
            BeanUtils.copyProperties(a, b);
            b.setOperator(a.getUpdatedBy());
            result.add(b);
        }
        result.add(workFlowZero);
        WorkFlowBeanList retResult = fillFlowBeans(workOrder, result);

        response.setStatus(MyErrorMap.e200.getCode());

        log.info("app side queryWorkFlows success");
        return new ResultObject<>(200,"success",retResult);

    }

    @ApiOperation(value = "APP创建工单处理流程信息", notes = "创建工单处理信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows")
    public IdResponseData appCreateWorkFlow(HttpServletResponse response,
                                         @RequestBody WorkFlowBodyBean data) {

        log.info("app side create WorkFlow enter : param {}", JSON.toJSONString(data));
        IdResponseData result = new IdResponseData();

        Long workOrderId = data.getWorkOrderId();
        Integer nextStatus = data.getStatus();
        String comments = data.getComments();
        String operator = data.getOperator();
        String expressNo = data.getExpressNo();

        if (null == workOrderId || 0 == workOrderId
        ) {
            StringUtil.throw400Exp(response, "400002:工单号不能为空");
            return result;
        }

        if (null == operator || operator.isEmpty()) {
            StringUtil.throw400Exp(response, "400003: 操作员不能为空");
            return result;
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(workOrderId);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400004:工单号不存在");
            return result;
        }

        Integer orderStatus = workOrder.getStatus();
        if (!WorkOrderStatusType.ACCEPTED.getCode().equals(orderStatus)
                && !WorkOrderStatusType.HANDLING.getCode().equals(orderStatus)) {
            String msg;
            if (WorkOrderStatusType.CLOSED.getCode().equals(orderStatus)){
                msg = "工单已经处理完成";
            }else{
                msg = "工单必须审核通过才能进行处理";
            }
            StringUtil.throw400Exp(response, "400007:"+msg);
            return result;
        }

        if (null == nextStatus || WorkOrderStatusType.Int2String(nextStatus).isEmpty() ||
            !WorkOrderStatusType.HANDLING.getCode().equals(nextStatus)) {
            StringUtil.throw400Exp(response, "400005:状态码错误");
            return result;
        }

        if (null != expressNo){
            workOrder.setExpressNo(expressNo);
        }

        WorkFlow workFlow = new WorkFlow();

        workFlow.setWorkOrderId(workOrderId);
        workFlow.setUpdatedBy(operator);

        workFlow.setStatus(nextStatus);

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }
        workFlow.setCreateTime(new Date());
        workFlow.setUpdateTime(new Date());
        if (!operator.isEmpty()) {
            workFlow.setCreatedBy(operator);
        }

        try {
            result.id = workFlowService.insert(workFlow);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400006:Failed to create work_flow");
            return result;
        }

        if (!workFlow.getStatus().equals(workOrder.getStatus())) {
            workOrder.setStatus(workFlow.getStatus());
            workOrder.setUpdateTime(new Date());
            try {
                workOrderService.update(workOrder);
            }catch (Exception e) {
                StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                return null;
            }
        }

        response.setStatus(MyErrorMap.e201.getCode());
        log.info("create WorkFlow {} and update workOrder {} success ", workFlow.getId().toString(),workOrder.getId().toString());
        return result;

    }


    @ApiOperation(value = "APP创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdResponseData appCreateWorkOrder(HttpServletResponse response,
                                          //@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                          @RequestBody CustomerWorkOrderBean data) {

        log.info("app side createWorkOrder: {}" , JSON.toJSONString(data));
        IdResponseData result = new IdResponseData();

        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        Integer typeId = data.getTypeId();
        Long merchantId = data.getMerchantId();
        Integer num = data.getNum();
        String iAppId = data.getiAppId();
        String tAppId = data.gettAppId();

        if (null == orderId || orderId.isEmpty() ) {
            StringUtil.throw400Exp(response, "400002:所属订单不能空缺");
            return result;
        }
        if (null == iAppId || iAppId.isEmpty() ) {
            StringUtil.throw400Exp(response, "400007:iAppId不能空缺");
            return result;
        }

        String configIAppIds = GuanAiTongConfig.getConfigGatIAppId();
        boolean isGat = false;
        if (null != configIAppIds && !configIAppIds.isEmpty() && configIAppIds.equals(iAppId)) {
            isGat = true;
        }

        if (isGat) {//GuanAiTong order
            if (null == tAppId || tAppId.isEmpty() ) {
                StringUtil.throw400Exp(response, "400007: 关爱通AppId不能空缺");
                return result;
            }
        }
        if (null == customer || customer.isEmpty() ) {
            StringUtil.throw400Exp(response, "400003:客户不能空缺");
            return result;
        }
        if (null == merchantId) {
            StringUtil.throw400Exp(response, "400004:merchantId不能空缺");
            return result;
        }

        if (null == typeId || 0 == typeId ||
                null == title || title.isEmpty()) {
            StringUtil.throw400Exp(response, "400002:工单标题, 工单类型, 所属订单不能空缺");
            return result;
        }

        if (WorkOrderType.Int2String(typeId).isEmpty()) {
            StringUtil.throw400Exp(response, "400005:工单类型错误");
            return result;
        }

        WorkOrder workOrder = new WorkOrder();

        WorkOrder selectedWO = null;
        try {
            selectedWO = workOrderService.getValidNumOfOrder(customer, orderId);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400009:"+e.getMessage());
            return result;
        }

        if (null == selectedWO) {//totally new order
            log.info("准备新建工单, 子订单号= " + orderId);
            JSONObject json;
            try {
                json = workOrderService.getOrderInfo(customer, orderId, merchantId);
            }catch (Exception e) {
                StringUtil.throw400Exp(response, "400007:"+e.getMessage());
                return result;
            }
            if (null == json) {
                StringUtil.throw400Exp(response, "400007: searchOrder失败");
                return result;
            }

            Integer parentOrderId = json.getInteger("id");
            if (null == parentOrderId) {
                StringUtil.throw400Exp(response, "400008: searchOrder, 获取id失败");
                return result;
            } else {
                workOrder.setParentOrderId(parentOrderId);
            }

            String paymentNo = json.getString("paymentNo");
            if (null == paymentNo) {
                log.info("searchOrder info: paymentNo is null");
            } else {
                workOrder.setTradeNo(paymentNo);
            }

            Float salePrice = json.getFloat("salePrice");
            if (null == salePrice) {
                log.info("searchOrder info: salePrice is null");
            } else {
                workOrder.setSalePrice(salePrice);
            }

            Integer orderNum = json.getInteger("num");
            if (null == orderNum) {
                log.error("searchOrder info: num is null");
                StringUtil.throw400Exp(response, "400008: searchOrder info: num is null");
                return result;
            } else {
                workOrder.setOrderGoodsNum(orderNum);
            }

            String mobile = json.getString("mobile");
            if (null != mobile) {
                workOrder.setReceiverPhone(mobile);
            }
            String receiverName = json.getString("receiverName");
            if (null != receiverName) {
                workOrder.setReceiverName(receiverName);
            }
            Float fare = json.getFloat("servFee");
            if (null != fare) {
                workOrder.setFare(fare);
            }
            Integer paymentAmount = json.getInteger("paymentAmount");
            if (null != paymentAmount) {
                workOrder.setPaymentAmount(paymentAmount);
            }
        } else {
            if (0 >= selectedWO.getReturnedNum() || num > selectedWO.getReturnedNum()) {
                StringUtil.throw400Exp(response, "400006:所属订单退货数量已满");
                return result;
            }

            workOrder.setFare(selectedWO.getFare());
            workOrder.setParentOrderId(selectedWO.getParentOrderId());
            workOrder.setPaymentAmount(selectedWO.getPaymentAmount());
            workOrder.setTradeNo(selectedWO.getTradeNo());
            workOrder.setSalePrice(selectedWO.getSalePrice());
            workOrder.setOrderGoodsNum(selectedWO.getOrderGoodsNum());
            workOrder.setReceiverPhone(selectedWO.getReceiverPhone());
            workOrder.setReceiverName(selectedWO.getReceiverName());

        }

        workOrder.setiAppId(iAppId);
        if (null != tAppId && !tAppId.isEmpty()) {
            workOrder.settAppId(tAppId);
        }
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setOrderId(orderId);
        workOrder.setReturnedNum(num);
        if (WorkOrderType.EXCHANGE.getCode().equals(typeId)) {
            workOrder.setRefundAmount(0.00f);
        }else {
            BigDecimal decSalePrice = new BigDecimal(workOrder.getSalePrice());
            BigDecimal decNum = new BigDecimal(num);
            workOrder.setRefundAmount(decSalePrice.multiply(decNum).floatValue());
        }
        workOrder.setTypeId(typeId);
        workOrder.setMerchantId(merchantId);
        workOrder.setStatus(WorkOrderStatusType.EDITING.getCode());
        workOrder.setReceiverId(customer);
        workOrder.setCreateTime(new Date());
        workOrder.setUpdateTime(new Date());


        //String username = null;//JwtTokenUtil.getUsername(authentication);
        //if (null != username) {
        //    workOrder.setCreatedBy(username);
        //    workOrder.setUpdatedBy(username);
        //}

        try {
            result.id = workOrderService.insert(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response,"400006:"+ ex.getMessage());
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400008:Failed to create work_order");
        }
        response.setStatus(MyErrorMap.e201.getCode());

        log.info("createWorkOrder success, id = " +result.id.toString());
        return result;

    }

    @ApiOperation(value = "APP更新工单信息", notes = "更新工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_orders/{id}")
    public IdResponseData appUpdateWorkOrder(HttpServletResponse response,
                                                    //@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                                    @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                                    @RequestBody CustomerWorkOrderBean data) {

        log.info("app side updateWorkOrder: id={}, param: {}",id, JSON.toJSONString(data));

        IdResponseData result = new IdResponseData();
        WorkOrder workOrder;
        //String username = null; //JwtTokenUtil.getUsername(authentication);
        //String orderId = data.getOrderId();
        String title = data.getTitle();
        //String description = data.getDescription();
        //String customer = data.getCustomer();
        //Integer typeId = data.getTypeId();
        //Long merchantId = data.getMerchantId();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        try {
            workOrder = workOrderService.selectById(id);
        } catch (Exception ex) {
            StringUtil.throw400Exp(response, "400006:"+ex.getMessage());
            return result;
        }
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:工单不存在");
            return result;
        }
        if (null != title && !title.isEmpty() ) {
            workOrder.setTitle(title);
        }
        /*
        if (null != typeId) {
                if (WorkOrderType.Int2String(typeId).isEmpty()) {
                    StringUtil.throw400Exp(response, "400002:工单类型错误");
                }

                workOrder.setTypeId((long)typeId);
        }

        if (null != orderId) {
            workOrder.setOrderId(orderId);
        }
        if (null != description && !description.isEmpty()) {
            workOrder.setDescription(description);
        }

        if (null != customer && !customer.isEmpty()) {
            workOrder.setReceiverId(customer);
        }

        if (null != merchantId) {
            workOrder.setMerchantId(merchantId);
        }
        */
        workOrder.setUpdateTime(new Date());

        try {
            workOrderService.update(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, "400006:"+ex.getMessage());
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkOrder done");
        return result;
    }

    private CustomerQueryWorkOrderBean fillbeanByOrderInfo(WorkOrder workOrder) throws Exception{
        JSONObject json;
        try {
            json = workOrderService.getOrderInfo(workOrder.getReceiverId(), workOrder.getOrderId(), workOrder.getMerchantId());
        }catch (Exception e) {
            throw new Exception(e);
        }
        if (null == json) {
            return null;
        }

        CustomerQueryWorkOrderBean bean = new CustomerQueryWorkOrderBean();

        String name = json.getString("name");
        if (null == name) {
            log.info("searchOrder info: name is null");
        } else {
            bean.setName(name);
        }

        String image = json.getString("image");
        if (null == image) {
            log.info("searchOrder info: image is null");
        } else {
            bean.setImage(image);
        }

        Float unitPrice = json.getFloat("unitPrice");
        if (null == unitPrice) {
            log.info("searchOrder info: unitPrice is null");
        } else {
            bean.setUnitPrice(unitPrice);
        }

        return bean;
    }


    @ApiOperation(value = "APP条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders")
    public PageInfo<CustomerQueryWorkOrderBean> appQueryWorkOrders(HttpServletResponse response,
                                                   //@RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                   @ApiParam(value="页码")@RequestParam(required=false) Integer pageIndex,
                                                   @ApiParam(value="每页记录数")@RequestParam(required=false) Integer pageSize,
                                                   @ApiParam(value="订单所属客户")@RequestParam(required=false) String customer,
                                                   @ApiParam(value="订单ID")@RequestParam(required=false) String orderId) {

/*
        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }
*/

        int index = (null == pageIndex || 0 >= pageIndex)?1:pageIndex;
        int limit = (null == pageSize || 0>= pageSize)?10:pageSize;

        if (null != customer) {
            customer = customer.trim();
        }
        if (null != orderId) {
            orderId = orderId.trim();
        }

        PageInfo<WorkOrder> pages;
        try {
            pages = workOrderService.selectPage(index, limit,
                    "id", "DESC", null, null,customer,
                    null, null, orderId,
                    null, null, null,
                    null, null,
                    null,null);
        }catch(Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        List<CustomerQueryWorkOrderBean> list = new ArrayList<>();

        if ((index -1) * limit <= pages.getTotal()) {
            for (WorkOrder a : pages.getRows()) {
                CustomerQueryWorkOrderBean b = new CustomerQueryWorkOrderBean();
                BeanUtils.copyProperties(a, b);
                CustomerQueryWorkOrderBean remoteBean = null;
                try {
                    remoteBean = fillbeanByOrderInfo(a);
                }catch (Exception e){
                    StringUtil.throw400Exp(response, "400007:"+e.getMessage());
                    return null;
                }
                if (null != remoteBean){
                    b.setImage(remoteBean.getImage());
                    b.setName(remoteBean.getName());
                    b.setUnitPrice(remoteBean.getUnitPrice());
                }

                if (WorkOrderStatusType.ACCEPTED.getCode().equals(b.getStatus())){
                    List<WorkFlow> workFlows;
                    try {
                        workFlows = workFlowService.selectByWorkOrderId(b.getId(), b.getStatus());
                    }catch (Exception e){
                        StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                        return null;
                    }
                    if (null != workFlows && 0 < workFlows.size()){
                        b.setDescription(workFlows.get(0).getComments());
                    }
                }
                list.add(b);
            }
        }
        PageInfo<CustomerQueryWorkOrderBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),index, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }


    @ApiOperation(value = "APP查询订单可退数量", notes = "查询订单可退数量")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("orders/validNum")
    public ValidNumResponseData appQueryOrderValidNum(HttpServletResponse response,
                                                   @ApiParam(value="订单所属客户",required=true)@RequestParam String customer,
                                                   @ApiParam(value="订单ID",required=true)@RequestParam String orderId,
                                                   @ApiParam(value="merchantId",required=true)@RequestParam Long merchantId
                                                   ) {

        ValidNumResponseData result = new ValidNumResponseData();

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.getValidNumOfOrder(customer, orderId);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return result;
        }
        response.setStatus(MyErrorMap.e200.getCode());
        if (null != workOrder) {
            result.validNum = (0>= workOrder.getReturnedNum()?0:workOrder.getReturnedNum());
            log.info("get valid number {}",result.validNum);
            return result;
        }

        JSONObject json;
        try {
            json = workOrderService.getOrderInfo(customer, orderId, merchantId);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return result;
        }
        if (null == json) {
            StringUtil.throw400Exp(response, "400003:找不到订单信息");
            return result;
        }

        Integer orderNum = json.getInteger("num");
        if (null == orderNum) {
            log.info("get order info err: num is null");
            StringUtil.throw400Exp(response, "400004:订单信息中找不到num");
            return result;
        } else {
            result.validNum = orderNum;
        }
        return result;

    }

    @ApiOperation(value = "APP查询订单售后详情", notes = "APP查询订单售后详情")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("orders/allRefunds")
    public ResultObject<ParentOrderRefundData> appQueryParentOrderRefund(HttpServletResponse response,
                                                      @ApiParam(value="子订单ID",required=true)@RequestParam String orderId
    ) {

        ParentOrderRefundData result = new ParentOrderRefundData();
        response.setStatus(MyErrorMap.e400.getCode());

        if (null == orderId || orderId.isEmpty()){
            return new ResultObject<>(400002,"子订单号不可省略",null);
        }

        JSONObject json;
        try {
            json = workOrderService.getOrderInfo(null, orderId, 0L);
        }catch (Exception e) {
            return new ResultObject<>(400007,e.getMessage(),null);
        }
        if (null == json) {
            return new ResultObject<>(400007,"searchOrder失败",null);
        }

        Integer parentOrderId = json.getInteger("id");
        if (null == parentOrderId) {
            return new ResultObject<>(400008,"searchOrder, 获取id失败",null);
        } else {
            result.parentOrderId = parentOrderId;
        }

        Integer paymentAmount = json.getInteger("paymentAmount");
        if (null == paymentAmount) {
            return new ResultObject<>(400008,"searchOrder,  paymentAmount is null",null);
        } else {
            result.paymentAmount = paymentAmount;
        }

        List<WorkOrder> workOrders;
        try {
            workOrders = workOrderService.selectByParentOrderId(parentOrderId);
        }catch (Exception e) {
            return new ResultObject<>(400006,e.getMessage(),null);
        }

        List<OrderRefundBean> list = new ArrayList<>();
        result.result = list;
        response.setStatus(MyErrorMap.e200.getCode());
        if (null == workOrders || 0 == workOrders.size()){
            result.realRefundAmount = 0;
            result.refundAmount = 0;
            return new ResultObject<>(200,"success",result);
        }

        for(WorkOrder a : workOrders){
            OrderRefundBean b = new OrderRefundBean();
            b.setOrderGoodsNum(a.getOrderGoodsNum());
            b.setOrderId(a.getOrderId());
            b.setRefundAmount(a.getRefundAmount());
            b.setReturnedNum(a.getReturnedNum());
            b.setRealRefundAmount(a.getGuanaitongRefundAmount());
            BigDecimal dec100f = new BigDecimal("100");
            if (null != b.getRefundAmount()){
                BigDecimal decRefundAmount = new BigDecimal(b.getRefundAmount());
                result.refundAmount += decRefundAmount.multiply(dec100f).intValue();
            }
            if (null != b.getRealRefundAmount()){
                BigDecimal decRefundAmount = new BigDecimal(b.getRealRefundAmount());
                result.realRefundAmount += decRefundAmount.multiply(dec100f).intValue();//(int)(b.getRealRefundAmount()*100);
            }
            list.add(b);
        }

        return new ResultObject<>(200,"success",result);

    }
}
