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

    @ApiModel(value = "工作流程信息列表")
    private class WorkFlowListData implements Serializable {
        @ApiModelProperty(value = "流程信息列表", example = "", required = true)
        public List<WorkFlowBean> list;

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
                                                   @ApiParam(value="页码",required=false)@RequestParam(required=false) Integer pageIndex,
                                                   @ApiParam(value="每页记录数",required=false)@RequestParam(required=false) Integer pageSize,
                                                   @ApiParam(value="标题")@RequestParam(required=false) String title,
                                                   @ApiParam(value="订单ID")@RequestParam(required=false) String orderId,
                                                   @ApiParam(value="客户ID")@RequestParam(required=false) String receiverId,
                                                   @ApiParam(value="客户电话")@RequestParam(required=false) String receiverPhone,
                                                   @ApiParam(value="客户名称")@RequestParam(required=false) String receiverName,
                                                   @ApiParam(value="工单类型ID")@RequestParam(required=false) Long typeId,
                                                   @ApiParam(value="商户ID")@RequestParam(required=false) Long merchantId,
                                                   @ApiParam(value="工单状态码")@RequestParam(required=false) Integer status
                                                     ) {

        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;
        java.util.Date dateFinishTimeStart = null;
        java.util.Date dateFinishTimeEnd = null;

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

        PageInfo<WorkOrder> pages = workOrderService.selectPage(pageIndex,pageSize,"id", "DESC",
                                title,receiverId,receiverName,receiverPhone,orderId,typeId,merchantId,
                                status,dateFinishTimeStart,dateFinishTimeEnd, dateCreateTimeStart, dateCreateTimeEnd);

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
        String receiverid = data.getReceiverId();
        String receiverPhone = data.getReceiverPhone();
        String receiverName = data.getReceiverName();
        Long merchantId = data.getMerchantId();
        Long typeId = data.getTypeId();
        String finishTimeStr = data.getFinishTime();
        Integer urgentDegree = data.getUrgentDegree();

        if (null == typeId || 0 == typeId ||
            null == title || title.isEmpty()
            ) {
            StringUtil.throw400Exp(response, "400002:工单标题, 工单类型不能空缺");
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
        workOrder.setStatus(WorkOrderStatusType.PENDING.getCode());

        if (null != receiverid && !receiverid.isEmpty()) {
            workOrder.setReceiverId(receiverid);
        }

        if (null != receiverPhone && !receiverPhone.isEmpty()) {
            workOrder.setReceiverPhone(receiverPhone);
        }

        if (null != receiverName && !receiverName.isEmpty()) {
            workOrder.setReceiverName(receiverName);
        }

        if (null != merchantId) {
            workOrder.setMerchantId(merchantId);
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
        String receiverid = data.getReceiverId();
        String receiverPhone = data.getReceiverPhone();
        String receiverName = data.getReceiverName();
        Long merchantId = data.getMerchantId();
        Long typeId = data.getTypeId();
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

        if (null != receiverid && !receiverid.isEmpty()) {
            workOrder.setReceiverId(receiverid);
        }

        if (null != receiverPhone && !receiverPhone.isEmpty()) {
            workOrder.setReceiverPhone(receiverPhone);
        }

        if (null != receiverName && !receiverName.isEmpty()) {
            workOrder.setReceiverName(receiverName);
        }

        if (null != merchantId) {
            workOrder.setMerchantId(merchantId);
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
            StringUtil.throw400Exp(response, "400002: failed to find record");
        }

        workOrderService.deleteById(id);
        response.setStatus(MyErrorMap.e204.getCode());

        logger.info("delete WorkOrder profile");

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
            logger.warn("can not find username in token");
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
