package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.model.*;
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
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags="WorkFlowAPI", description = "工单流程管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkFlowController {

    //private static Logger log = LoggerFactory.getLogger(WorkFlowController.class);

    private WorkFlowServiceImpl workFlowService;
    private WorkOrderServiceImpl workOrderService;

    @ApiModel(value = "工单流程信息ID")
    private class IdData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @Autowired
    public WorkFlowController(WorkOrderServiceImpl workOrderService,
                            WorkFlowServiceImpl workFlowService
                             ) {
        this.workFlowService = workFlowService;
        this.workOrderService = workOrderService;
    }

    /*
    @ApiOperation(value = "获取工单流程列表", notes = "获取工单流程列表")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/list")
    public ListData getList(HttpServletResponse response,
                            @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication
                            ) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }

        List<WorkFlow> WorkFlows = workFlowService.selectAll();
        List<WorkFlowBean> list = new ArrayList<>();
        for (WorkFlow a : WorkFlows) {
            WorkFlowBean b = new WorkFlowBean();
            BeanUtils.copyProperties(a, b);
            list.add(b);
        }

        ListData result = new ListData();
        result.list = list;
        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }
*/
    @ApiOperation(value = "获取指定工单流程信息", notes = "工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/{id}")
    public WorkFlowBean getWorkFlowById(HttpServletResponse response,
                                 @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                 @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        log.info("getWorkFlowById param : {}",id.toString());
        WorkFlowBean bean = new WorkFlowBean();

        if (0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return bean;
        }
        String username = JwtTokenUtil.getUsername(authentication);

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }
        WorkFlow workFlow =null;
        try {
            workFlow = workFlowService.selectById(id);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
        }
        if (null == workFlow) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_flow");
            return bean;
        }

        BeanUtils.copyProperties(workFlow, bean);
        bean.setOperator(workFlow.getUpdatedBy());

        response.setStatus(MyErrorMap.e200.getCode());
        log.info("getWorkFlowById exit success");
        return bean;

    }

    @ApiOperation(value = "条件查询工单流程", notes = "查询工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/pages")
    public PageInfo<WorkFlowBean> queryWorkFlows(HttpServletResponse response,
                                                     @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                     @ApiParam(value="页码")@RequestParam(required=false) Integer pageIndex,
                                                     @ApiParam(value="每页记录数")@RequestParam(required=false) Integer pageSize,
                                                     @ApiParam(value="创建开始时间")@RequestParam(required=false) String createTimeStart,
                                                     @ApiParam(value="创建结束时间")@RequestParam(required=false) String createTimeEnd,
                                                     @ApiParam(value="workOrderId")@RequestParam(required=false)Long workOrderId) {

        log.info("queryWorkFlows workOrderId ={}",workOrderId);
        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        String username = JwtTokenUtil.getUsername(authentication);

        int index = (null == pageIndex || 0 >= pageIndex)?1:pageIndex;
        int limit = (null == pageSize || 0>= pageSize)?10:pageSize;
        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }

        try {
            if (null != createTimeStart && !createTimeStart.isEmpty()) {
                dateCreateTimeStart = StringUtil.String2Date(createTimeStart);
            }
            if (null != createTimeEnd && !createTimeEnd.isEmpty()) {
                dateCreateTimeEnd = StringUtil.String2Date(createTimeEnd);
            }
        } catch (ParseException ex) {
            StringUtil.throw400Exp(response,"400002:createTime format is wrong");
        }

        PageInfo<WorkFlow> pages;
        try {
            pages = workFlowService.selectPage(index, limit,
                    "id", "DESC", workOrderId, dateCreateTimeStart, dateCreateTimeEnd);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        List<WorkFlowBean> list = new ArrayList<>();

        if ((index -1) * pages.getPageSize() <= pages.getTotal()) {
            for (WorkFlow a : pages.getRows()) {
                WorkFlowBean b = new WorkFlowBean();
                BeanUtils.copyProperties(a, b);
                b.setOperator(a.getUpdatedBy());
                list.add(b);
            }
        }

        PageInfo<WorkFlowBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),index, list);

        response.setStatus(MyErrorMap.e200.getCode());
        log.info("queryWorkFlows success");
        return result;

    }

    private void
    updateFlowAndWorkorder(WorkOrder workOrder, WorkFlow workFlow,boolean canRollBackStatus) throws Exception{
        Long flowId;
        workFlow.setCreateTime(new Date());
        workFlow.setUpdateTime(new Date());
        if (WorkOrderStatusType.REFUNDING.getCode().equals(workFlow.getStatus())
                && null != workOrder.getRefundNo()){
            String comments = workFlow.getComments();
            if (null == comments || comments.isEmpty()){
                comments = "{\"refundNo\":"+"\""+workOrder.getRefundNo()+"\"}";
            }
            workFlow.setComments(comments);
        }

        try {
            flowId = workFlowService.insert(workFlow);
            log.info("create WorkFlow success, id = {}", flowId );
        }catch (Exception e) {
            log.error("数据库操作异常 {}",e.getMessage());
            throw e;
        }

        if (0 == flowId) {
            throw new Exception("Failed to create work_flow");
        }

        log.info("create work_flow {}",JSON.toJSONString(workFlow));
        if (!workFlow.getStatus().equals(workOrder.getStatus())) {
            Long workOrderId = workOrder.getId();
            try {
                if (canRollBackStatus) {
                    //允许工单状态回退
                    workOrder.setStatus(workFlow.getStatus());
                    workOrder.setUpdateTime(new Date());
                    workOrderService.update(workOrder);
                } else {
                    workOrder = workOrderService.selectById(workOrderId);
                    if (null != workOrder) {
                        if (!WorkOrderStatusType.CLOSED.getCode().equals(workOrder.getStatus())) {
                            workOrder.setStatus(workFlow.getStatus());
                        }
                        workOrder.setUpdateTime(new Date());
                        workOrderService.update(workOrder);
                    }

                }
            }catch (Exception e) {
                log.error(e.getMessage(),e);
                throw e;
            }
        }
        log.info("update work_order {}",JSON.toJSONString(workOrder));
    }

    @ApiOperation(value = "特别处理重新打开工单创建工单流程信息", notes = "特别处理重新打开工单创建工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "特别处理重新打开工单失败") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows/work_order")
    public IdData reNewProfile(HttpServletResponse response,
                               @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                               @RequestBody WorkFlowBodyBean data) {

        log.info("特别处理重新打开工单 入参 {}", JSON.toJSONString(data));
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        Long workOrderId = data.getWorkOrderId();
        Integer nextStatus = WorkOrderStatusType.EDITING.getCode();
        String comments = data.getComments();
        String operator = data.getOperator();
        Integer typeId = data.getTypeId();
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
        log.info("特别处理重新打开工单： {}",JSON.toJSONString(workOrder));

        Integer orderStatus = workOrder.getStatus();
        if (!WorkOrderStatusType.CLOSED.getCode().equals(orderStatus)) {
            StringUtil.throw400Exp(response, "400007:工单状态为处理完成时才可以重新打开");
            return result;
        }

        WorkFlow workFlow = new WorkFlow();

        workFlow.setWorkOrderId(workOrderId);
        workFlow.setUpdatedBy(operator);

        workFlow.setStatus(nextStatus);

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }
        if (null != typeId){
            if (null == comments) {
                String tmpStr = " 更改工单类型,从" + workOrder.getTypeId().toString() + " 到 " + typeId.toString();
                String oldComments = workFlow.getComments();
                workFlow.setComments(oldComments + tmpStr);
            }
            workOrder.setTypeId(typeId);
            if (WorkOrderType.EXCHANGE.getCode().equals(typeId)){
                workOrder.setRefundAmount(0.00F);
            }else {
                workOrder.setRefundAmount(refund);
            }
        }

        if (null != username && !username.isEmpty()) {
            workFlow.setCreatedBy(username);
        }

        //workOrder.setRefundNo("");
        //workOrder.setExpressNo("");
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

        log.info("create WorkFlow enter : param {}", JSON.toJSONString(data));
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        Long workOrderId = data.getWorkOrderId();
        Integer nextStatus = data.getStatus();
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

        log.info("create WorkFlow 获取到工单信息： {}",JSON.toJSONString(workOrder));
        String iAppId = workOrder.getiAppId();
        //String tAppId = workOrder.gettAppId();
        Integer orderStatus = workOrder.getStatus();
        if (WorkOrderStatusType.CLOSED.getCode().equals(orderStatus) || WorkOrderStatusType.REJECT.getCode().equals(orderStatus)) {
            StringUtil.throw400Exp(response, "400007:工单状态为审核失败或处理完成时不可更改");
            return result;
        }

        if (null == nextStatus || WorkOrderStatusType.Int2String(nextStatus).isEmpty()) {
            StringUtil.throw400Exp(response, "400005:状态码错误");
            return result;
        }
        if (WorkOrderStatusType.REFUNDING.getCode().equals(nextStatus)){
            if (null == refund){
                StringUtil.throw400Exp(response, "400008:退款金额缺失");
                return result;
            }
            if (workOrder.getTypeId().equals(WorkOrderType.EXCHANGE.getCode())){
                StringUtil.throw400Exp(response, "400009:换货工单不能做退款");
                return result;
            }
        }

        if (null != refund && 0 < refund) {
            BigDecimal decRefund = new BigDecimal(refund);
            BigDecimal decRefundStored = new BigDecimal(workOrder.getRefundAmount());
            Float fare = workOrder.getFare();
            boolean hasFare = (null != fare) && (0.009 < fare);
            if (hasFare) {
                BigDecimal decFare = new BigDecimal(workOrder.getFare());
                decRefundStored = decRefundStored.add(decFare);
            }
            NumberFormat formatter = new DecimalFormat("0");
            BigDecimal dec100f = new BigDecimal("100");
            String strRefundStored = formatter.format(decRefundStored.multiply(dec100f).floatValue());
            String strRefund = formatter.format(decRefund.multiply(dec100f).floatValue());
            log.info("工单：{} 可退款最高额度={}分 , 本次要求退款额度={}分",
                    workOrder.getId().toString(), strRefundStored, strRefund);
            if (0 < strRefund.compareTo(strRefundStored)) {
                StringUtil.throw400Exp(response, "400008:退款金额超出合理范围");
                return result;
            }
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
        if (!username.isEmpty()) {
            workFlow.setCreatedBy(username);
        }

        Integer workTypeId = workOrder.getTypeId();

        if (WorkOrderStatusType.CLOSED.getCode().equals(nextStatus)){//直接关闭工单
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

        String configIAppIds = GuanAiTongConfig.getConfigGatIAppId();
        boolean isGat = false;
        if (null != configIAppIds && !configIAppIds.isEmpty() && configIAppIds.equals(iAppId)) {
            isGat = true;
        }
        log.info("create WorkFlow isGat={}",isGat);
        if ((WorkOrderType.RETURN.getCode().equals(workTypeId) || WorkOrderType.REFUND.getCode().equals(workTypeId)) &&
             (WorkOrderStatusType.REFUNDING.getCode().equals(nextStatus) && (WorkOrderStatusType.ACCEPTED.getCode().equals(orderStatus) ||
               WorkOrderStatusType.HANDLING.getCode().equals(orderStatus)))) {

            if (isGat) {
                String guanAiTongTradeNo;
                try {
                    guanAiTongTradeNo = workOrderService.sendRefund2GuangAiTong(workOrderId, handleFare, refund);
                } catch (Exception e) {
                    StringUtil.throw400Exp(response, "400006:" + e.getMessage());
                    return null;
                }
                if (null == guanAiTongTradeNo) {
                    log.error("关爱通退款 未返回交易号");
                    StringUtil.throw400Exp(response, "400009: failed to get guanAiTongTradeNo in result of response");
                    return result;
                } else {
                    if (guanAiTongTradeNo.isEmpty()) {
                        log.error("关爱通退款 未返回交易号");
                        StringUtil.throw400Exp(response, "400009: failed to get guanAiTongTradeNo in result of response");
                        return result;
                    } else {
                        if (guanAiTongTradeNo.contains("Error:")) {
                            String errMsg = guanAiTongTradeNo.replace(':', '-');
                            StringUtil.throw400Exp(response, "400009: " + errMsg);
                            return result;
                        }
                    }
                }
            }

            Long flowId = 0L;
            try {
                flowId = workFlowService.insert(workFlow);
                log.info("create WorkFlow success, id = {}", flowId );
            }catch (Exception e) {
                log.error("数据库操作异常 {}",e.getMessage(),e);
            }

            if (0L == flowId) {
                log.error("Failed to create work_flow");
            } else {
                log.info("create work_flow {}", JSON.toJSONString(workFlow));
                result.id = workFlow.getId();
            }
            response.setStatus(MyErrorMap.e201.getCode());

            return result;

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

    @ApiOperation(value = "更新工单流程信息", notes = "更新工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_flows/{id}")
    public IdData updateProfile(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                @RequestBody WorkFlowBodyBean data) throws RuntimeException {

        log.info("update WorkFlow param : {}", JSON.toJSONString(data));
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        String comments = data.getComments();
        String operator = data.getOperator();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        WorkFlow workFlow;
        try {
            workFlow = workFlowService.selectById(id);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == workFlow) {
            StringUtil.throw400Exp(response, "400003:工单流程不存在");
            return result;
        }

        workFlow.setUpdateTime(new Date());

        if (!username.isEmpty()) {
            workFlow.setUpdatedBy(username);
        } else {
            if (null != operator && !operator.isEmpty()) {
                workFlow.setUpdatedBy(operator);
            }
        }

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkFlow done");
        return result;
    }


    @ApiOperation(value = "删除工单流程信息", notes = "删除工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete WorkFlow's profile") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("work_flows/{id}")
    public void deleteWorkFlow(HttpServletResponse response,
                                          @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                          @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication
                                          ) throws RuntimeException {

        log.info("delete WorkFlow id = {}",id);

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002: ID is wrong");
            return;
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }


        WorkFlow workFlow;
        try {
            workFlow = workFlowService.selectById(id);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400006: "+e.getMessage());
            return;
        }
        if (null == workFlow) {
            StringUtil.throw400Exp(response, "400003: failed to find record");
            return;
        }

        try {
            workFlowService.deleteById(id);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return;
        }
        response.setStatus(MyErrorMap.e204.getCode());

        log.info("delete WorkFlow success");

    }

}
