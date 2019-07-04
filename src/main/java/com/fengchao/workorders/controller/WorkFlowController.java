package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.*;
//import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


@Api(tags="WorkFlowAPI", description = "工单流程管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkFlowController {

    private static Logger logger = LoggerFactory.getLogger(WorkFlowController.class);

    private WorkFlowServiceImpl workFlowService;
    private WorkOrderServiceImpl workOrderService;

    @ApiModel(value = "工单流程信息ID")
    private class IdData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @ApiModel(value = "工单流程List")
    private class ListData implements Serializable {

        public List<WorkFlowBean> list;

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
            logger.warn("can not find username in token");
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

        WorkFlowBean bean = new WorkFlowBean();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return bean;
        }
        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }
        WorkFlow workFlow = workFlowService.selectById(id);
        if (null == workFlow) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_flow");
            return bean;
        }

        BeanUtils.copyProperties(workFlow, bean);

        response.setStatus(MyErrorMap.e200.getCode());
        return bean;

    }

    @ApiOperation(value = "条件查询工单流程", notes = "查询工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows/pages")
    public PageInfo<WorkFlowBean> queryWorkFlows(HttpServletResponse response,
                                                     @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                     @ApiParam(value="页码",required=false)@RequestParam(required=false) Integer pageIndex,
                                                     @ApiParam(value="每页记录数",required=false)@RequestParam(required=false) Integer pageSize,
                                                     @ApiParam(value="创建开始时间")@RequestParam(required=false) String createTimeStart,
                                                     @ApiParam(value="创建结束时间")@RequestParam(required=false) String createTimeEnd,
                                                     @ApiParam(value="workOrderId")@RequestParam(required=false)Long workOrderId) {

        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        if (null == pageIndex || 0 >= pageIndex) {
            pageIndex = 1;
        }
        if (null == pageSize || 0>= pageSize) {
            pageSize = 10;
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

        PageInfo<WorkFlow> pages = workFlowService.selectPage(pageIndex,pageSize,
                "id", "DESC",workOrderId,dateCreateTimeStart, dateCreateTimeEnd);

        List<WorkFlowBean> list = new ArrayList<>();

        if ((pageIndex -1) * pageSize <= pages.getTotal()) {
            for (WorkFlow a : pages.getRows()) {
                WorkFlowBean b = new WorkFlowBean();
                BeanUtils.copyProperties(a, b);
                list.add(b);
            }
        }

        PageInfo<WorkFlowBean> result = new PageInfo<>(pages.getTotal(), pageSize,pageIndex, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }

    @ApiOperation(value = "创建工单流程信息", notes = "创建工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows")
    public IdData createProfile(HttpServletResponse response,
                                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                            @RequestBody WorkFlowBodyBean data) throws RuntimeException {

        logger.info("create WorkFlow enter");
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        Long workOrderId = data.getWorkOrderId();
        Integer status = data.getStatus();
        String comments = data.getComments();

        if (null == workOrderId || 0 == workOrderId
            ) {
            StringUtil.throw400Exp(response, "400002:工单号不能为空");
            return result;
        }

        WorkOrder workOrder = workOrderService.selectById(workOrderId);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400002:工单号不存在");
            return result;
        }

        WorkFlow workFlow = new WorkFlow();

        workFlow.setWorkOrderId(workOrderId);

        if (null != status && !WorkOrderStatusType.Int2String(status).isEmpty()) {
            workFlow.setStatus(status);
            workOrder.setStatus(status);
        }

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }
        workFlow.setCreateTime(new Date());
        workFlow.setUpdateTime(new Date());
        if (null != username && !username.isEmpty()) {
            workFlow.setCreatedBy(username);
            workFlow.setUpdatedBy(username);
        }

        try {
            workOrderService.update(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
        }

        try {
            result.id = workFlowService.insert(workFlow);
        }  catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400003:Failed to create work_flow");
        }
        response.setStatus(MyErrorMap.e201.getCode());

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

        logger.info("update WorkFlow");
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        String comments = data.getComments();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        WorkFlow workFlow = workFlowService.selectById(id);
        if (null == workFlow) {
            StringUtil.throw400Exp(response, "400003:工单流程不存在");
            return result;
        }

        workFlow.setUpdateTime(new Date());

        if (null != username && !username.isEmpty()) {
            workFlow.setUpdatedBy(username);
        }

        if (null != comments && !comments.isEmpty()) {
            workFlow.setComments(comments);
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        logger.info("update WorkFlow done");
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

        logger.info("delete WorkFlow");

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002: ID is wrong");
            return;
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        WorkFlow workFlow = workFlowService.selectById(id);
        if (null == workFlow) {
            StringUtil.throw400Exp(response, "400003: failed to find record");
        }

        workFlowService.deleteById(id);
        response.setStatus(MyErrorMap.e204.getCode());

        logger.info("delete WorkFlow profile");

    }

}
