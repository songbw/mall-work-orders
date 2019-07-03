package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
//import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags="CustomerWorkOrderAPI", description = "客户工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/customers/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CustomerWorkOrderController {

    private static Logger logger = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;
    private WorkFlowServiceImpl workFlowService;
    private OrderTypeServiceImpl orderTypeService;

    @ApiModel(value = "工单信息ID")
    private class IdResponseData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @Autowired
    public CustomerWorkOrderController(WorkFlowServiceImpl workFlowService,
                               OrderTypeServiceImpl orderTypeService,
                               WorkOrderServiceImpl workOrderService
    ) {
        this.workOrderService = workOrderService;
        this.workFlowService = workFlowService;
        this.orderTypeService = orderTypeService;
    }

    @ApiOperation(value = "创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdResponseData createWorkOrder(HttpServletResponse response,
                                                    //@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                                    @RequestBody CustomerWorkOrderBean data) throws RuntimeException {

        logger.info("create WorkOrder enter");
        IdResponseData result = new IdResponseData();
        String username = null;//JwtTokenUtil.getUsername(authentication);
        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        Long typeId = data.getTypeId();


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


        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setOrderId(orderId);
        workOrder.setTypeId(typeId);
        workOrder.setStatus(WorkOrderStatusType.PENDING.getCode());

        if (null != customer && !customer.isEmpty()) {
            workOrder.setReceiverId(customer);
        }

        workOrder.setUrgentDegree(1);

        workOrder.setCreateTime(new Date());
        workOrder.setUpdateTime(new Date());
        if (null != username) {
            workOrder.setCreatedBy(username);
            workOrder.setUpdatedBy(username);
        }

        result.id = workOrderService.insert(workOrder);
        if (0 == result.id) {
            StringUtil.throw400Exp(response,"400003:Failed to create work_order");
        }
        response.setStatus(MyErrorMap.e201.getCode());

        return result;
    }

    @ApiOperation(value = "更新工单流程信息", notes = "更新工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_orders/{id}")
    public IdResponseData updateWorkOrder(HttpServletResponse response,
                                                    //@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                                    @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                                    @RequestBody CustomerWorkOrderBean data) throws RuntimeException {


        logger.info("update WorkOrder");
        IdResponseData result = new IdResponseData();
        String username = null; //JwtTokenUtil.getUsername(authentication);
        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        Long typeId = data.getTypeId();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        WorkOrder workOrder = workOrderService.selectById(id);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:工单不存在");
            return result;
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
            workOrder.setReceiverId(customer);
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

    @ApiOperation(value = "条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders")
    public PageInfo<WorkOrderBean> queryWorkOrders(HttpServletResponse response,
                                                   //@RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                   @ApiParam(value="页码",required=false)@RequestParam(required=false) Integer pageIndex,
                                                   @ApiParam(value="每页记录数",required=false)@RequestParam(required=false) Integer pageSize,
                                                   @ApiParam(value="订单所属客户")@RequestParam(required=false) String customer,
                                                   @ApiParam(value="订单ID")@RequestParam(required=false) String orderId) {

/*
        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }
*/
        if (null == pageIndex || 0 >= pageIndex) {
            pageIndex = 1;
        }
        if (null == pageSize || 0>= pageSize) {
            pageSize = 10;
        }

        if (null != customer) {
            customer = customer.trim();
        }
        if (null != orderId) {
            orderId = orderId.trim();
        }

        PageInfo<WorkOrder> pages = workOrderService.selectPage(pageIndex,pageSize,
                "id", "DESC",null,null,customer,orderId,null,null,null,null,null, null, null,null);

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


}
