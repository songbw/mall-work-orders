package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.feign.IAggPayClient;
import com.fengchao.workorders.feign.VendorsServiceClient;
import com.fengchao.workorders.model.*;
//import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.rpc.VendorsRpcService;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
//import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Api(tags="WorkOrderAPI", description = "工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkOrderController {

    //private static Logger = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;
    private IAggPayClient aggPayClient;
    private WorkFlowServiceImpl workFlowService;
    private VendorsRpcService vendorsRpcService ;

    @ApiModel(value = "工单信息ID")
    private class IdData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }
/*
    @ApiModel(value = "工作流程信息列表")
    private class WorkFlowListData implements Serializable {
        @ApiModelProperty(value = "流程信息列表", example = "", required = true)
        public List<WorkFlowBean> list;

    }
*/
    @ApiModel(value = "退货统计")
    private class ReturnCount {
        @ApiModelProperty(value = "统计数", example = "100", required = true)
        public Integer count;

    }

    @ApiModel(value = "GuanAiTongNo")
    private class GuanAiTongNo {
        @ApiModelProperty(value = "统计数", example = "100", required = true)
        public String tradeNo;

    }

    @Autowired
    public WorkOrderController(IAggPayClient aggPayClient,
                               WorkFlowServiceImpl workFlowService,
                               WorkOrderServiceImpl workOrderService,
                               VendorsRpcService vendorsRpcService
                             ) {
        this.workOrderService = workOrderService;
        this.aggPayClient = aggPayClient;
        this.workFlowService = workFlowService;
        this.vendorsRpcService = vendorsRpcService ;
    }

    private Date getDateType(String timeStr, boolean isEnd) throws Exception{

        if (null == timeStr) {
            return null;
        }
        if (timeStr.isEmpty()) {
            return null;
        }

        String timeString = timeStr.trim();
        Date dateTime;

       if (10 > timeString.length()) {
                return null;
       }

       if (10 == timeString.length()) {
           if (isEnd) {
               timeString += " 23:59:59";
           }else {
               timeString += " 00:00:00";
           }
       }

       try {
           dateTime = StringUtil.String2Date(timeString);
       } catch (Exception ex) {
           log.error("timeString is wrong {}",ex.getMessage());
           throw new Exception(ex);
       }

       return dateTime;
    }

    /**
     * 根据查询结果更新工单记录
     */
    private void
    checkWorkOrderByAggPay(WorkOrder workOrder,List<AggPayRefundQueryBean> list){

        boolean isAllDone = true;
        int itemCount = 0;
        int itemOk = 0;
        int itemFailed = 0;
        String endTime = "1970-01-01 00:0:00";

        for(AggPayRefundQueryBean b: list){
            itemCount += 1;
            if (AggPayRefundStatusEnum.NEW.getCode().equals(b.getStatus()) ||
                    AggPayRefundStatusEnum.PENDING.getCode().equals(b.getStatus())){
                isAllDone = false;
                break;
            }else {
                if (AggPayRefundStatusEnum.SUCCESS.getCode().equals(b.getStatus())) {
                    itemOk += 1;
                    if (null != b.getTradeDate() && 0 < b.getTradeDate().compareTo(endTime)){
                        endTime = b.getCreateDate();
                    }
                } else {
                    itemFailed += 1;
                }
            }
        }

        if (isAllDone && (itemCount == itemOk || itemCount == itemFailed)) {
            if (itemCount == itemOk) {
                workOrder.setComments("聚合支付退款成功");
            }else {
                workOrder.setComments("聚合支付退款失败");
            }
            Long recordId = workOrder.getId();
            String comments = workOrder.getComments();
            try {
                workOrder = workOrderService.selectById(recordId);
                if (null != workOrder && !WorkOrderStatusType.CLOSED.getCode().equals(workOrder.getStatus())) {
                    workOrder.setStatus(WorkOrderStatusType.CLOSED.getCode());
                    workOrder.setRefundTime(StringUtil.String2Date(endTime));
                    workOrder.setComments(comments);
                    workOrderService.update(workOrder);
                }
            } catch (Exception e) {
                log.error("数据库操作异常 {}", e.getMessage(), e);
            }
        }
    }

    @ApiOperation(value = "获取指定工单信息", notes = "获取指定工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/{id}")
    public WorkOrderBean getWorkOrderById(HttpServletResponse response,
                                 @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                 @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        String functionDescription = "获取指定工单信息";
        WorkOrderBean bean = new WorkOrderBean();
        String username = JwtTokenUtil.getUsername(authentication);

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return bean;
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(id);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_order");
            return bean;
        }

        BeanUtils.copyProperties(workOrder, bean);
        if (null != workOrder.getGuanaitongRefundAmount()) {
            bean.setRealRefundAmount(workOrder.getGuanaitongRefundAmount());
        }

        String outRefundNo = workOrder.getGuanaitongTradeNo();
        if (null != outRefundNo
                && (WorkOrderStatusType.REFUNDING.getCode().equals(workOrder.getStatus()) ||
                WorkOrderStatusType.CLOSED.getCode().equals(workOrder.getStatus()))) {
            log.info("调用查询聚合支付退款状态接口 outRefundNo={}", outRefundNo);

            ResultMessage<List<AggPayRefundQueryBean>> aggPayResult;
            try {
                aggPayResult = aggPayClient.getAggPayRefund(outRefundNo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                aggPayResult = null;
            }
            log.info("查询聚合支付退款状态： response={}", JSON.toJSONString(aggPayResult));
            if (null == aggPayResult || null == aggPayResult.getCode() ||
                    200 != aggPayResult.getCode() || null == aggPayResult.getData()) {
                log.error("未找到聚合支付服务, 或调用查询聚合支付退款状态接口失败");
                response.setStatus(MyErrorMap.e200.getCode());
                return bean;
            }
            bean.setComments(JSON.toJSONString(aggPayResult.getData()));

            // 根据查询结果更新工单记录
            checkWorkOrderByAggPay(workOrder,aggPayResult.getData());
        }
        response.setStatus(MyErrorMap.e200.getCode());
        log.info("{} {}",functionDescription,JSON.toJSONString(bean));
        return bean;

    }

    @ApiOperation(value = "根据订单号列表获取工单信息", notes = "根据订单号列表获取工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("work_orders/byOrderList")
    public List<WorkOrderBean> getWorkOrderByOrderIdList(HttpServletResponse response,
                                          @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                          @ApiParam(value="idList",required=true) @Valid @RequestBody List<String> idList) {

        String functionDescription = "根据订单号列表获取工单信息";

        String username = JwtTokenUtil.getUsername(authentication);

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }

        if (null == idList || 0 == idList.size()) {
            StringUtil.throw400Exp(response, "400002: 订单号列表缺失");
            return null;
        }

        List<WorkOrder> workOrders;
        try {
            workOrders = workOrderService.selectByOrderIdList(idList);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == workOrders || 0 == workOrders.size()) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_order");
            return null;
        }

        List<WorkOrderBean> list = new ArrayList<>();
        for(WorkOrder w: workOrders) {
            WorkOrderBean bean = new WorkOrderBean();
            BeanUtils.copyProperties(w, bean);
            if (null != w.getGuanaitongRefundAmount()) {
                bean.setRealRefundAmount(w.getGuanaitongRefundAmount());
            }
            list.add(bean);
        }
        response.setStatus(MyErrorMap.e200.getCode());
        log.info("{} 查询到={} 条",functionDescription,list.size());
        return list;

    }


    @ApiOperation(value = "查询退款异常工单", notes = "查询退款异常工单")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/abnormalList")
    public PageInfo<WorkOrderBean> queryAbnormalList(HttpServletResponse response,
                                                   @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                   @RequestHeader(value = "merchant") Long merchantIdInHeader,
                                                   @ApiParam(value="页码")@RequestParam(required=false) Integer pageIndex,
                                                   @ApiParam(value="每页记录数")@RequestParam(required=false) Integer pageSize,
                                                   @ApiParam(value="iAppId")@RequestParam(required=false) String iAppId,
                                                   @ApiParam(value="订单ID")@RequestParam(required=false) String orderId,
                                                   @ApiParam(value="商户ID")@RequestParam(required=false) Long merchantId,
                                                   @ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                                                   @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd
                                                    ) {

        java.util.Date dateCreateTimeStart ;
        java.util.Date dateCreateTimeEnd ;

        int index = (null == pageIndex || 0 >= pageIndex)?1:pageIndex;
        int limit = (null == pageSize || 0>= pageSize)?10:pageSize;

        if (null == authentication) {
            log.info("can not get authentication");
        }
        if (null == merchantIdInHeader) {
            log.warn("can not find merchant in header");
            StringUtil.throw400Exp(response, "400002:merchant  is wrong");
            return null;
        }

        try {
            dateCreateTimeStart = getDateType(timeStart,false);
            dateCreateTimeEnd = getDateType(timeEnd,true);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400005:"+e.getMessage());
            return null;
        }

        Long merchant;
        if (0 != merchantIdInHeader) {
            merchant = merchantIdInHeader;
        } else {
            merchant = merchantId;
        }

        PageInfo<WorkOrder> pages;
        try {
            pages = workOrderService.selectAbnormalRefundList(index, limit,"id", "DESC",
                    iAppId, orderId, merchant,
                    dateCreateTimeStart, dateCreateTimeEnd);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }
        List<WorkOrderBean> list = new ArrayList<>();

        if ((index -1) * pages.getPageSize() <= pages.getTotal()) {
            List<WorkOrder> workOrders = batchQueryAggPay(pages.getRows());
            for (WorkOrder a : workOrders) {
                WorkOrderBean b = new WorkOrderBean();
                BeanUtils.copyProperties(a, b);

                list.add(b);
            }
        }
        PageInfo<WorkOrderBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),index, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }

    @ApiOperation(value = "条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/pages")
    public PageInfo<WorkOrderBean> queryWorkOrders(HttpServletResponse response,
                                                   @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                   @RequestHeader(value = "merchant") Long merchantIdInHeader,
                                                   @RequestHeader(value = "renter") String renterInHeader,
                                                   @ApiParam(value="页码")@RequestParam(required=false) Integer pageIndex,
                                                   @ApiParam(value="每页记录数")@RequestParam(required=false) Integer pageSize,
                                                   @ApiParam(value="标题")@RequestParam(required=false) String title,
                                                   @ApiParam(value="订单ID")@RequestParam(required=false) String orderId,
                                                   @ApiParam(value="客户ID")@RequestParam(required=false) String receiverId,
                                                   @ApiParam(value="客户电话")@RequestParam(required=false) String receiverPhone,
                                                   @ApiParam(value="iAppId")@RequestParam(required=false) String iAppId,
                                                   @ApiParam(value="客户名称")@RequestParam(required=false) String receiverName,
                                                   @ApiParam(value="工单类型ID")@RequestParam(required=false) Integer typeId,
                                                   @ApiParam(value="商户ID")@RequestParam(required=false) Long merchantId,
                                                   @ApiParam(value="退款完成开始日期")@RequestParam(required=false) String refundTimeStart,
                                                   @ApiParam(value="退款完成结束日期")@RequestParam(required=false) String refundTimeEnd,
                                                   @ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                                                   @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd,
                                                   @ApiParam(value="工单状态码")@RequestParam(required=false) Integer status,
                                                   @ApiParam(value="租户ID")@RequestParam(required=false) String renterId
                                                     ) {

        java.util.Date dateCreateTimeStart ;
        java.util.Date dateCreateTimeEnd ;
        java.util.Date dateRefundTimeStart ;
        java.util.Date dateRefundTimeEnd ;
        int index = (null == pageIndex || 0 >= pageIndex)?1:pageIndex;
        int limit = (null == pageSize || 0>= pageSize)?10:pageSize;
        //String username = JwtTokenUtil.getUsername(authentication);
        if (null == authentication) {
            log.info("can not get authentication");
        }
        if (null == merchantIdInHeader) {
            log.warn("can not find merchant in header");
            StringUtil.throw400Exp(response, "400002:merchant  is wrong");
            return null;
        }

        try {
            dateCreateTimeStart = getDateType(timeStart,false);
            dateCreateTimeEnd = getDateType(timeEnd,true);
            dateRefundTimeStart = getDateType(refundTimeStart,false);
            dateRefundTimeEnd = getDateType(refundTimeEnd,true);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400005:"+e.getMessage());
            return null;
        }

//        Long merchant;
//        if (0 != merchantIdInHeader) {
//            merchant = merchantIdInHeader;
//        } else {
//            merchant = merchantId;
//        }

        List<Long> merchantIds = null ;
        if ("0".equals(renterInHeader)) {
            // 平台管理员
            // 获取所有租户下的所有商户信息
            if (StringUtils.isNotBlank(iAppId)) {
                merchantIds = vendorsRpcService.queryMerhantListByAppId(iAppId) ;
            } else {
                if (StringUtils.isNotBlank(renterId)) {
                    merchantIds = vendorsRpcService.queryRenterMerhantList(renterId) ;
                } else {
                    merchantIds = vendorsRpcService.queryRenterMerhantList("") ;
                }
            }
            //  判断商户中是否存在merchantId
            if (merchantIds.contains(merchantId))  {
                merchantIds = null;
            }
        } else {
            // 租户
            if (merchantIdInHeader == 0) {
                // 获取当前租户下的所有商户信息
                if (StringUtils.isNotBlank(iAppId)) {
                    merchantIds = vendorsRpcService.queryMerhantListByAppId(iAppId) ;
                } else {
                    merchantIds = vendorsRpcService.queryRenterMerhantList(renterInHeader) ;
                }
            } else {
                // 租户的商户
                merchantIds = vendorsRpcService.queryRenterMerhantList(renterInHeader) ;
                if (merchantIds.contains(merchantIdInHeader)) {
                    merchantId = merchantIdInHeader ;
                }
            }
        }


        PageInfo<WorkOrder> pages;
        try {
            pages = workOrderService.selectPage(index, limit, "id", "DESC",iAppId,
                    title, receiverId, receiverName, receiverPhone, orderId, typeId, merchantId,
                    status, dateCreateTimeStart, dateCreateTimeEnd,dateRefundTimeStart, dateRefundTimeEnd, merchantIds);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }
        List<WorkOrderBean> list = new ArrayList<>();
        List<WorkOrder> workOrders = batchQueryAggPay(pages.getRows());
        if ((index -1) * pages.getPageSize() <= pages.getTotal()) {
            for (WorkOrder a : workOrders) {
                WorkOrderBean b = new WorkOrderBean();
                BeanUtils.copyProperties(a, b);
                if (null != a.getGuanaitongRefundAmount()) {
                    b.setRealRefundAmount(a.getGuanaitongRefundAmount());
                }

                list.add(b);
            }
        }
        PageInfo<WorkOrderBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),index, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }

    @ApiOperation(value = "创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdData createProfile(HttpServletResponse response,
                                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                            @RequestBody WorkOrderBodyBean data) {

        log.info("create WorkOrder param: {}",JSON.toJSONString(data));
        if (null == authentication) {
            log.info("can not get authentication");
        }
        IdData result = new IdData();

        //String username = JwtTokenUtil.getUsername(authentication);
        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String receiverId = data.getReceiverId();
        //String receiverPhone = data.getReceiverPhone();
        //String receiverName = data.getReceiverName();
        Long merchantId = data.getMerchantId();
        Integer typeId = data.getTypeId();
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
        if (null == receiverId || receiverId.isEmpty() ) {
            StringUtil.throw400Exp(response, "400003:客户不能空缺");
            return result;
        }
        if (null == merchantId) {
            StringUtil.throw400Exp(response, "400004:merchantId不能空缺");
            return result;
        }

        if (null == typeId || 0 == typeId ||
                null == title || title.isEmpty()
        ) {
            StringUtil.throw400Exp(response, "400002:工单标题, 工单类型, 所属订单不能空缺");
            return result;
        }

        if (WorkOrderType.Int2String(typeId).isEmpty()) {
            StringUtil.throw400Exp(response, "400005:工单类型错误");
            return result;
        }

        WorkOrder workOrder = new WorkOrder();

        WorkOrder selectedWO;
        try {
            selectedWO = workOrderService.getValidNumOfOrder(receiverId, orderId);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == selectedWO) {//totally new order
            log.info("there is not work order of this orderId: " + orderId);
            JSONObject json;
            try {
                json = workOrderService.getOrderInfo(receiverId, orderId, merchantId);
            }catch (Exception e) {
                StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                return null;
            }

            if (null == json) {
                StringUtil.throw400Exp(response, "400007:找不到订单信息");
                return result;
            }

            Integer parentOrderId = json.getInteger("id");
            if (null == parentOrderId) {
                StringUtil.throw400Exp(response, "400008: searchOrder, 获取id失败");
                return result;
            } else {
                workOrder.setParentOrderId(parentOrderId);
            }

            Float fare = json.getFloat("servFee");
            if (null != fare) {
                workOrder.setFare(fare);
            }
            Integer paymentAmount = json.getInteger("paymentAmount");
            {
                if (null != paymentAmount) {
                    workOrder.setPaymentAmount(paymentAmount);
                }
            }

            workOrder.setTradeNo(json.getString("paymentNo"));
            workOrder.setSalePrice(json.getFloat("salePrice"));
            workOrder.setOrderGoodsNum(json.getInteger("num"));
            workOrder.setReceiverPhone(json.getString("mobile"));
            workOrder.setReceiverName(json.getString("receiverName"));
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

        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setOrderId(orderId);
        workOrder.setReturnedNum(num);
        workOrder.setRefundAmount(num * workOrder.getSalePrice());
        workOrder.setTypeId(typeId);
        workOrder.setMerchantId(merchantId);
        workOrder.setStatus(WorkOrderStatusType.EDITING.getCode());
        workOrder.setReceiverId(receiverId);
        workOrder.setiAppId(iAppId);
        if (null != tAppId && !tAppId.isEmpty()) {
            workOrder.settAppId(tAppId);
        }

        try {
            result.id = workOrderService.insert(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400008:Failed to create work_order");
        }

        response.setStatus(MyErrorMap.e201.getCode());

        return result;
    }

    @ApiOperation(value = "更新工单信息", notes = "更新工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_orders/{id}")
    public IdData updateProfile(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                @RequestBody WorkOrderBodyBean data) {


        log.info("update WorkOrder param: {}",JSON.toJSONString(data));
        if (null == authentication) {
            log.info("updateProfile: there is not authentication");
        }
        IdData result = new IdData();
        //String username = JwtTokenUtil.getUsername(authentication);
        //String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        //String receiverId = data.getReceiverId();
        String receiverPhone = data.getReceiverPhone();
        String receiverName = data.getReceiverName();
        //Long merchantId = data.getMerchantId();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(id);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
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

            workOrder.setTypeId(typeId);
        }

        if (null != orderId) {
            workOrder.setOrderId(orderId);
        }
        */
        if (null != description && !description.isEmpty()) {
            workOrder.setDescription(description);
        }
/*
        if (null != receiverid && !receiverid.isEmpty()) {
            workOrder.setReceiverId(receiverid);
        }
*/
        if (null != receiverPhone && !receiverPhone.isEmpty()) {
            workOrder.setReceiverPhone(receiverPhone);
        }

        if (null != receiverName && !receiverName.isEmpty()) {
            workOrder.setReceiverName(receiverName);
        }
/*
        if (null != merchantId) {
            workOrder.setMerchantId(merchantId);
        }
*/

        try {
            workOrderService.update(workOrder);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkOrder done");
        return result;
    }

    @ApiOperation(value = "怡亚通退款申请回调", notes = "怡亚通退款申请回调，来自服务aoyi ")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("work_orders/aoyi/refund/status")
    public ResultMessage<String> callBackAoyiRefund(HttpServletResponse response,
                                @RequestBody AoYiRefundCallBackPostBean data) {

        String functionName = "怡亚通退款申请回调";
        log.info("{} {}",functionName,JSON.toJSONString(data));

        ResultMessage<String> failedResult = new ResultMessage<>(400,"failed",null);
        ResultMessage<String> successResult = new ResultMessage<>(200,"success",null);

        String aoyiRefundNo = data.getServiceSn();
        String aoyiRefundStatus = data.getNewStatus();
        String oldStatus = data.getOldStatus();

        if (null == aoyiRefundNo || aoyiRefundNo.isEmpty()){
            failedResult.setMessage("serviceSn 缺失");
            return failedResult;
        }
        if (null == aoyiRefundStatus || aoyiRefundStatus.isEmpty()){
            failedResult.setMessage("newStatus 缺失");
            return failedResult;
        }


        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectByRefundNo(aoyiRefundNo);
        }catch (Exception e) {
            failedResult.setMessage("工单内部错误");
            return failedResult;
        }

        if (null == workOrder) {
            failedResult.setMessage("工单不存在");
            return failedResult;
        }

        WorkFlow workFlow = new WorkFlow();
        workFlow.setWorkOrderId(workOrder.getId());
        workFlow.setCreatedBy("怡亚通通知");

        workFlow.setComments(WebSideWorkFlowStatusEnum.buildComments(AoYiRefundCallBackPostBean.convert2workflowCommentsCode(aoyiRefundStatus,oldStatus),JSON.toJSONString(data)));
        /*
        if (AoYiRefundCallBackPostBean.isPassedStatus(aoyiRefundStatus)) {
            workFlow.setStatus(WorkOrderStatusType.EDITING.getCode());
        }else if (AoYiRefundCallBackPostBean.isRejectedStatus(aoyiRefundStatus)){
            workFlow.setStatus(WorkOrderStatusType.REJECT.getCode());
        }else if (AoYiRefundCallBackPostBean.isReturnGoodsStatus(aoyiRefundStatus)){
            workFlow.setStatus(WorkOrderStatusType.HANDLING.getCode());
        }else {
            log.error("无法处理的回调状态: {}",aoyiRefundStatus);
        }*/
        workFlow.setStatus(WorkOrderStatusType.RESERVED.getCode());
        try {
            workFlowService.insert(workFlow);
        }catch (Exception e){
            log.error("数据库操作异常 {}",e.getMessage(),e);
        }

        log.info("{} 新建工作流记录 {}", functionName, JSON.toJSONString(workFlow));

        ////主动查询怡亚通退款状态获取退货地址
        WorkFlow addressWorkFlow = workOrderService.getYiYaTongRetureAddress(workOrder);
        if(null != addressWorkFlow){
            String addressComments = addressWorkFlow.getComments();
            addressWorkFlow.setComments(WebSideWorkFlowStatusEnum.buildComments(WebSideWorkFlowStatusEnum.NOTIFY_WAITING_RETURN_RECEIVED,addressComments));
            workFlowService.insert(addressWorkFlow);
            log.info("{} 新建工作流记录 {}", functionName, JSON.toJSONString(workFlow));
        }

        return successResult;
    }

/*
    @ApiOperation(value = "怡亚通发送退货物流回调", notes = "怡亚通退货物流回调，来自服务aoyi ")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("work_orders/notify/returnGoods")
    public ResultMessage<String> callBackReturnGoods(HttpServletResponse response,
                                                   @RequestBody AoYiRefundCallBackPostBean data) {

        String functionName = "怡亚通发送退货物流回调";
        log.info("{} {}",functionName,JSON.toJSONString(data));

        ResultMessage<String> failedResult = new ResultMessage<>(400,"failed",null);
        ResultMessage<String> successResult = new ResultMessage<>(200,"success",null);

        String aoyiRefundNo = data.getServiceSn();
        String aoyiRefundStatus = data.getNewStatus();

        if (null == aoyiRefundNo || aoyiRefundNo.isEmpty()){
            failedResult.setMessage("serviceSn 缺失");
            return failedResult;
        }
        if (null == aoyiRefundStatus || aoyiRefundStatus.isEmpty()){
            failedResult.setMessage("newStatus 缺失");
            return failedResult;
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectByRefundNo(aoyiRefundNo);
        }catch (Exception e) {
            failedResult.setMessage("工单内部错误");
            return failedResult;
        }

        if (null == workOrder) {
            failedResult.setMessage("工单不存在");
            return failedResult;
        }

        WorkFlow workFlow = new WorkFlow();
        workFlow.setWorkOrderId(workOrder.getId());
        workFlow.setCreatedBy("怡亚通通知");
        workFlow.setStatus(WorkOrderStatusType.HANDLING.getCode());
        workFlow.setCreateTime(new Date());
        workFlow.setUpdateTime(workFlow.getCreateTime());
        workFlow.setComments(WebSideWorkFlowStatusEnum.buildComments(AoYiRefundCallBackPostBean.convert2workflowCommentsCode(aoyiRefundStatus)));

        try {
            workFlowService.insert(workFlow);
        }catch (Exception e){
            log.error("数据库操作异常 {}",e.getMessage(),e);
        }


        log.info("{} done {}", functionName, JSON.toJSONString(successResult));
        return successResult;
    }
*/
    @ApiOperation(value = "删除工单流程信息", notes = "删除工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete WorkOrder's profile") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("work_orders/{id}")
    public void deleteWorkOrder(HttpServletResponse response,
                                          @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                          @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication
                                          ) throws RuntimeException {

        log.info("delete WorkOrders param : {}",id);

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002: ID is wrong");
            return;
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(id);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return;
        }

        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400002: failed to find record");
            return;
        }

        try {
            workOrderService.deleteById(id);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return;
        }
        response.setStatus(MyErrorMap.e204.getCode());

        log.info("delete WorkOrder profile");

    }

    @ApiOperation(value = "退款统计", notes = "退款统计信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/refunds")
    public ResultObject<ReturnCount> countReturn(@ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                                                 @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd
                                                ) {

        ReturnCount countNum = new ReturnCount();
        ResultObject<ReturnCount> result = new ResultObject<>(400, "failed: parameter missing", countNum);
        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        if (null == timeStart || timeStart.isEmpty() ||
                null == timeEnd || timeEnd.isEmpty()) {
            log.info("count all return and refund work-orders");
        } else {
            log.info("count return and refund work-order between: " + timeStart + "--" + timeEnd);
            try {
                dateCreateTimeStart = getDateType(timeStart,false);
                dateCreateTimeEnd = getDateType(timeEnd,true);
            } catch (Exception ex) {
                result.setMsg("createTime is wrong "+ex.getMessage());
                return result;
            }
        }
        int countReturn;
        try {
            countReturn = workOrderService.countReturn(dateCreateTimeStart, dateCreateTimeEnd);
        }catch (Exception e) {
            result.setCode(400);
            result.setMsg("400006:"+e.getMessage());
            return result;
        }
        countNum.count = countReturn;
        result.setCode(200);
        result.setMsg("success");

        return result;
    }

    @ApiOperation(value = "退款时间段列表", notes = "退款时间段列表")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/refunds/list")
    public ResultObject<List<WorkOrder>> getRefundList(@ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                                                 @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd
                                            ) {


        ResultObject<List<WorkOrder>> result = new ResultObject<>(400, "failed: parameter missing", null);
        java.util.Date dateCreateTimeStart;
        java.util.Date dateCreateTimeEnd;

        if (null == timeStart || timeStart.isEmpty() ||
                null == timeEnd || timeEnd.isEmpty()) {
            String dataNow = new Date().toString().trim();
            String todayStr = dataNow.substring(0,10);
            String todayBegin = todayStr+" 00:00:00";
            String todayEnd = todayStr+" 23:59:59";
            try {
                dateCreateTimeStart = StringUtil.String2Date(todayBegin);
                dateCreateTimeEnd = StringUtil.String2Date(todayEnd);
            } catch (Exception ex) {
                log.error("exception: {}",ex.getMessage());
                result.setMsg("createTime is wrong");
                return result;
            }

            log.info("will find records of "+todayStr);
        } else {
            log.info("count return and refund work-order between: " + timeStart + "--" + timeEnd);

            try {
                dateCreateTimeStart = getDateType(timeStart,false);
                dateCreateTimeEnd = getDateType(timeEnd,true);
            } catch (Exception ex) {
                log.error("createTime string is wrong exception {}",ex.getMessage());
                result.setMsg("createTime is wrong "+ex.getMessage());
                return result;
            }
        }
        try {
            List<WorkOrder> list = workOrderService.selectByTimeRange(dateCreateTimeStart, dateCreateTimeEnd);
            if (null != list) {
                /*
                for (WorkOrder a: list){
                    if (null != a.getGuanaitongTradeNo()){
                        String comments = getCommnets(a.getGuanaitongTradeNo());
                        if (null != comments){
                            a.setComments(comments);
                        }
                    }
                }*/

                result.setData(list);
                result.setCode(200);
                result.setMsg("success");
            }
        } catch (Exception e) {
            log.warn("selectByTimeRange exception : {}",e.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "获取商户退货人数", notes = "获取商户退货人数")
    @ApiResponses({@ApiResponse(code = 400, message = "failed to find record")})
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/refund/user/count")
    public ResultObject<Integer> getRefundUserCount(@ApiParam(value = "商户id", required = true)
                                                        @RequestParam("merchantId") Long merchantId)
    {
        log.info("获取商户的退货人数 入参 merchantId:{}", merchantId);

        ResultObject<Integer> resultObject = new ResultObject<>(500, "获取商户的退货人数错误", null);

        try {
            Integer count = workOrderService.queryRefundUserCount(merchantId);

            resultObject.setCode(200);
            resultObject.setData(count);
            resultObject.setMsg("success");
        } catch (Exception e) {
            log.error("获取商户的退货人数 异常:{}", e.getMessage(), e);

            resultObject = new ResultObject<>(500, "获取商户的退货人数异常", null);
        }

        log.info("获取商户的退货人数 返回:{}", resultObject.toString());

        return resultObject;
    }

    @ApiOperation(value = "聚合支付退款回调", notes = "聚合支付退款回调")
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping(value = "aggpays/notify", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String aggpaysNotify(@RequestBody AggPayNotifyBean data) {
        log.info("聚合支付退款回调通知: {}",JSON.toJSONString(data));
        try {
            return workOrderService.handleAggPaysNotify(data);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            return "fail";
        }
    }


    @ApiOperation(value = "关爱通退款回调", notes = "关爱通退款回调")
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping(value = "refund/notify", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String gBack(GuanAiTongNotifyBean bean) {
        String param = JSON.toJSONString(bean);
        log.info("关爱通 refund notify: params : " + param);
        try {
            return workOrderService.handleNotify(bean);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            return "fail";
        }
    }

    @ApiOperation(value = "发起关爱通退款", notes = "发起关爱通退款")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("refund/guanaitong")
    public GuanAiTongNo sendRefund(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @RequestBody Map<String, Long> data) {

        if (null == authentication) {
            log.info("can not get authentication");
        }
        GuanAiTongNo result = new GuanAiTongNo();
        Long id = data.get("id");
        Integer hasFare = (int)(long)data.get("fare");
        if (null == id) {
            log.error("send refund to GuanAiTong for work-order: id is null");
            StringUtil.throw400Exp(response, "400002: id is wrong");
            return result;
        }
        log.info("send refund for work-order: id = " + id.toString());

        String guanAiTongTradeNo;
        try {
            guanAiTongTradeNo = workOrderService.sendRefund2GuangAiTong(id,hasFare,null);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }
        if (null == guanAiTongTradeNo) {
            StringUtil.throw400Exp(response, "400003: 关爱通退款接口没有返回关爱通退款单号");
            return result;
        } else {
            if (guanAiTongTradeNo.isEmpty()) {
                StringUtil.throw400Exp(response, "400004: send to GuanAiTong failed");
                return null;
            } else {
                if (guanAiTongTradeNo.contains("Error:")) {
                    String errMsg = guanAiTongTradeNo.replace(':','-');
                    StringUtil.throw400Exp(response, "400006: " + errMsg);
                    return result;
                }
            }
        }

        result.tradeNo = guanAiTongTradeNo;
        response.setStatus(MyErrorMap.e201.getCode());

        return result;
    }

    /**
     * 获取已退款的子订单id集合
     *
     * @param merchantId 厂商ID
     * @param startTime yyyy-MM-dd HH:mm:ss
     * @param endTime yyyy-MM-dd HH:mm:ss
     * @return list
     */
    @ApiOperation(value = "获取已退款的子订单id集合", notes = "获取已退款的子订单id集合")
    @ResponseStatus(code = HttpStatus.CREATED)
    @GetMapping("refund/query/refunded")
    public ResultObject<List<String>>
    queryRefundedOrderDetailIdList(@RequestParam(value = "merchantId", required = false) Long merchantId,
                                   @RequestParam(value = "appId", required = false) String appId,
                                   @RequestParam(value = "startTime") String startTime,
                                   @RequestParam(value = "endTime") String endTime) {
        // 返回值
        ResultObject<List<String>> resultObject = new ResultObject<>(500, "获取已退款的子订单id集合默认错误", null);

        log.info("获取已退款的子订单id集合 入参 merchantId:{}, startTime:{}, endTime:{}", merchantId, startTime, endTime);

        try {
            Date startTimeDate = DateUtil.parseDateTime(startTime, DateUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            Date endTimeDate = DateUtil.parseDateTime(endTime, DateUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            List<WorkOrder> workOrderList =
                    workOrderService.querySuccessRefundOrderDetailIdList(appId,merchantId, startTimeDate, endTimeDate);

            List<String> idList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(workOrderList)) {
                idList = workOrderList.stream().map(w -> w.getOrderId()).collect(Collectors.toList());
            }

            resultObject.setCode(200);
            resultObject.setMsg("成功");
            resultObject.setData(idList);
        } catch (Exception e) {
            log.error("取已退款的子订单id集合 异常:{}", e.getMessage(), e);

            resultObject.setCode(500);
            resultObject.setData(null);
            resultObject.setMsg("取已退款的子订单id集合异常," + e.getMessage());
        }

        log.info("取已退款的子订单id集合 返回:{}", JSONUtil.toJsonString(resultObject));

        return resultObject;
    }


    private ResultMessage<List<AggPayRefundQueryBean>> queryAggPay(WorkOrder workOrder) {

        String outRefundNo = workOrder.getGuanaitongTradeNo();
        if (null != outRefundNo
                && WorkOrderStatusType.REFUNDING.getCode().equals(workOrder.getStatus())) {
            log.info("调用查询聚合支付退款状态接口 outRefundNo={}", outRefundNo);

            ResultMessage<List<AggPayRefundQueryBean>> aggPayResult;
            try {
                aggPayResult = aggPayClient.getAggPayRefund(outRefundNo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                aggPayResult = null;
            }
            log.info("查询聚合支付退款状态： response={}", JSON.toJSONString(aggPayResult));
            if (null == aggPayResult || null == aggPayResult.getCode() ||
                    200 != aggPayResult.getCode() || null == aggPayResult.getData()) {
                log.error("未找到聚合支付服务, 或调用查询聚合支付退款状态接口失败");

            }
            workOrder.setComments(JSON.toJSONString(aggPayResult.getData()));
            return aggPayResult;
        }
        return null;
    }

    private List<WorkOrder> batchQueryAggPay(List<WorkOrder> list) {

        StringBuilder sb = new StringBuilder();

        for(WorkOrder w:list){
            if (null != w.getGuanaitongTradeNo()) {
                if (0 != sb.length()) {
                    sb.append(",");
                }
                sb.append(w.getGuanaitongTradeNo());
            }
        }

        String refundNoList = sb.toString();
        if (null == refundNoList || refundNoList.isEmpty()){
            log.info("没有需要查询的退款记录");
            return list;
        }

        log.info("查询聚合支付退款状态：参数 {}", refundNoList);
        ResultMessage<Map<String,List<AggPayRefundQueryBean>>> aggPayResult =
                aggPayClient.getBatchAggPayRefund(refundNoList);

        log.info("查询聚合支付退款状态：返回 {}", JSON.toJSONString(aggPayResult));
        if (null == aggPayResult || null == aggPayResult.getCode() ||
                    200 != aggPayResult.getCode() || null == aggPayResult.getData()) {
                log.error("未找到聚合支付服务, 或调用查询聚合支付退款状态接口失败");
                return list;

        }

        Map<String,List<AggPayRefundQueryBean>> refundMap = aggPayResult.getData();
        for(WorkOrder record: list){
            String refundNo = record.getGuanaitongTradeNo();
            if (null != refundNo){
                String refundDetail = JSON.toJSONString(refundMap.get(refundNo));
                if (null != refundDetail) {
                    record.setComments(refundDetail);
                }
            }
        }

        return list;
    }


    /**
     * 获取已退款的子订单信息集合
     *
     * @param merchantId 厂商ID
     * @param startTime yyyy-MM-dd HH:mm:ss
     * @param endTime yyyy-MM-dd HH:mm:ss
     * @return list
     */
    @ApiOperation(value = "获取已退款的子订单信息集合", notes = "获取已退款的子订单id集合")
    @ResponseStatus(code = HttpStatus.CREATED)
    @GetMapping("refund/query/refundedDetail")
    public ResultObject<List<WorkOrder>> queryRefundedOrderDetailList(@RequestParam(value = "merchantId", required = false) Long merchantId,
                                                                      @RequestParam(value = "appId", required = false) String appId,
                                                                     @RequestParam(value = "startTime") String startTime,
                                                                     @RequestParam(value = "endTime") String endTime) {
        // 返回值
        ResultObject<List<WorkOrder>> resultObject = new ResultObject<>(500, "获取已退款的子订单信息集合默认错误", null);

        log.info("获取已退款的子订单信息集合 入参 appId={}, merchantId:{}, startTime:{}, endTime:{}", appId,merchantId, startTime, endTime);
        Date startTimeDate=null;
        Date endTimeDate=null;
        List<WorkOrder> workOrderList = null;

        try {
            startTimeDate = DateUtil.parseDateTime(startTime, DateUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            endTimeDate = DateUtil.parseDateTime(endTime, DateUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if (null == startTimeDate || null == endTimeDate){
            resultObject.setCode(500);
            resultObject.setData(null);
            resultObject.setMsg("查询失败：开始时间，结束时间不能为空");
            return  resultObject;
        }
        try {
            workOrderList =
                    workOrderService.querySuccessRefundOrderDetailIdList(appId,merchantId, startTimeDate, endTimeDate);
        } catch (Exception e) {
            resultObject.setCode(500);
            resultObject.setData(null);
            resultObject.setMsg("获取已退款的子订单信息集合异常," + e.getMessage());
            return resultObject;
        }

        resultObject.setCode(200);
        resultObject.setMsg("成功");
        resultObject.setData(workOrderList);

        List<WorkOrder> result = batchQueryAggPay(workOrderList);
        resultObject.setData(result);

        log.info("获取已退款的子订单信息集合 返回"/*, JSONUtil.toJsonString(result)*/);

        return resultObject;
    }

/*
    @ApiOperation(value = "获取指定工单流程信息", notes = "工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/{id}/work_flows")
    public WorkFlowListData getWorkFlowById(HttpServletResponse response,
                                          @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                          @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        WorkFlowListData result = new WorkFlowListData();
        //String username = JwtTokenUtil.getUsername(authentication);
        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }


        WorkOrder workOrder = workOrderService.selectById(id);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_order");
            return result;
        }

        List<WorkFlow> flows = workFlowService.selectByWorkOrderId(workOrder.getId());
        List<WorkFlowBean> list = new ArrayList<>();
        for (WorkFlow a : flows) {
            WorkFlowBean b = new WorkFlowBean();
            BeanUtils.copyProperties(a, b);
            list.add(b);
        }
        result.list = list;
        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }
*/
}
