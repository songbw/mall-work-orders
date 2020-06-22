package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.config.RefundConfig;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.constants.WorkOrderType;
import com.fengchao.workorders.entity.WorkOrder;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.feign.IAggPayClient;
import com.fengchao.workorders.entity.WorkFlow;
//import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.util.*;

/**
 * 工作流接口
 * @author Clark
 * */
@Slf4j
@Api(tags="WorkFlowAPI", description = "工单流程管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkFlowController {

    //private static Logger log = LoggerFactory.getLogger(WorkFlowController.class);

    private WorkFlowServiceImpl workFlowService;
    private WorkOrderServiceImpl workOrderService;
    private IAggPayClient aggPayClient;
    private RefundConfig refundConfig;

    @ApiModel(value = "工单流程信息ID")
    private class IdData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @Autowired
    public WorkFlowController(WorkOrderServiceImpl workOrderService,
                              RefundConfig refundConfig,
                              IAggPayClient aggPayClient,
                            WorkFlowServiceImpl workFlowService
                             ) {
        this.workFlowService = workFlowService;
        this.workOrderService = workOrderService;
        this.aggPayClient = aggPayClient;
        this.refundConfig = refundConfig;
    }

    @ApiOperation(value = "获取工单流程备注码信息", notes = "用于前端解析工单流程备注JSON")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/commentsCode")
    public Map
    getCommentsCodeMap() {
        return WebSideWorkFlowStatusEnum.getMap();
    }

    @ApiOperation(value = "获取指定工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/{id}")
    public WorkFlowBean
    getWorkFlowById(@ApiParam(value="id",required=true)@PathVariable("id") Long id) {
        return WorkFlowBean.convert(Optional.ofNullable(workFlowService.getById(id)).orElse(new WorkFlow()));
    }

    @ApiOperation(value = "条件查询工单流程")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/pages")
    public PageInfo<WorkFlowBean>
    queryWorkFlows(HttpServletResponse response,
                   @ApiParam(value="页码")@RequestParam(defaultValue = "1") Integer pageIndex,
                   @ApiParam(value="每页记录数")@RequestParam(defaultValue = "20") Integer pageSize,
                   @ApiParam(value="创建开始时间")@RequestParam(required=false) String createTimeStart,
                   @ApiParam(value="创建结束时间")@RequestParam(required=false) String createTimeEnd,
                   @ApiParam(value="workOrderId")@RequestParam(required=false)Long workOrderId) {

        log.info("条件查询工单流程 入参： workOrderId ={} createTime range:{} {}",workOrderId,createTimeStart,createTimeEnd);

        PageInfo<WorkFlow> pages = workFlowService.selectPage(pageIndex, pageSize,
                                                              workOrderId, createTimeStart, createTimeEnd);

        List<WorkFlowBean> beans = new ArrayList<>();
        if(null != pages.getRows() && 0 < pages.getRows().size()){
            pages.getRows().forEach(record->beans.add(WorkFlowBean.convert(record)));
        }

        PageInfo<WorkFlowBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),pageIndex, beans);

        log.info("条件查询工单流程 返回 {}",JSON.toJSONString(result));
        return result;

    }


    @ApiOperation(value = "特别处理重新打开工单创建工单流程信息", notes = "特别处理重新打开工单创建工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "特别处理重新打开工单失败") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows/work_order")
    public IdData renewProfile(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @RequestBody WorkFlowBodyBean data) throws Exception {

        log.info("特别处理重新打开工单 入参 {}", JSON.toJSONString(data));
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        Long workOrderId = data.getWorkOrderId();
        WorkOrderStatusType nextStatus = WorkOrderStatusType.EDITING;
        String comments = data.getComments();
        String operator = data.getOperator();
        Integer typeId =  data.getTypeId();
        Float refund = data.getRefund();

        if (null == workOrderId || 0 == workOrderId ) {
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_ID_BLANK);
        }

        if (null == operator || operator.isEmpty()) {
            if(null == username || username.isEmpty()) {
                throw new MyException(MyErrorEnum.PARAM_OPERATOR_BLANK);
            }
        }

        if (null != typeId){
            if (WorkOrderType.Int2String(typeId).isEmpty()) {
                StringUtil.throw400Exp(response, "400005:工单类型错误");
                return result;
            }
            if (!WorkOrderType.EXCHANGE.getCode().equals(typeId) && null == refund){
                StringUtil.throw400Exp(response, "400010:退款金额缺失");
                return result;
            }
        }

        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400004:工单号不存在");
            return result;
        }
        log.info("特别处理重新打开工单： {}",JSON.toJSONString(workOrder));

        WorkOrderStatusType orderStatus = workOrder.getStatus();
        if (!WorkOrderStatusType.isClosedStatus(orderStatus)) {
            StringUtil.throw400Exp(response, "400007:工单状态为处理完成时才可以重新打开");
            return result;
        }

        WorkFlow workFlow = new WorkFlow();
        workFlow.setWorkOrderId(workOrderId);
        workFlow.setStatus(nextStatus);

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }
        if (null != typeId){
            if (null == comments) {
                String tmpStr = " 更改工单类型,从" + workOrder.getTypeId().toString() + " 到 " + typeId.toString();
                String oldComments = workFlow.getComments();
                workFlow.setComments(oldComments + tmpStr);
                workOrder.setComments("重新打开工单 同时更改工单类型,从 " + workOrder.getTypeId().toString() + " 到 " + typeId.toString());
            }
            workOrder.setTypeId(WorkOrderType.checkByCode(typeId));
            if (WorkOrderType.EXCHANGE.getCode().equals(typeId)){
                workOrder.setRefundAmount(0.00F);
            }else {
                workOrder.setRefundAmount(refund);
            }
        }else {
            workOrder.setComments("重新打开工单");
        }

        if (null != username && !username.isEmpty()) {
            workFlow.setCreatedBy(username);
        }

        try{
            updateFlowAndWorkorder(workOrder,workFlow,true);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        result.id = workFlow.getId();
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("特别处理重新打开工单 success");
        return result;
    }

    @ApiOperation(value = "创建工单流程信息", notes = "创建工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows")
    public IdData createProfile(HttpServletResponse response,
                                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                            @RequestBody WorkFlowBodyBean data) throws RuntimeException {

        log.info("创建工单流程信息 param {}", JSON.toJSONString(data));
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        Long workOrderId = data.getWorkOrderId();
        Integer nextStatusCode = data.getStatus();
        WorkOrderStatusType nextStatus = WorkOrderStatusType.checkByCode(nextStatusCode);
        String comments = data.getComments();
        String operator = data.getOperator();
        Integer handleFare = null;//data.getHandleFare();
        Float refund = data.getRefund();

        if (null == workOrderId || 0 == workOrderId
            ) {
            StringUtil.throw400Exp(response, "400002:工单号不能为空");
            return result;
        }

        if (null == operator || operator.isEmpty()) {
            StringUtil.throw400Exp(response, "400003: 操作员不能为空");
            return result;
        }

        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400004:工单号不存在");
            return result;
        }
        log.info("工单： {}",JSON.toJSONString(workOrder));
        String iAppId = workOrder.getIAppId();
        WorkOrderStatusType orderStatus = workOrder.getStatus();
        if (WorkOrderStatusType.isClosedStatus(orderStatus)
                || WorkOrderStatusType.REJECT.equals(orderStatus)) {
            StringUtil.throw400Exp(response, "400007:工单状态为审核失败或处理完成时不可更改");
            return result;
        }

        WorkFlow workFlow = new WorkFlow();

        workFlow.setWorkOrderId(workOrderId);

        workFlow.setStatus(nextStatus);

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }

        if (null != username && !username.isEmpty()) {
            workFlow.setCreatedBy(username);
        }

        if (WorkOrderStatusType.HANDLING.equals(nextStatus)){
            //上传退货物流信息
            if (YiYaTong.MERCHANT_ID == workOrder.getMerchantId()){
                //怡亚通的订单,需要发送物流信息
                workOrderService.sendExpressInfo(workOrder,comments);
            }
            try{
                updateFlowAndWorkorder(workOrder,workFlow,false);
            }catch (Exception e){
                StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                return null;
            }

            result.id = workFlow.getId();
            response.setStatus(MyErrorMap.e201.getCode());
            log.info("create WorkFlow and update workOrder {} success ",workOrder.getId().toString());
            return result;

        }

        if (WorkOrderStatusType.isClosedStatus(nextStatus)){
            // 直接关闭工单
            try{
                updateFlowAndWorkorder(workOrder,workFlow,false);
            }catch (Exception e){
                StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                return null;
            }

            result.id = workFlow.getId();
            response.setStatus(MyErrorMap.e201.getCode());
            log.info("create WorkFlow and close workOrder {} success ",workOrder.getId().toString());
            return result;

        }

        if (WorkOrderStatusType.REFUNDING.equals(orderStatus)
                && (WorkOrderStatusType.REFUNDING.equals(nextStatus))) {
            StringUtil.throw400Exp(response, "40000a:工单状态已经为退款处理中");
            return result;
        }

        if (null == nextStatus) {
            StringUtil.throw400Exp(response, "400005:状态码错误");
            return result;
        }

        if (null != refund && 0 < refund){

            BigDecimal decRefund = new BigDecimal(refund);
            BigDecimal decRefundStored = new BigDecimal(workOrder.getRefundAmount());
            Float fare = workOrder.getFare();
            boolean hasFare = (null != fare) && (0.009 < fare);
            if (hasFare){
                BigDecimal decFare = new BigDecimal(workOrder.getFare());
                decRefundStored = decRefundStored.add(decFare);
            }
            NumberFormat formatter = new DecimalFormat("0");
            BigDecimal dec100f = new BigDecimal("100");
            String strRefundStored = formatter.format(decRefundStored.multiply(dec100f).floatValue());
            String strRefund = formatter.format(decRefund.multiply(dec100f).floatValue());
            log.info("工单：{} 可退款最高额度={}分 , 本次要求退款额度={}分",
                    workOrder.getId().toString(),strRefundStored,strRefund);
            Integer canRefundAmountFen = Integer.valueOf(strRefundStored);
            Integer requestRefundAmountFen = Integer.valueOf(strRefund);
            if (requestRefundAmountFen > canRefundAmountFen){
                StringUtil.throw400Exp(response, "400008:退款金额超出合理范围");
                return result;
            }
        }

        WorkOrderType workTypeId = workOrder.getTypeId();

        String configIAppIds = GuanAiTongConfig.getConfigGatIAppId();
        ConfigBean configBean;
        try{
           configBean = refundConfig.getConfig(iAppId);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "410005:未配置或无效的iAppId: "+iAppId);
            return result;
        }
        if (null == configBean || null == configBean.getIAppId() || null == configBean.getApiType()){
            StringUtil.throw400Exp(response, "410006:未配置iAppId:"+iAppId);
            return result;
        }


        boolean isGat = false;
        boolean isAggPay = false;
        if (null != iAppId && null != configIAppIds && !configIAppIds.isEmpty() && !iAppId.isEmpty()){
            if (Constant.API_TYPE_GUAN_AI_TONG.equals(configBean.getApiType())){
                isGat = true;
            }else{
                if (Constant.API_TYPE_AGGPAY.equals(configBean.getApiType())){
                    isAggPay = true;
                }
            }

            if (configIAppIds.equals(iAppId)) {
                isGat = true;
            }
        }
        log.info("create WorkFlow: isGat={}, isAggPay={}",isGat, isAggPay);
        if ((WorkOrderType.RETURN.equals(workTypeId) || WorkOrderType.REFUND.equals(workTypeId)) &&
             ((WorkOrderStatusType.REFUNDING.equals(nextStatus)) && (WorkOrderStatusType.ACCEPTED.equals(orderStatus) ||
               WorkOrderStatusType.HANDLING.equals(orderStatus)))) {

            if (isGat) {
                if (null == refund){
                    StringUtil.throw400Exp(response, "400008:退款金额缺失");
                    return null;
                }
                String guanAiTongTradeNo;
                try {
                    guanAiTongTradeNo = workOrderService.sendRefund2GuangAiTong(workOrderId, handleFare, refund);
                } catch (Exception e) {
                    StringUtil.throw400Exp(response, "400006:" + e.getMessage());
                    return null;
                }
                if (null == guanAiTongTradeNo) {
                    StringUtil.throw400Exp(response, "400009: failed to get guanAiTongTradeNo in result of response");
                    return result;
                } else {
                    if (guanAiTongTradeNo.isEmpty()) {
                        StringUtil.throw400Exp(response, "400009: failed to get guanAiTongTradeNo in result of response");
                        return result;
                    } else {
                        if (guanAiTongTradeNo.contains("Error:")) {
                            String errMsg = guanAiTongTradeNo.replace(':', '-');
                            StringUtil.throw400Exp(response, "400009:" + errMsg);
                            return result;
                        }
                    }
                }

            } else if (isAggPay){
                if (null == refund){
                    StringUtil.throw400Exp(response, "400008:退款金额缺失");
                    return null;
                }
                // 怡亚通退货退款流程检查
                //if (YiYaTong.MERCHANT_ID == workOrder.getMerchantId()){
                //    if (!WorkOrderStatusType.HANDLING.getCode().equals(workOrder.getStatus()) &&
                //        WorkOrderType.RETURN.getCode().equals(workOrder.getTypeId())){
                //        StringUtil.throw400Exp(response,"420101:需要先上传退货物流信息");
                //    }
                //}
                // end
                AggPayRefundBean aBean = new AggPayRefundBean();
                aBean.setOrderNo(workOrder.getTradeNo());
                NumberFormat formatter = new DecimalFormat("0");
                BigDecimal dec100f = new BigDecimal("100");
                BigDecimal decRefund = new BigDecimal(refund);
                aBean.setRefundFee(formatter.format(decRefund.multiply(dec100f).floatValue()));
                String outerRefundNo = iAppId+StringUtil.getTimeStampRandomStr();
                aBean.setOutRefundNo(outerRefundNo);
                aBean.setMerchantCode(workOrder.getMerchantId().toString());
                aBean.setNotifyUrl(Constant.AGGPAY_NOTIFY_URL);
                ResultMessage<String> aggpayRst = null;
                log.info("try send to aggpay {}",JSON.toJSONString(aBean));
                try {
                    aggpayRst = aggPayClient.postAggPayRefund(aBean);
                }catch (Exception e){
                    log.error("access aggpays failed {}",e.getMessage());
                    StringUtil.throw400Exp(response,"40000a:access aggpays failed"+e.getMessage());
                }
                log.info("got response from aggpays: {}",JSON.toJSONString(aggpayRst));
                if (null == aggpayRst || null == aggpayRst.getCode() || null == aggpayRst.getMessage()){
                    StringUtil.throw400Exp(response,"40000b:access aggPays failed, got null response");
                }
                if (200 != aggpayRst.getCode()){
                    StringUtil.throw400Exp(response,"40000c:access aggPays failed, "+aggpayRst.getMessage());
                }

                if (null != aggpayRst.getData()) {
                    //JSONObject json = JSON.parseObject(aggpayRst.getData());
                    //String aggpayRefundNo = json.getString("refundNo");
                    String aggpayRefundNo = aBean.getOutRefundNo();
                    if (null != aggpayRefundNo && !aggpayRefundNo.isEmpty()){
                        try {
                            workOrder = workOrderService.getById(workOrderId);
                            if (null != workOrder) {
                                if (!WorkOrderStatusType.isClosedStatus(workOrder.getStatus())) {
                                    workOrder.setStatus(WorkOrderStatusType.REFUNDING);
                                }
                                if (null == workOrder.getRefundNo() || workOrder.getRefundNo().isEmpty()) {
                                    //怡亚通的订单,退款号来自申请接口返回的serviceSn,不需要更新
                                    //其他商家工单,或怡亚通的订单没有成功下单到怡亚通的, 此处记录工单系统生成的退款号
                                    workOrder.setRefundNo(outerRefundNo);
                                }
                                workOrder.setGuanaitongTradeNo(aggpayRefundNo);
                                workOrder.setRefundAmount(refund);
                                workOrderService.updateById(workOrder);
                            }
                        }catch (Exception e){
                            StringUtil.throw400Exp(response,"400006:work_order update failed "+e.getMessage());
                        }
                    }
                }

            }

            result.id = createWorkFlow(workOrder,workFlow);
            response.setStatus(MyErrorMap.e201.getCode());
            log.info("退款接口完成并更新工单,记录处理流程 id= {} ",result.id);
            return result;
        }

        // 没有进行退款操作处理
        try{
            updateFlowAndWorkorder(workOrder,workFlow,false);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        result.id = workFlow.getId();
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("create WorkFlow and update workOrder {} success ",workOrder.getId().toString());
        return result;

    }

    private Long
    createWorkFlow(WorkOrder workOrder, WorkFlow workFlow){

        if (WorkOrderStatusType.REFUNDING.equals(workFlow.getStatus())
                && null != workOrder.getRefundNo()){
            String oldComments = workFlow.getComments();
            if (null == oldComments || oldComments.isEmpty()){
                oldComments = "{\"refundNo\":"+"\""+workOrder.getRefundNo()+"\"}";
            }
            workFlow.setComments(oldComments);
        }

        workFlowService.save(workFlow);

        log.info("create work_flow {}",JSON.toJSONString(workFlow));
        return workFlow.getId();
    }

    private void
    updateFlowAndWorkorder(WorkOrder workOrder, WorkFlow workFlow,boolean canRollBackStatus) throws Exception{

        if (WorkOrderStatusType.REFUNDING.getCode().equals(workFlow.getStatus())
                && null != workOrder.getRefundNo()){
            String comments = workFlow.getComments();
            if (null == comments || comments.isEmpty()){
                comments = "{\"refundNo\":"+"\""+workOrder.getRefundNo()+"\"}";
            }
            workFlow.setComments(comments);
        }

        workFlowService.save(workFlow);

        log.info("create work_flow {}",JSON.toJSONString(workFlow));
        if (!workFlow.getStatus().equals(workOrder.getStatus())) {
            Long workOrderId = workOrder.getId();
            try {
                if (canRollBackStatus) {
                    //允许工单状态回退
                    workOrder.setStatus(workFlow.getStatus());
                    workOrder.setUpdateTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    workOrderService.updateById(workOrder);
                } else {
                    workOrder = workOrderService.getById(workOrderId);
                    if (null != workOrder) {
                        if (!WorkOrderStatusType.isClosedStatus(workOrder.getStatus())) {
                            workOrder.setStatus(workFlow.getStatus());
                        }
                        workOrder.setUpdateTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                        workOrderService.updateById(workOrder);
                    }
                }
            }catch (Exception e) {
                log.error("数据库操作异常 {}",e.getMessage(),e);
                throw e;
            }
        }
        log.info("Update workOrder success {}",JSON.toJSONString(workOrder));
    }


    private static final String YIYATONG_RESEND_REFUND = "为怡亚通工单补发退款请求";
    @ApiOperation(value = YIYATONG_RESEND_REFUND, notes = "仅仅对怡亚通工单可用")
    @ApiResponses({ @ApiResponse(code = 400, message = "为怡亚通工单补发退款请求失败") })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("work_flows/yiyatong/{workOrderId}")
    public ResultObject<String>
    reSendYiYaTongRefund(HttpServletResponse response,
                        @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                        @PathVariable Long workOrderId) {

        String functionDesc = YIYATONG_RESEND_REFUND;
        log.info("{} 开始",functionDesc);

        String operator = JwtTokenUtil.getUsername(authentication);

        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400004:工单号不存在");
        }
        log.info("所属工单： {}",JSON.toJSONString(workOrder));

        Long merchantId = workOrder.getMerchantId();
        /// 只对对怡亚通订单需要特别处理
        //  * merchantId = 4 为怡亚通订单
        if(Constant.YI_YA_TONG_MERCHANT_ID != merchantId){
            StringUtil.throw400Exp(response, "400104:非怡亚通订单");
        }

        ///不再限制工单状态
        ///boolean canNotResendStatus = !(WorkOrderStatusType.isClosedStatus(workOrder.getStatus()));
        //if (canNotResendStatus) {
        //    StringUtil.throw400Exp(response, "400107:工单状态为关闭的才可以补发退款请求");
        //}

        WorkFlow workFlow = new WorkFlow();
        workFlow.setWorkOrderId(workOrderId);
        workFlow.setCreatedBy(operator);
        workFlow.setStatus(WorkOrderStatusType.RESERVED);

        String customer = workOrder.getReceiverId();
        String orderId = workOrder.getOrderId();
        String comments = "工单原来记录怡亚通退款号:"+workOrder.getGuanaitongTradeNo();

        JSONObject json = null;
        try {
            json = workOrderService.getOrderInfo(customer, orderId, merchantId);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400007:" + e.getMessage());
        }
        if (null == json) {
            StringUtil.throw400Exp(response, "400007: searchOrder失败");
        }

        try {
            AoYiRefundResponseBean bean =
                    workOrderService.getYiYaTongRefundNo("重发退款申请",
                            json.getInteger("subStatus"),
                            json.getString("thirdOrderSn"),
                            json.getString("skuId")
                            );
            if (null != bean) {
                workOrder.setRefundNo(bean.getServiceSn());
                //复用该字段保存星链子订单sn
                workOrder.setGuanaitongTradeNo(bean.getOrderSn());
            } else {
                JSONObject commentsJson = new JSONObject();
                commentsJson.put("remark","怡亚通退款申请失败");
                commentsJson.put("operation",WebSideWorkFlowStatusEnum.UNKNOWN.getCode());
                workFlow.setComments(commentsJson.toJSONString());
                workFlowService.save(workFlow);
                StringUtil.throw400Exp(response, "420101:怡亚通退款申请失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            JSONObject commentsJson = new JSONObject();
            commentsJson.put("remark",e.getMessage());
            commentsJson.put("operation",WebSideWorkFlowStatusEnum.UNKNOWN.getCode());
            workFlow.setComments(commentsJson.toJSONString());
            workFlowService.save(workFlow);

            StringUtil.throw400Exp(response, e.getMessage());
        }

        JSONObject commentsJson = new JSONObject();
        commentsJson.put("remark",comments+" 新申请怡亚通退款号："+workOrder.getGuanaitongTradeNo());
        commentsJson.put("operation",WebSideWorkFlowStatusEnum.UNKNOWN.getCode());
        workFlow.setComments(commentsJson.toJSONString());

        try {
            workFlowService.save(workFlow);
            log.info("未发送请求到怡亚通,仅创建工作流 {}", JSON.toJSONString(workFlow));
        }catch (Exception e){
            log.error("数据库操作异常 {}",e.getMessage(),e);
        }

        return new ResultObject<>(200,"success","为怡亚通工单补发退款请求 成功");
    }


}
