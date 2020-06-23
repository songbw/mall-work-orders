package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.constants.WorkOrderType;
import com.fengchao.workorders.entity.WorkFlow;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.feign.IAggPayClient;
import com.fengchao.workorders.entity.WorkOrder;
//import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.service.db.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
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
                               WorkOrderServiceImpl workOrderService
                             ) {
        this.workOrderService = workOrderService;
        this.aggPayClient = aggPayClient;
        this.workFlowService = workFlowService;
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
                workOrder = workOrderService.getById(recordId);
                if (null != workOrder && !WorkOrderStatusType.isClosedStatus(workOrder.getStatus())) {
                    if(itemCount == itemOk) {
                        workOrder.setStatus(WorkOrderStatusType.CLOSED);
                    }else{
                        workOrder.setStatus(WorkOrderStatusType.REFUND_FAILED);
                    }
                    workOrder.setRefundTime(StringUtil.String2Date(endTime));
                    workOrder.setComments(comments);
                    workOrderService.updateById(workOrder);
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
    public WorkOrderBean
    getWorkOrderById(@ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        String functionDescription = "获取指定工单信息";
        WorkOrderBean bean = new WorkOrderBean();

        if (null == id || 0 == id) {
            return new WorkOrderBean();
        }

        WorkOrder workOrder = workOrderService.getById(id);
        if (null == workOrder) {
            return new WorkOrderBean();
        }

        BeanUtils.copyProperties(workOrder, bean);
        if (null != workOrder.getGuanaitongRefundAmount()) {
            bean.setRealRefundAmount(workOrder.getGuanaitongRefundAmount());
        }

        String outRefundNo = workOrder.getGuanaitongTradeNo();
        if (null != outRefundNo
                && (WorkOrderStatusType.REFUNDING.getCode().equals(workOrder.getStatus()) ||
                WorkOrderStatusType.isClosedStatus(workOrder.getStatus()))) {
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

                return bean;
            }
            bean.setComments(JSON.toJSONString(aggPayResult.getData()));

            // 根据查询结果更新工单记录
            checkWorkOrderByAggPay(workOrder,aggPayResult.getData());
        }

        log.info("{} {}",functionDescription,JSON.toJSONString(bean));
        return bean;

    }

    @ApiOperation(value = "根据订单号列表获取工单信息", notes = "根据订单号列表获取工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("work_orders/byOrderList")
    public List<WorkOrderBean>
    getWorkOrderByOrderIdList(HttpServletResponse response,
                              @ApiParam(value="idList",required=true) @Valid @RequestBody List<String> idList) {

        String functionDescription = "根据订单号列表获取工单信息";

        if (null == idList || 0 == idList.size()) {
            throw new MyException(MyErrorEnum.RESPONSE_FUNCTION_ERROR, "订单号列表缺失");
        }

        List<WorkOrder> workOrders = workOrderService.selectByOrderIdList(idList);
        if (null == workOrders || 0 == workOrders.size()) {
            return new ArrayList<>(0);
        }

        List<WorkOrderBean> list = new ArrayList<>();
        for(WorkOrder w: workOrders) {
            list.add(WorkOrderBean.convert(w));
        }

        log.info("{} 查询到={} 条",functionDescription,list.size());
        return list;

    }


    @ApiOperation(value = "查询退款异常工单", notes = "查询退款异常工单")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/abnormalList")
    public PageInfo<WorkOrderBean>
    queryAbnormalList(HttpServletResponse response,
                      @RequestHeader(value = "merchant") Long merchantIdInHeader,
                      @ApiParam(value="页码")@RequestParam(defaultValue = "1") Integer pageIndex,
                      @ApiParam(value="每页记录数")@RequestParam(defaultValue = "10") Integer pageSize,
                      @ApiParam(value="iAppId")@RequestParam(required=false) String iAppId,
                      @ApiParam(value="订单ID")@RequestParam(required=false) String orderId,
                      @ApiParam(value="商户ID")@RequestParam(required=false) Long merchantId,
                      @ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                      @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd
                    ) {

        if (null == merchantIdInHeader) {
            log.warn("can not find merchant in header");
            throw new MyException(MyErrorEnum.HEADER_MERCHANT_ID_BLANK);
        }

        Long merchant;
        if (0 != merchantIdInHeader) {
            merchant = merchantIdInHeader;
        } else {
            merchant = merchantId;
        }

        PageInfo<WorkOrder> pages = workOrderService.selectAbnormalRefundList(pageIndex, pageSize,
                    iAppId, orderId, merchant,
                    timeStart, timeEnd);

        List<WorkOrderBean> workOrderBeans = new ArrayList<>();
        if ((pageIndex -1) * pages.getPageSize() <= pages.getTotal()) {
            List<WorkOrder> workOrders = batchQueryAggPay(pages.getRows());
            for (WorkOrder a : workOrders) {
                workOrderBeans.add(WorkOrderBean.convert(a));
            }
        }

        return new PageInfo<>(pages.getTotal(), pages.getPageSize(),pageIndex, workOrderBeans);
    }

    @ApiOperation(value = "条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/pages")
    public PageInfo<WorkOrderBean>
    queryWorkOrders(
                    @RequestHeader(value = "merchant") Long merchantIdInHeader,
                    @ApiParam(value="页码")@RequestParam(defaultValue = "1") Integer pageIndex,
                    @ApiParam(value="每页记录数")@RequestParam(defaultValue = "10") Integer pageSize,
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
                    @ApiParam(value="工单状态码")@RequestParam(required=false) Integer status
                    ) {

        if (null == merchantIdInHeader) {
            log.warn("can not find merchant in header");
            throw new MyException(MyErrorEnum.HEADER_MERCHANT_ID_BLANK);
        }

        Long merchant;
        if (0 != merchantIdInHeader) {
            merchant = merchantIdInHeader;
        } else {
            merchant = merchantId;
        }

        PageInfo<WorkOrder> pages= workOrderService.selectPage(pageIndex, pageSize, iAppId,
                    title, receiverId, receiverName, receiverPhone, orderId, typeId, merchant,
                    status, timeStart, timeEnd,refundTimeStart, refundTimeEnd);

        List<WorkOrderBean> list = new ArrayList<>();
        List<WorkOrder> workOrders = batchQueryAggPay(pages.getRows());
        if ((pageIndex -1) * pages.getPageSize() <= pages.getTotal()) {
            for (WorkOrder a : workOrders) {
                list.add(WorkOrderBean.convert(a));
            }
        }
        return new PageInfo<>(pages.getTotal(), pages.getPageSize(),pageIndex, list);
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
        String iAppId = data.getIAppId();
        String tAppId = data.getTAppId();

        data.checkFields();

        WorkOrder workOrder = new WorkOrder();

        WorkOrder selectedWO = workOrderService.getValidNumOfOrder(receiverId, orderId);
        if (null == selectedWO) {//totally new order
            log.info("there is not work order of this orderId: " + orderId);
            JSONObject json = workOrderService.getOrderInfo(receiverId, orderId, merchantId);
            if (null == json) {
                throw new MyException(MyErrorEnum.API_SEARCH_ORDER_FAILED);
            }

            Integer parentOrderId = json.getInteger("id");
            if (null == parentOrderId) {
                throw new MyException(MyErrorEnum.API_SEARCH_ORDER_ID_BLANK);
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
                throw new MyException(MyErrorEnum.REFUND_COUNT_OVERFLOW);
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
        workOrder.setTypeId(WorkOrderType.checkByCode(typeId));
        workOrder.setMerchantId(merchantId);
        workOrder.setStatus(WorkOrderStatusType.EDITING);
        workOrder.setReceiverId(receiverId);
        workOrder.setIAppId(iAppId);
        if (null != tAppId && !tAppId.isEmpty()) {
            workOrder.setTAppId(tAppId);
        }
        workOrderService.save(workOrder);
        result.id = workOrder.getId();

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
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_ID_BLANK);
        }

        WorkOrder workOrder = workOrderService.getById(id);
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

        workOrderService.updateById(workOrder);

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
        workFlow.setStatus(WorkOrderStatusType.RESERVED);
        workFlowService.save(workFlow);

        log.info("{} 新建工作流记录 {}", functionName, JSON.toJSONString(workFlow));
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
    public void
    deleteWorkOrder(HttpServletResponse response,
                    @ApiParam(value="id",required=true)@PathVariable("id") Long id
                    ){

        log.info("delete WorkOrders param : {}",id);
        if (null == id || 0 == id) {
            return;
        }

        WorkOrder workOrder = workOrderService.getById(id);
        if (null == workOrder) {
            throw new MyException(MyErrorEnum.COMMON_DB_GET_RECORD_RESULT_NULL);
        }

        workOrderService.removeById(id);
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

        int countReturn;
        try {
            countReturn = workOrderService.countReturn(timeStart, timeEnd);
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
    public ResultObject<List<WorkOrder>>
    getRefundList(@ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                  @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd
                ) {

        return new ResultObject<>(workOrderService.selectByTimeRange(timeStart, timeEnd));
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
    public GuanAiTongNo
    sendRefund(HttpServletResponse response,
               @RequestBody Map<String, Long> data) {


        GuanAiTongNo result = new GuanAiTongNo();
        Long id = data.get("id");
        Integer hasFare = (int)(long)data.get("fare");
        if (null == id) {
            log.error("send refund to GuanAiTong for work-order: id is null");
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_ID_BLANK);
        }
        log.info("send refund for work-order: id = " + id.toString());

        String guanAiTongTradeNo = workOrderService.sendRefund2GuangAiTong(id,hasFare,null);

        if (null == guanAiTongTradeNo || guanAiTongTradeNo.isEmpty()) {
            throw new MyException(MyErrorEnum.API_GAT_CLIENT_POST_FAILED," 关爱通退款接口没有返回关爱通退款单号");
        } else {
                if (guanAiTongTradeNo.contains("Error:")) {
                    String errMsg = guanAiTongTradeNo.replace(':','-');
                    throw new MyException(MyErrorEnum.API_GAT_CLIENT_POST_FAILED,errMsg);
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
                                   @RequestParam(value = "startTime", required = false) String startTime,
                                   @RequestParam(value = "endTime", required = false) String endTime) {
        // 返回值
        ResultObject<List<String>> resultObject = new ResultObject<>(500, "获取已退款的子订单id集合默认错误", null);

        log.info("获取已退款的子订单id集合 入参 merchantId:{}, startTime:{}, endTime:{}", merchantId, startTime, endTime);

        try {
            List<WorkOrder> workOrderList =
                    workOrderService.querySuccessRefundOrderDetailIdList(appId,merchantId, startTime, endTime);

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
    public ResultObject<List<WorkOrder>>
    queryRefundedOrderDetailList(@RequestParam(value = "merchantId", required = false) Long merchantId,
                                 @RequestParam(value = "appId", required = false) String appId,
                                 @RequestParam(value = "startTime", required = false) String startTime,
                                 @RequestParam(value = "endTime", required = false) String endTime) {
        // 返回值
        ResultObject<List<WorkOrder>> resultObject = new ResultObject<>(500, "获取已退款的子订单信息集合默认错误", null);

        log.info("获取已退款的子订单信息集合 入参 appId={}, merchantId:{}, startTime:{}, endTime:{}", appId,merchantId, startTime, endTime);

        List<WorkOrder> workOrderList;

        if (null == startTime || null == endTime){
            resultObject.setCode(500);
            resultObject.setData(null);
            resultObject.setMsg("查询失败：开始时间，结束时间不能为空");
            return  resultObject;
        }
        try {
            workOrderList =
                    workOrderService.querySuccessRefundOrderDetailIdList(appId,merchantId, startTime, endTime);
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
