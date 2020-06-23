package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.constants.WorkOrderType;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.feign.IAoYiClient;
import com.fengchao.workorders.entity.*;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Clark
 * */
@Slf4j
@Api(tags="CustomerWorkOrderAPI", description = "客户工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/customers/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CustomerWorkOrderController {

    //private static Logger log = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;
    private WorkFlowServiceImpl workFlowService;
    private IAoYiClient aoYiClient;

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

        @ApiModelProperty(value="申请退款金额", example="1.1")
        public Float refundAmount;

        @ApiModelProperty(value="实际退款金额", example="1.1")
        public Float realRefundAmount;
        @ApiModelProperty(value="商户ID", example="111")
        public Long merchantId;

        @ApiModelProperty(value="工单标题", example="退货000011",required=true)
        public String title;

        @ApiModelProperty(value="工单描述", example="退货000011")
        public String description;

        @ApiModelProperty(value="客户ID", example="123")
        public String receiverId;

        @ApiModelProperty(value="客户名称", example="李四")
        public String receiverName;

        @ApiModelProperty(value="客户电话", example="13345678901")
        public String receiverPhone;

        @ApiModelProperty(value="工单状态码", example="1")
        public Integer status;

        @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11")
        public LocalDateTime updateTime;

        @ApiModelProperty(value="退款完成时间", example="2019-06-16 11:11:11")
        public LocalDateTime refundTime;

        @ApiModelProperty(value="快递单号", example="2019111111")
        public String expressNo;

        @ApiModelProperty(value="工单类型ID", example="1")
        public Integer typeId;

        @ApiModelProperty(value = "流程信息List", example = " ")
        public List<WorkFlowBean> result;

    }

    private WorkFlowBeanList fillFlowBeans(WorkOrder a, List<WorkFlowBean> list){
        WorkFlowBeanList b = new WorkFlowBeanList();
        if(null == a){
            return b;
        }
        b.result = list;
        b.description = a.getDescription();
        b.orderId = a.getOrderId();
        b.expressNo = a.getExpressNo();
        b.iAppId = a.getIAppId();
        b.merchantId = a.getMerchantId();
        b.realRefundAmount = a.getGuanaitongRefundAmount();
        b.receiverId = a.getReceiverId();
        b.receiverName = a.getReceiverName();
        b.receiverPhone = a.getReceiverPhone();
        b.refundAmount = a.getRefundAmount();
        b.returnedNum = a.getReturnedNum();
        b.refundTime = a.getRefundTime();
        WorkOrderStatusType workOrderStatusType = WorkOrderStatusType.checkByName(String.valueOf(a.getStatus()));
        if (null != workOrderStatusType) {
            b.status = workOrderStatusType.getCode();
        }
        b.tAppId = a.getTAppId();
        b.title = a.getTitle();
        b.updateTime = a.getUpdateTime();
        WorkOrderType workOrderType = WorkOrderType.checkByName(String.valueOf(a.getTypeId()));
        if(null != workOrderType) {
            b.typeId = workOrderType.getCode();
        }
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
    public CustomerWorkOrderController(WorkFlowServiceImpl workFlowService,
                                       IAoYiClient aoYiClient,
                                       WorkOrderServiceImpl workOrderService
                                        ) {
        this.workOrderService = workOrderService;
        this.workFlowService = workFlowService;
        this.aoYiClient = aoYiClient;
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

        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (null == workOrder){
            throw new MyException(MyErrorEnum.WORK_ORDER_NO_NOT_FOUND);
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
            list = workFlowService.selectByWorkOrderIdExcludeReserved(workOrderId);
        }catch (Exception e) {
            response.setStatus(MyErrorMap.e400.getCode());
            return new ResultObject<>(400006,e.getMessage(),null);
        }

        List<WorkFlowBean> result = new ArrayList<>();

        for (WorkFlow a : list) {
            WorkFlowBean b = new WorkFlowBean();
            BeanUtils.copyProperties(a, b);
            b.setOperator(a.getCreatedBy());
            result.add(b);
        }
        result.add(workFlowZero);
        WorkFlowBeanList retResult = fillFlowBeans(workOrder, result);

        response.setStatus(MyErrorMap.e200.getCode());

        log.info("app side queryWorkFlows success");
        return new ResultObject<>(200,"success",retResult);

    }

    @ApiOperation(value = "APP创建工单处理流程信息", notes = "APP创建工单处理流程信息,添加退货物流信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows")
    public IdResponseData appCreateWorkFlow(HttpServletResponse response,
                                         @RequestBody WorkFlowBodyBean data) {

        log.info("app side create WorkFlow enter : param {}", JSON.toJSONString(data));
        IdResponseData result = new IdResponseData();
        if(null == data){
            return result;
        }

        Long workOrderId = data.getWorkOrderId();
        Integer nextStatusCode = data.getStatus();
        WorkOrderStatusType nextStatus = WorkOrderStatusType.checkByCode(nextStatusCode);
        String comments = data.getComments();
        String operator = data.getOperator();
        String expressNo = data.getExpressNo();

        data.checkParameters();

        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (null == workOrder) {
            throw new MyException(MyErrorEnum.WORK_ORDER_NO_NOT_FOUND);
        }

        WorkOrderStatusType orderStatus = workOrder.getStatus();
        log.info("orderStatus= {}",orderStatus);
        if (WorkOrderStatusType.ACCEPTED !=orderStatus
                && WorkOrderStatusType.HANDLING != orderStatus) {
            if (WorkOrderStatusType.isClosedStatus(orderStatus)) {
                throw new MyException(MyErrorEnum.WORK_ORDER_HAS_CLOSED);
            } else {
                throw new MyException(MyErrorEnum.WORK_ORDER_STATUS_NOT_ACCEPT);
            }

        }

        if (!WorkOrderStatusType.HANDLING.equals(nextStatus)) {
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_STATUS_INVALID);
        }

        if (null != expressNo) {
            workOrder.setExpressNo(expressNo);
        }

        WorkFlow workFlow = new WorkFlow();
        workFlow.setWorkOrderId(workOrderId);
        workFlow.setStatus(nextStatus);

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
            if (Constant.YI_YA_TONG_MERCHANT_ID == workOrder.getMerchantId()) {
                //怡亚通的订单,需要发送物流信息
                workOrderService.sendExpressInfo(workOrder, comments);
            }
        }

        workFlow.setCreatedBy(operator);
        workFlowService.save(workFlow);
        result.id = workFlow.getId();

        if (!workFlow.getStatus().equals(workOrder.getStatus())) {
            workOrder.setStatus(workFlow.getStatus());
            workOrder.setUpdateTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            workOrderService.updateById(workOrder);
        }

        response.setStatus(MyErrorMap.e201.getCode());
        log.info("create WorkFlow {} and update workOrder {} success ", workFlow.getId().toString(), workOrder.getId().toString());
        return result;

    }


    @ApiOperation(value = "APP创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdResponseData
    appCreateWorkOrder(HttpServletResponse response,
                       @RequestBody CustomerWorkOrderBean data) {

        log.info("app side createWorkOrder: {}", JSON.toJSONString(data));
        IdResponseData result = new IdResponseData();

        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        Integer typeIdCode = data.getTypeId();
        WorkOrderType typeId = WorkOrderType.checkByCode(typeIdCode);
        Long merchantId = data.getMerchantId();
        Integer num = data.getNum();
        String iAppId = data.getIAppId();
        String tAppId = data.getTAppId();

        data.checkFields();
        String configIAppIds = GuanAiTongConfig.getConfigGatIAppId();
        boolean isGat = false;
        if (null != configIAppIds && !configIAppIds.isEmpty() && configIAppIds.equals(iAppId)) {
            isGat = true;
        }

        if (isGat) {
            //GuanAiTong order
            if (null == tAppId || tAppId.isEmpty()) {
                throw new MyException(MyErrorEnum.PARAM_GAT_APP_ID_BLANK);
            }
        }

        BigDecimal decCouponDiscount = new BigDecimal(0);
        WorkOrder workOrder = new WorkOrder();

        WorkOrder selectedWO = workOrderService.getValidNumOfOrder(customer, orderId);

        boolean isFull = (null != selectedWO) &&
                (0 >= selectedWO.getReturnedNum() || num > selectedWO.getReturnedNum());
        if (isFull) {
            throw new MyException(MyErrorEnum.REFUND_COUNT_OVERFLOW);
        }

        log.info("准备新建工单, 子订单号= {}" ,orderId);
        JSONObject json= workOrderService.getOrderInfo(customer, orderId, merchantId);
        if (null == json) {
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_FAILED);
        }

        Integer parentOrderId = json.getInteger("id");
        if (null == parentOrderId) {
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_ID_BLANK);
        } else {
            workOrder.setParentOrderId(parentOrderId);
        }

        String paymentNo = json.getString("paymentNo");
        if (null == paymentNo) {
            log.info("searchOrder info: paymentNo is null");
        } else {
            workOrder.setTradeNo(paymentNo);
        }

        Float unitPrice = json.getFloat("unitPrice");
        Integer skuCouponDiscount = json.getInteger("skuCouponDiscount");
        if (null == unitPrice) {
            log.info("searchOrder info: salePrice is null");
        } else {
            workOrder.setSalePrice(unitPrice);
        }
        if (null != skuCouponDiscount) {
            Float floatDiscount = Float.valueOf(FeeUtil.Fen2Yuan(String.valueOf(skuCouponDiscount)));
            //log.info("== floatDiscount = {}", floatDiscount);
            BigDecimal decRealCouponDiscount = new BigDecimal(floatDiscount);
            decCouponDiscount = decCouponDiscount.add(decRealCouponDiscount);
            //log.info("=== decCouponDiscount = {}", decCouponDiscount);
        } else {
            log.warn("订单中没找到 skuCouponDiscount");
        }
        Integer orderNum = json.getInteger("num");
        if (null == orderNum) {
            log.error("searchOrder info: num is null");
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_NUMBER_BLANK);
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

        workOrder.setIAppId(iAppId);
        if (null != tAppId && !tAppId.isEmpty()) {
            workOrder.setTAppId(tAppId);
        }
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setOrderId(orderId);
        workOrder.setReturnedNum(num);
        /*对怡亚通订单需要特别处理
        * merchantId = 4 为怡亚通订单
        * 如果subStatus = 1, 调用postRefundOnly 未发货退款
        *    subStatus = 2|3, postRefundReturn 退货退款
        * 其他值, 生成工单失败
        *
        * 返回成功，需要将serviceSn 计入 工单refundNo
        * 返回失败，生成工单失败
        * */

        Long orderMerchantId = json.getLong("merchantId");
        Integer subStatus = json.getInteger("subStatus");
        String thirdOrderSn = json.getString("thirdOrderSn");
        String skuId = json.getString("skuId");
        boolean  needYiYaTongHandle = canSendYiYaTong(orderMerchantId,subStatus,thirdOrderSn,skuId);
        if (null != orderMerchantId &&
                Constant.YI_YA_TONG_MERCHANT_ID == orderMerchantId &&
                needYiYaTongHandle) {


            AoYiRefundResponseBean bean = workOrderService.getYiYaTongRefundNo(title, subStatus, thirdOrderSn, skuId);
            if (null != bean) {
                workOrder.setRefundNo(bean.getServiceSn());
                //复用该字段保存星链子订单sn
                workOrder.setGuanaitongTradeNo(bean.getOrderSn());
                workOrder.setMerchantId(orderMerchantId);
            } else {
                throw new MyException(MyErrorEnum.API_SEARCH_ORDER_STATUS_INVALID);
            }

        }
        /*对怡亚通订单需要特别处理 end*/
        if (WorkOrderType.EXCHANGE.getCode().equals(typeId)) {
            workOrder.setRefundAmount(0.00f);
        } else {
            BigDecimal decUnitPrice = new BigDecimal(workOrder.getSalePrice());
            BigDecimal decNum = new BigDecimal(num);
            BigDecimal decRefundAmount = decUnitPrice.multiply(decNum);
            //log.info("=== decRefundAmount = {}", decRefundAmount);
            decRefundAmount = decRefundAmount.subtract(decCouponDiscount);
            //log.info("=== decRefundAmount = {}", decRefundAmount);
            workOrder.setRefundAmount(decRefundAmount.floatValue());
        }
        workOrder.setTypeId(typeId);
        workOrder.setMerchantId(merchantId);
        workOrder.setStatus(WorkOrderStatusType.EDITING);
        workOrder.setReceiverId(customer);


        //String username = null;//JwtTokenUtil.getUsername(authentication);
        //if (null != username) {
        //    workOrder.setCreatedBy(username);
        //    workOrder.setUpdatedBy(username);
        //}

        workOrderService.save(workOrder);
        result.id = workOrder.getId();


        if (null != orderMerchantId &&
                Constant.YI_YA_TONG_MERCHANT_ID == orderMerchantId){

            //怡亚通需要记录处理流程
            WorkFlow workFlow = new WorkFlow();
            workFlow.setWorkOrderId(result.id);
            workFlow.setCreatedBy("怡亚通调用");
            workFlow.setStatus(WorkOrderStatusType.RESERVED);
            if (needYiYaTongHandle) {
                workFlow.setComments(WebSideWorkFlowStatusEnum.buildComments(WebSideWorkFlowStatusEnum.NOTIFY_PENDING,"等待回调通知"));
            }else{
                workFlow.setComments(WebSideWorkFlowStatusEnum.buildComments(WebSideWorkFlowStatusEnum.THIRD_SN_BLANK,"需要与怡亚通确认下单信息"));
            }

            try {
                workFlowService.save(workFlow);
                if (needYiYaTongHandle) {
                    log.info("发送请求到怡亚通后创建工作流 {}", JSON.toJSONString(workFlow));
                }else{
                    log.info("未发送请求到怡亚通,仅创建工作流 {}", JSON.toJSONString(workFlow));
                }
            }catch (Exception e){
                log.error("数据库操作异常 {}",e.getMessage(),e);
            }

        }
        response.setStatus(MyErrorMap.e201.getCode());

        log.info("createWorkOrder success {} ", JSON.toJSONString(workOrder));
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
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_ID_BLANK);
        }

        workOrder = workOrderService.getById(id);

        if (null == workOrder) {
            throw new MyException(MyErrorEnum.WORK_ORDER_NO_NOT_FOUND);
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
        workOrder.setUpdateTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        workOrderService.updateById(workOrder);

        result.id = workOrder.getId();
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkOrder done");
        return result;
    }

    private CustomerQueryWorkOrderBean fillbeanByOrderInfo(WorkOrder workOrder){
        JSONObject json = workOrderService.getOrderInfo(workOrder.getReceiverId(), workOrder.getOrderId(), workOrder.getMerchantId());
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
    public PageInfo<CustomerQueryWorkOrderBean>
    appQueryWorkOrders(HttpServletResponse response,
                       @ApiParam(value="页码")@RequestParam(defaultValue = "1") Integer pageIndex,
                       @ApiParam(value="每页记录数")@RequestParam(defaultValue = "10") Integer pageSize,
                       @ApiParam(value="订单所属客户")@RequestParam(required=false) String customer,
                       @ApiParam(value="订单ID")@RequestParam(required=false) String orderId) {


        if (null != customer) {
            customer = customer.trim();
        }
        if (null != orderId) {
            orderId = orderId.trim();
        }

        PageInfo<WorkOrder> pages = workOrderService.selectPage(pageIndex, pageSize,
                     null, null,customer,
                    null, null, orderId,
                    null, null, null,
                    null, null,
                    null,null);


        List<CustomerQueryWorkOrderBean> list = new ArrayList<>();

        if ((pageIndex -1) * pageSize <= pages.getTotal()) {
            for (WorkOrder a : pages.getRows()) {
                CustomerQueryWorkOrderBean b = new CustomerQueryWorkOrderBean();
                BeanUtils.copyProperties(a, b);
                CustomerQueryWorkOrderBean remoteBean;

                remoteBean = fillbeanByOrderInfo(a);

                if (null != remoteBean){
                    b.setImage(remoteBean.getImage());
                    b.setName(remoteBean.getName());
                    b.setUnitPrice(remoteBean.getUnitPrice());
                }

                if (WorkOrderStatusType.ACCEPTED.getCode().equals(b.getStatus())){
                    List<WorkFlow> workFlows = workFlowService.selectByWorkOrderId(b.getId(), b.getStatus());

                    if (null != workFlows && 0 < workFlows.size()){
                        b.setDescription(workFlows.get(0).getComments());
                    }
                }
                list.add(b);
            }
        }
        PageInfo<CustomerQueryWorkOrderBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),pageIndex, list);

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

        WorkOrder workOrder= workOrderService.getValidNumOfOrder(customer, orderId);

        if (null != workOrder) {
            result.validNum = (0>= workOrder.getReturnedNum()?0:workOrder.getReturnedNum());
            log.info("get valid number {}",result.validNum);
            return result;
        }

        JSONObject json = workOrderService.getOrderInfo(customer, orderId, merchantId);
        if (null == json) {
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_FAILED);
        }

        Integer orderNum = json.getInteger("num");
        if (null == orderNum) {
            log.info("get order info err: num is null");
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_NUMBER_BLANK);
        } else {
            result.validNum = orderNum;
        }
        return result;

    }

    @ApiOperation(value = "APP查询订单售后详情", notes = "APP查询订单售后详情")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("orders/allRefunds")
    public ResultObject<ParentOrderRefundData>
    appQueryParentOrderRefund(@ApiParam(value="子订单ID")@RequestParam(required = false) String orderId) {

        ParentOrderRefundData result = new ParentOrderRefundData();

        if (null == orderId || orderId.isEmpty()){
            throw new MyException(MyErrorEnum.PARAM_ORDER_ID_BLANK);
        }

        JSONObject json = workOrderService.getOrderInfo(null, orderId, 0L);
        if (null == json) {
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_FAILED);
        }

        Integer parentOrderId = json.getInteger("id");
        if (null == parentOrderId) {
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_ID_BLANK);
        } else {
            result.parentOrderId = parentOrderId;
        }

        Integer paymentAmount = json.getInteger("paymentAmount");
        if (null == paymentAmount) {
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_FAILED," 返回缺失paymentAmount");
        } else {
            result.paymentAmount = paymentAmount;
        }

        List<WorkOrder> workOrders= workOrderService.selectByParentOrderId(parentOrderId);


        List<OrderRefundBean> list = new ArrayList<>();
        result.result = list;

        if (null == workOrders || 0 == workOrders.size()){
            result.realRefundAmount = 0;
            result.refundAmount = 0;
            return new ResultObject<>(result);
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

        return new ResultObject<>(result);

    }

    private boolean
    canSendYiYaTong(Long orderMerchantId,Integer subStatus,String thirdOrderSn,String skuId){

        if (null == orderMerchantId ||
             Constant.YI_YA_TONG_MERCHANT_ID != orderMerchantId){
            return false;
        }

        boolean result = true;
        if (null == subStatus) {
            log.error("所属订单缺失subStatus");
            result = false;
        }
        /*对怡亚通订单需要特别处理
         * merchantId = 4 为怡亚通订单
         * 如果subStatus = 1, 调用postRefundOnly 未发货退款
         *    subStatus = 2|3, postRefundReturn 退货退款
         * 其他值, 生成工单失败
         *
         * 返回成功，需要将serviceSn 计入 工单refundNo
         * 返回失败，生成工单失败
         * */
        if (1 != subStatus && 2 != subStatus && 3 != subStatus) {
            throw new RuntimeException("420004:所属订单子状态不符合退款要求");
        }

        if (null == thirdOrderSn || thirdOrderSn.isEmpty()) {
            log.error("所属订单缺失thirdOrderSn");
            result = false;
        }

        if (null == skuId || skuId.isEmpty()) {
            log.error("所属订单缺失skuId");
            result = false;
        }
        return result;
    }


}
