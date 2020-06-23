package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.constants.WorkOrderType;
import com.fengchao.workorders.dto.ParentOrderRefundData;
import com.fengchao.workorders.dto.WorkFlowBeanList;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.feign.IAoYiClient;
import com.fengchao.workorders.entity.*;
import com.fengchao.workorders.service.db.impl.*;
import com.fengchao.workorders.service.impl.AppSideWorkOrderServiceImpl;
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

    @Autowired
    private AppSideWorkOrderServiceImpl appSideWorkOrderService;

    @ApiModel(value = "工单信息ID")
    private class IdResponseData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @ApiModel(value = "工单信息ID")
    private class ValidNumResponseData implements Serializable {
        @ApiModelProperty(value = "validNum", example = "1", required = true)
        public Integer validNum;

    }


    @ApiOperation(value = "APP查询工单流程")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_flows")
    public ResultObject<WorkFlowBeanList>
    appQueryWorkFlows(@RequestHeader(value="renterId",required = false) String renterId,
                      @ApiParam(value="workOrderId")@RequestParam(required=false)Long workOrderId) {

        log.info("app side queryWorkFlows workOrderId ={} renterId={}",workOrderId,renterId);
        WorkFlowBeanList retResult = appSideWorkOrderService.queryWorkFlows(renterId,workOrderId);
        log.info("app side queryWorkFlows {}",JSON.toJSONString(retResult));
        return new ResultObject<>(retResult);

    }

    @ApiOperation(value = "APP创建工单处理流程信息", notes = "APP创建工单处理流程信息,添加退货物流信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_flows")
    public IdResponseData
    appCreateWorkFlow(HttpServletResponse response,
                      @RequestHeader(value="renterId",required = false) String renterId,
                      @RequestBody WorkFlowBodyBean data) {

        log.info("app side create WorkFlow enter : param {}", JSON.toJSONString(data));
        IdResponseData result = new IdResponseData();
        WorkFlow workFlow = appSideWorkOrderService.handleWorkOrder(renterId,data);
        result.id = workFlow.getId();
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("create WorkFlow and update workOrdersuccess ");
        return result;

    }

    @ApiOperation(value = "APP创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdResponseData
    appCreateWorkOrder(HttpServletResponse response,
                       @RequestHeader(value="renterId",required = false) String renterId,
                       @RequestBody CustomerWorkOrderBean data) {

        log.info("app side createWorkOrder: {}", JSON.toJSONString(data));
        IdResponseData result = new IdResponseData();

        WorkOrder workOrder = appSideWorkOrderService.createWorkOrder(renterId,data);
        response.setStatus(MyErrorMap.e201.getCode());
        result.id = workOrder.getId();

        log.info("createWorkOrder success {} ", JSON.toJSONString(workOrder));
        return result;

    }

    @ApiOperation(value = "APP更新工单信息", notes = "更新工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_orders/{id}")
    public IdResponseData
    appUpdateWorkOrder(HttpServletResponse response,
                       @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                       @RequestHeader(value="renterId",required = false) String renterId,
                       @RequestBody CustomerWorkOrderBean data) {

        log.info("app side updateWorkOrder: id={}, param: {}",id, JSON.toJSONString(data));

        IdResponseData result = new IdResponseData();
        WorkOrder updatedWorkOrder = appSideWorkOrderService.updateWorkOrder(renterId,data,id);

        result.id = updatedWorkOrder.getId();
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkOrder done");
        return result;
    }




    @ApiOperation(value = "APP条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders")
    public PageInfo<CustomerQueryWorkOrderBean>
    appQueryWorkOrders(@RequestHeader(value="renterId",required = false) String renterId,
                       @ApiParam(value="页码")@RequestParam(defaultValue = "1") Integer pageIndex,
                       @ApiParam(value="每页记录数")@RequestParam(defaultValue = "10") Integer pageSize,
                       @ApiParam(value="订单所属客户")@RequestParam(required=false) String customer,
                       @ApiParam(value="订单ID")@RequestParam(required=false) String orderId) {

        return appSideWorkOrderService.queryWorkOrder(renterId,pageIndex,pageSize,customer,orderId);
    }


    @ApiOperation(value = "APP查询订单可退数量", notes = "查询订单可退数量")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("orders/validNum")
    public ValidNumResponseData
    appQueryOrderValidNum(@RequestHeader(value="renterId",required = false) String renterId,
                          @ApiParam(value="订单所属客户",required=true)@RequestParam String customer,
                          @ApiParam(value="订单ID",required=true)@RequestParam String orderId,
                          @ApiParam(value="merchantId",required=true)@RequestParam Long merchantId
    ) {

        ValidNumResponseData result = new ValidNumResponseData();
        result.validNum = appSideWorkOrderService.getRefundValidCount(renterId,customer,orderId,merchantId);
        return result;
    }

    @ApiOperation(value = "APP查询订单售后详情", notes = "APP查询订单售后详情")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("orders/allRefunds")
    public ResultObject<ParentOrderRefundData>
    appQueryParentOrderRefund(@RequestHeader(value="renterId",required = false) String renterId,
                              @ApiParam(value="子订单ID")@RequestParam(required = false) String orderId) {

        return new ResultObject<>(appSideWorkOrderService.getParentOrderRefund(renterId,orderId));

    }

}
