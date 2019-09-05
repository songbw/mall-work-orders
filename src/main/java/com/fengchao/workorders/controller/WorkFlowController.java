package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.bean.*;
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
        Integer handleFare = data.getHandleFare();

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
        if (WorkOrderStatusType.CLOSED.getCode().equals(orderStatus) || WorkOrderStatusType.REJECT.getCode().equals(orderStatus)) {
            StringUtil.throw400Exp(response, "400007:工单状态为审核失败或处理完成时不可更改");
            return result;
        }

        if (null == nextStatus || WorkOrderStatusType.Int2String(nextStatus).isEmpty()) {
            StringUtil.throw400Exp(response, "400005:状态码错误");
            return result;
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

        if ((WorkOrderType.RETURN.getCode().equals(workTypeId) || WorkOrderType.REFUND.getCode().equals(workTypeId)) &&
             (WorkOrderStatusType.CLOSED.getCode().equals(nextStatus) && (WorkOrderStatusType.ACCEPTED.getCode().equals(orderStatus) ||
               WorkOrderStatusType.HANDLING.getCode().equals(orderStatus)))) {

            String iAppId = workOrder.getiAppId();
            String tAppId = workOrder.gettAppId();



            if ("10".equals(iAppId) && null != tAppId) {
                String guanAiTongTradeNo;
                try {
                    guanAiTongTradeNo = workOrderService.sendRefund2GuangAiTong(workOrderId, handleFare);
                } catch (Exception e) {
                    StringUtil.throw400Exp(response, "400006:" + e.getMessage());
                    return null;
                }
                if (null == guanAiTongTradeNo) {
                    StringUtil.throw400Exp(response, "400004: failed to find work-order");
                    return result;
                } else {
                    if (guanAiTongTradeNo.isEmpty()) {
                        StringUtil.throw400Exp(response, "400004: send to GuanAiTong failed");
                        return result;
                    } else {
                        if (guanAiTongTradeNo.contains("Error:")) {
                            String errMsg = guanAiTongTradeNo.replace(':', '-');
                            StringUtil.throw400Exp(response, "400006: " + errMsg);
                            return result;
                        }
                    }
                }
            }
        }

        try {
            result.id = workFlowService.insert(workFlow);
            log.info("create WorkFlow success, id = {}", result.id );
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400003:Failed to create work_flow");
            return result;
        }

        if (!workFlow.getStatus().equals(workOrder.getStatus())) {
            workOrder.setStatus(workFlow.getStatus());

            try {
                workOrderService.update(workOrder);
            }catch (Exception e) {
                StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                return null;
            }
        }

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
