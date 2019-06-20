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


@Api(tags="WorkOrderAPI", description = "工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkOrderController {

    private static Logger logger = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;
    private WorkFlowServiceImpl workFlowService;
    private OrderTypeServiceImpl orderTypeService;

    @ApiModel(value = "工单信息ID")
    private class IdData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @Autowired
    public WorkOrderController(WorkFlowServiceImpl workFlowService,
                               OrderTypeServiceImpl orderTypeService,
                               WorkOrderServiceImpl workOrderService
                             ) {
        this.workOrderService = workOrderService;
        this.workFlowService = workFlowService;
        this.orderTypeService = orderTypeService;
    }

    @ApiOperation(value = "获取指定工单信息", notes = "工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/{id}")
    public WorkOrderBean getWorkOrderById(HttpServletResponse response,
                                 @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                 @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        WorkOrderBean bean = new WorkOrderBean();
        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return bean;
        }

        WorkOrder workOrder = workOrderService.selectById(id);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_order");
            return bean;
        }

        BeanUtils.copyProperties(workOrder, bean);

        response.setStatus(MyErrorMap.e200.getCode());
        return bean;

    }

    @ApiOperation(value = "条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/pages")
    public PageInfo<WorkOrderBean> queryWorkOrders(HttpServletResponse response,
                                                   @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                   @ApiParam(value="页码",required=true)@RequestParam Integer pageIndex,
                                                   @ApiParam(value="每页记录数",required=true)@RequestParam Integer pageSize,
                                                   @ApiParam(value="标题")String title,
                                                   @ApiParam(value="描述")String description,
                                                   @ApiParam(value="订单所属客户")String customer,
                                                   @ApiParam(value="工单指定接待员")String receptionist,
                                                   @ApiParam(value="工单类型ID")Long typeId,
                                                   @ApiParam(value="工单紧急程度")Integer urgentDegree,
                                                   @ApiParam(value="工单状态码")Integer status,
                                                   @ApiParam(value="预计完成时间开始")@RequestParam(required=false) String finishTimeStart,
                                                   @ApiParam(value="预计完成时间结束")@RequestParam(required=false) String finishTimeEnd,
                                                   @ApiParam(value="创建开始时间")@RequestParam(required=false) String createTimeStart,
                                                   @ApiParam(value="创建结束时间")@RequestParam(required=false) String createTimeEnd
                                                     ) {

        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;
        java.util.Date dateFinishTimeStart = null;
        java.util.Date dateFinishTimeEnd = null;

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        if (null == pageIndex || 0 >= pageIndex
                || null == pageSize || 0>= pageSize) {
            StringUtil.throw400Exp(response,"400002:pageIndex or pageSize is wrong");
        }

        try {
            if (null != createTimeStart && !createTimeStart.isEmpty()) {
                dateCreateTimeStart = StringUtil.String2Date(createTimeStart);
            }
            if (null != createTimeEnd && !createTimeEnd.isEmpty()) {
                dateCreateTimeEnd = StringUtil.String2Date(createTimeEnd);
            }
            if (null != finishTimeStart && !finishTimeStart.isEmpty()) {
                dateFinishTimeStart = StringUtil.String2Date(finishTimeStart);
            }
            if (null != finishTimeEnd && !finishTimeEnd.isEmpty()) {
                dateFinishTimeEnd = StringUtil.String2Date(finishTimeEnd);
            }
        } catch (ParseException ex) {
            StringUtil.throw400Exp(response,"400002:dateTime format error");
        }

        PageInfo<WorkOrder> pages = workOrderService.selectPage(pageIndex,pageSize,
                "id", "DESC",title,description,customer,receptionist,typeId,urgentDegree,status,dateFinishTimeStart,dateFinishTimeEnd, dateCreateTimeStart, dateCreateTimeEnd);

        List<WorkOrderBean> list = new ArrayList<>();

        if ((pageIndex -1) * pageSize <= pages.getTotal()) {
            for (WorkOrder a : pages.getRows()) {
                WorkOrderBean b = new WorkOrderBean();
                BeanUtils.copyProperties(a, b);
                list.add(b);
            }
        }
        PageInfo<WorkOrderBean> result = new PageInfo<>(pages.getTotal(), pageSize,pageIndex, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }

    @ApiOperation(value = "创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdData createProfile(HttpServletResponse response,
                                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                            @RequestBody WorkOrderBodyBean data) throws RuntimeException {

        logger.info("create WorkOrder enter");
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        String receptionist = data.getDescription();
        Long typeId = data.getTypeId();
        String finishTimeStr = data.getFinishTime();
        Integer urgentDegree = data.getUrgentDegree();

        if (null == typeId || 0 == typeId ||
            null == orderId || orderId.isEmpty() ||
            null == title || title.isEmpty() ||
            null == description || description.isEmpty()
            ) {
            StringUtil.throw400Exp(response, "400002:工单标题, 工单描述, 工单类型, 所属订单不能空缺");
        }

        OrderType orderType = orderTypeService.selectById(typeId);
        if (null == orderType) {
            StringUtil.throw400Exp(response, "400002:工单类型错误");

        }

        WorkOrder workOrder = new WorkOrder();

        if (null != finishTimeStr && !finishTimeStr.isEmpty()) {
            try {
                Date finishTime = StringUtil.String2Date(finishTimeStr);
                workOrder.setFinishTime(finishTime);
            } catch (ParseException ex) {
                StringUtil.throw400Exp(response,"400002:dateTime format error");
            }
        }

        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setOrderId(orderId);
        workOrder.setTypeId(typeId);

        if (null != customer && !customer.isEmpty()) {
            workOrder.setCustomer(customer);
        }

        if (null != receptionist && !receptionist.isEmpty()) {
            workOrder.setReceptionist(receptionist);
        }

        if (null != urgentDegree) {
            workOrder.setUrgentDegree(urgentDegree);
        }

        workOrder.setCreateTime(new Date());
        workOrder.setUpdateTime(new Date());
        if (null != username) {
            workOrder.setCreatedBy(username);
            workOrder.setUpdatedBy(username);
        }

        result.id = workOrderService.insert(workOrder);
        response.setStatus(MyErrorMap.e201.getCode());

        return result;
    }

    @ApiOperation(value = "更新工单流程信息", notes = "更新工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_orders/{id}")
    public IdData updateProfile(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                @RequestBody WorkOrderBodyBean data) throws RuntimeException {


        logger.info("update WorkOrder");
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        String receptionist = data.getDescription();
        Long typeId = data.getTypeId();
        String workFlow = data.getWorkFlow();
        String finishTimeStr = data.getFinishTime();
        Integer urgentDegree = data.getUrgentDegree();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        WorkOrder workOrder = workOrderService.selectById(id);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:工单不存在");
            return result;
        }

        if (null != finishTimeStr && !finishTimeStr.isEmpty()) {
            try {
                Date finishTime = StringUtil.String2Date(finishTimeStr);
                workOrder.setFinishTime(finishTime);
            } catch (ParseException ex) {
                StringUtil.throw400Exp(response,"400002:dateTime format error");
            }
        }

        if (null != title && !title.isEmpty() ) {
            workOrder.setTitle(title);
        }

        if (null != typeId) {
            OrderType orderType = orderTypeService.selectById(typeId);
            if (null == orderType) {
                StringUtil.throw400Exp(response, "400002:工单类型错误");

            }
            workOrder.setTypeId(typeId);
        }
        if (null != orderId) {
            workOrder.setOrderId(orderId);
        }
        if (null != description && !description.isEmpty()) {
            workOrder.setDescription(description);
        }

        if (null != customer && !customer.isEmpty()) {
            workOrder.setCustomer(customer);
        }

        if (null != receptionist && !receptionist.isEmpty()) {
            workOrder.setReceptionist(receptionist);
        }

        if (null != workFlow && !workFlow.isEmpty()) {
            workOrder.setWorkFlow(workFlow);
        }

        if (null != urgentDegree) {
            workOrder.setUrgentDegree(urgentDegree);
        }

        workOrder.setUpdateTime(new Date());

        if (null != username) {
            workOrder.setUpdatedBy(username);
        }

        workOrderService.update(workOrder);

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        logger.info("update WorkOrder done");
        return result;
    }


    @ApiOperation(value = "删除工单流程信息", notes = "删除工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete WorkOrder's profile") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("work_orders/{id}")
    public void deleteWorkOrder(HttpServletResponse response,
                                          @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                          @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication
                                          ) throws RuntimeException {

        logger.info("delete WorkOrder");

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002: ID is wrong");
            return;
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        WorkOrder workOrder = workOrderService.selectById(id);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003: failed to find record");
        }

        workOrderService.deleteById(id);
        response.setStatus(MyErrorMap.e204.getCode());

        logger.info("delete WorkOrder profile");

    }

    @ApiOperation(value = "获取指定工单流程信息", notes = "工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/{id}/work_flows")
    public List<WorkFlowBean> getWorkFlowById(HttpServletResponse response,
                                          @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                          @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        //String username = JwtTokenUtil.getUsername(authentication);
        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return new ArrayList<>();
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        WorkOrder workOrder = workOrderService.selectById(id);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_order");
            return new ArrayList<>();
        }

        List<WorkFlow> flows = workFlowService.selectByWorkOrderId(workOrder.getId());
        List<WorkFlowBean> list = new ArrayList<>();
        for (WorkFlow a : flows) {
            WorkFlowBean b = new WorkFlowBean();
            BeanUtils.copyProperties(a, b);
            list.add(b);
        }

        response.setStatus(MyErrorMap.e200.getCode());
        return list;

    }

}
