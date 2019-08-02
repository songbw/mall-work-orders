package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Api(tags="CustomerWorkOrderAPI", description = "客户工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/customers/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CustomerWorkOrderController {

    //private static Logger log = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;

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

    @Autowired
    public CustomerWorkOrderController(WorkOrderServiceImpl workOrderService
                                        ) {
        this.workOrderService = workOrderService;
    }

    @ApiOperation(value = "创建工单信息", notes = "创建工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("work_orders")
    public IdResponseData createWorkOrder(HttpServletResponse response,
                                          //@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                          @RequestBody CustomerWorkOrderBean data) {

        log.info("app side createWorkOrder: " + data.toString());
        IdResponseData result = new IdResponseData();

        String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        String customer = data.getCustomer();
        Integer typeId = data.getTypeId();
        Long merchantId = data.getMerchantId();
        Integer num = data.getNum();

        if (null == orderId || orderId.isEmpty() ) {
            StringUtil.throw400Exp(response, "400002:所属订单不能空缺");
            return result;
        }
        if (null == customer || customer.isEmpty() ) {
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

        WorkOrder selectedWO = workOrderService.getValidNumOfOrder(customer, orderId);
        if (null == selectedWO) {//totally new order
            log.info("there is not work order of this orderId: " + orderId);
            JSONObject json = workOrderService.getOrderInfo(customer, orderId, merchantId);
            if (null == json) {
                StringUtil.throw400Exp(response, "400007:所属订单信息错误");
                return result;
            }

            String paymentNo = json.getString("paymentNo");
            if (null == paymentNo) {
                log.info("get order info err: paymentNo is null");
                return result;
            } else {
                workOrder.setTradeNo(paymentNo);
            }

            Float salePrice = json.getFloat("salePrice");
            if (null == salePrice) {
                log.info("get order info err: salePrice is null");
                return result;
            } else {
                workOrder.setSalePrice(salePrice);
            }

            Integer orderNum = json.getInteger("num");
            if (null == orderNum) {
                log.info("get order info err: num is null");
                return result;
            } else {
                workOrder.setOrderGoodsNum(orderNum);
            }

            String mobile = json.getString("mobile");
            if (null != mobile) {
                workOrder.setReceiverPhone(mobile);
            }
            String receiverName = json.getString("receiverName");
            if (null != receiverName) {
                workOrder.setReceiverName(receiverName);
            }
        } else {
            if (0 >= selectedWO.getReturnedNum() || num > selectedWO.getReturnedNum()) {
                StringUtil.throw400Exp(response, "400006:所属订单退货数量已满");
                return result;
            }

            workOrder.setTradeNo(selectedWO.getTradeNo());
            workOrder.setSalePrice(selectedWO.getSalePrice());
            workOrder.setOrderGoodsNum(selectedWO.getOrderGoodsNum());
            workOrder.setReceiverPhone(selectedWO.getReceiverPhone());
            workOrder.setReceiverName(selectedWO.getReceiverName());

        }

        workOrder.setAppid(Constant.APPID_VALUE);
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setOrderId(orderId);
        workOrder.setReturnedNum(num);
        workOrder.setRefundAmount(num * workOrder.getSalePrice());
        workOrder.setTypeId((long)typeId);
        workOrder.setMerchantId(merchantId);
        workOrder.setStatus(WorkOrderStatusType.EDITING.getCode());
        workOrder.setReceiverId(customer);
        workOrder.setUrgentDegree(1);
        workOrder.setCreateTime(new Date());
        workOrder.setUpdateTime(new Date());


        //String username = null;//JwtTokenUtil.getUsername(authentication);
        //if (null != username) {
        //    workOrder.setCreatedBy(username);
        //    workOrder.setUpdatedBy(username);
        //}

        try {
            result.id = workOrderService.insert(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400008:Failed to create work_order");
        }
        response.setStatus(MyErrorMap.e201.getCode());

        log.info("createWorkOrder success, id = " +result.id.toString());
        return result;

    }

    @ApiOperation(value = "更新工单流程信息", notes = "更新工单流程信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("work_orders/{id}")
    public IdResponseData updateWorkOrder(HttpServletResponse response,
                                                    //@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                                    @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                                    @RequestBody CustomerWorkOrderBean data) {

        log.info("app side updateWorkOrder: id = " + id);
        log.info("app side updateWorkOrder: " + data.toString());

        IdResponseData result = new IdResponseData();
        WorkOrder workOrder = null;
        String username = null; //JwtTokenUtil.getUsername(authentication);
        //String orderId = data.getOrderId();
        String title = data.getTitle();
        String description = data.getDescription();
        //String customer = data.getCustomer();
        //Integer typeId = data.getTypeId();
        //Long merchantId = data.getMerchantId();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        try {
            workOrder = workOrderService.selectById(id);

            if (null == workOrder) {
                StringUtil.throw400Exp(response, "400003:工单不存在");
                return result;
            }
        } catch (Exception ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
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

                workOrder.setTypeId((long)typeId);
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

        if (null != merchantId) {
            workOrder.setMerchantId(merchantId);
        }
        */
        workOrder.setUpdateTime(new Date());

        if (null != username) {
            workOrder.setUpdatedBy(username);
        }

        try {
            workOrderService.update(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkOrder done");
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
            log.warn("can not find username in token");
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
                "id", "DESC",null,customer,null,null,orderId,null,null,null,null, null, null,null);

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


    @ApiOperation(value = "查询订单可退数量", notes = "查询订单可退数量")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("orders/validNum")
    public ValidNumResponseData queryOrderValidNum(HttpServletResponse response,
                                                   @ApiParam(value="订单所属客户",required=true)@RequestParam(required=true) String customer,
                                                   @ApiParam(value="订单ID",required=true)@RequestParam(required=true) String orderId,
                                                   @ApiParam(value="merchantId",required=true)@RequestParam(required=true) Long merchantId
                                                   ) {

        ValidNumResponseData result = new ValidNumResponseData();


        WorkOrder workOrder = workOrderService.getValidNumOfOrder(customer, orderId);

        response.setStatus(MyErrorMap.e200.getCode());
        if (null != workOrder) {
            result.validNum = (0>= workOrder.getReturnedNum()?0:workOrder.getReturnedNum());
            return result;
        }

        log.info("there is not work order of this orderId: " + orderId);
        JSONObject json = workOrderService.getOrderInfo(customer, orderId, merchantId);
        if (null == json) {
            StringUtil.throw400Exp(response, "400003:所属订单信息错误");
            return result;
        }

        Integer orderNum = json.getInteger("num");
        if (null == orderNum) {
            log.info("get order info err: num is null");
            StringUtil.throw400Exp(response, "400004:get order info err: num is null");
            return result;
        } else {
            result.validNum = orderNum;
        }
        return result;

    }

}
