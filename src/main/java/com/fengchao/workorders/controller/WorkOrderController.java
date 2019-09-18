package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.*;
//import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.service.impl.*;
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
//import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(tags="WorkOrderAPI", description = "工单管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WorkOrderController {

    //private static Logger = LoggerFactory.getLogger(WorkOrderController.class);

    private WorkOrderServiceImpl workOrderService;

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
    public WorkOrderController(WorkOrderServiceImpl workOrderService
                             ) {
        this.workOrderService = workOrderService;
    }

    private Date getDateType(String timeStr, boolean isEnd) throws Exception{

        if (null == timeStr) {
            return null;
        }
        if (timeStr.isEmpty()) {
            return null;
        }

        String timeString = timeStr.trim();
        Date dateTime;

       if (10 > timeString.length()) {
                return null;
       }

       if (10 == timeString.length()) {
           if (isEnd) {
               timeString += " 23:59:59";
           }else {
               timeString += " 00:00:00";
           }
       }

       try {
           dateTime = StringUtil.String2Date(timeString);
       } catch (ParseException ex) {
           log.error("timeString is wrong {}",ex.getMessage());
           throw new Exception(ex);
       }

       return dateTime;
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

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return bean;
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(id);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:Failed to find work_order");
            return bean;
        }

        BeanUtils.copyProperties(workOrder, bean);
        if (null != workOrder.getGuanaitongRefundAmount()) {
            bean.setRealRefundAmount(workOrder.getGuanaitongRefundAmount());
        }
        response.setStatus(MyErrorMap.e200.getCode());
        return bean;

    }

    @ApiOperation(value = "条件查询工单", notes = "查询工单信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("work_orders/pages")
    public PageInfo<WorkOrderBean> queryWorkOrders(HttpServletResponse response,
                                                   @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                   @RequestHeader(value = "merchant") Long merchantIdInHeader,
                                                   @ApiParam(value="页码")@RequestParam(required=false) Integer pageIndex,
                                                   @ApiParam(value="每页记录数")@RequestParam(required=false) Integer pageSize,
                                                   @ApiParam(value="标题")@RequestParam(required=false) String title,
                                                   @ApiParam(value="订单ID")@RequestParam(required=false) String orderId,
                                                   @ApiParam(value="客户ID")@RequestParam(required=false) String receiverId,
                                                   @ApiParam(value="客户电话")@RequestParam(required=false) String receiverPhone,
                                                   @ApiParam(value="客户名称")@RequestParam(required=false) String receiverName,
                                                   @ApiParam(value="工单类型ID")@RequestParam(required=false) Integer typeId,
                                                   @ApiParam(value="商户ID")@RequestParam(required=false) Long merchantId,
                                                   @ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                                                   @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd,
                                                   @ApiParam(value="工单状态码")@RequestParam(required=false) Integer status
                                                     ) {

        java.util.Date dateCreateTimeStart ;
        java.util.Date dateCreateTimeEnd ;
        int index = (null == pageIndex || 0 >= pageIndex)?1:pageIndex;
        int limit = (null == pageSize || 0>= pageSize)?10:pageSize;
        //String username = JwtTokenUtil.getUsername(authentication);
        if (null == authentication) {
            log.info("can not get authentication");
        }
        if (null == merchantIdInHeader) {
            log.warn("can not find merchant in header");
            StringUtil.throw400Exp(response, "400002:merchant  is wrong");
            return null;
        }

        try {
            dateCreateTimeStart = getDateType(timeStart,false);
            dateCreateTimeEnd = getDateType(timeEnd,true);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400005:"+e.getMessage());
            return null;
        }

        Long merchant;
        if (0 != merchantIdInHeader) {
            merchant = merchantIdInHeader;
        } else {
            merchant = merchantId;
        }

        PageInfo<WorkOrder> pages;
        try {
            pages = workOrderService.selectPage(index, limit, "id", "DESC",
                    title, receiverId, receiverName, receiverPhone, orderId, typeId, merchant,
                    status, dateCreateTimeStart, dateCreateTimeEnd);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }
        List<WorkOrderBean> list = new ArrayList<>();

        if ((index -1) * pages.getPageSize() <= pages.getTotal()) {
            for (WorkOrder a : pages.getRows()) {
                WorkOrderBean b = new WorkOrderBean();
                BeanUtils.copyProperties(a, b);
                if (null != a.getGuanaitongRefundAmount()) {
                    b.setRealRefundAmount(a.getGuanaitongRefundAmount());
                }
                list.add(b);
            }
        }
        PageInfo<WorkOrderBean> result = new PageInfo<>(pages.getTotal(), pages.getPageSize(),index, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

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
        String iAppId = data.getiAppId();
        String tAppId = data.gettAppId();


        if (null == orderId || orderId.isEmpty() ) {
            StringUtil.throw400Exp(response, "400002:所属订单不能空缺");
            return result;
        }
        if (null == iAppId || iAppId.isEmpty() ) {
            StringUtil.throw400Exp(response, "400007:iAppId不能空缺");
            return result;
        }
        if (null == receiverId || receiverId.isEmpty() ) {
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

        WorkOrder selectedWO;
        try {
            selectedWO = workOrderService.getValidNumOfOrder(receiverId, orderId);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == selectedWO) {//totally new order
            log.info("there is not work order of this orderId: " + orderId);
            JSONObject json;
            try {
                json = workOrderService.getOrderInfo(receiverId, orderId, merchantId);
            }catch (Exception e) {
                StringUtil.throw400Exp(response, "400006:"+e.getMessage());
                return null;
            }

            if (null == json) {
                StringUtil.throw400Exp(response, "400007:找不到订单信息");
                return result;
            }

            Integer parentOrderId = json.getInteger("id");
            if (null == parentOrderId) {
                StringUtil.throw400Exp(response, "400008: searchOrder, 获取id失败");
                return result;
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
                StringUtil.throw400Exp(response, "400006:所属订单退货数量已满");
                return result;
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
        workOrder.setTypeId(typeId);
        workOrder.setMerchantId(merchantId);
        workOrder.setStatus(WorkOrderStatusType.EDITING.getCode());
        workOrder.setReceiverId(receiverId);
        workOrder.setiAppId(iAppId);
        if (null != tAppId && !tAppId.isEmpty()) {
            workOrder.settAppId(tAppId);
        }
        workOrder.setCreateTime(new Date());
        workOrder.setUpdateTime(new Date());

        try {
            result.id = workOrderService.insert(workOrder);
        } catch (RuntimeException ex) {
            StringUtil.throw400Exp(response, ex.getMessage());
        }

        if (0 == result.id) {
            StringUtil.throw400Exp(response, "400008:Failed to create work_order");
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
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return result;
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(id);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400003:工单不存在");
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

        workOrder.setUpdateTime(new Date());

        try {
            workOrderService.update(workOrder);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        log.info("update WorkOrder done");
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

        log.info("delete WorkOrders param : {}",id);

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002: ID is wrong");
            return;
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (username.isEmpty()) {
            log.warn("can not find username in token");
        }

        WorkOrder workOrder;
        try {
            workOrder = workOrderService.selectById(id);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return;
        }

        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400002: failed to find record");
            return;
        }

        try {
            workOrderService.deleteById(id);
        } catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return;
        }
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
        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        if (null == timeStart || timeStart.isEmpty() ||
                null == timeEnd || timeEnd.isEmpty()) {
            log.info("count all return and refund work-orders");
        } else {
            log.info("count return and refund work-order between: " + timeStart + "--" + timeEnd);
            try {
                dateCreateTimeStart = getDateType(timeStart,false);
                dateCreateTimeEnd = getDateType(timeEnd,true);
            } catch (Exception ex) {
                result.setMsg("createTime is wrong "+ex.getMessage());
                return result;
            }
        }
        int countReturn;
        try {
            countReturn = workOrderService.countReturn(dateCreateTimeStart, dateCreateTimeEnd);
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
    public ResultObject<List<WorkOrder>> getRefundList(@ApiParam(value="开始日期")@RequestParam(required=false) String timeStart,
                                                 @ApiParam(value="结束日期")@RequestParam(required=false) String timeEnd
                                            ) {


        ResultObject<List<WorkOrder>> result = new ResultObject<>(400, "failed: parameter missing", null);
        java.util.Date dateCreateTimeStart;
        java.util.Date dateCreateTimeEnd;

        if (null == timeStart || timeStart.isEmpty() ||
                null == timeEnd || timeEnd.isEmpty()) {
            String dataNow = new Date().toString().trim();
            String todayStr = dataNow.substring(0,10);
            String todayBegin = todayStr+" 00:00:00";
            String todayEnd = todayStr+" 23:59:59";
            try {
                dateCreateTimeStart = StringUtil.String2Date(todayBegin);
                dateCreateTimeEnd = StringUtil.String2Date(todayEnd);
            } catch (ParseException ex) {
                log.error("exception: {}",ex.getMessage());
                result.setMsg("createTime is wrong");
                return result;
            }

            log.info("will find records of "+todayStr);
        } else {
            log.info("count return and refund work-order between: " + timeStart + "--" + timeEnd);

            try {
                dateCreateTimeStart = getDateType(timeStart,false);
                dateCreateTimeEnd = getDateType(timeEnd,true);
            } catch (Exception ex) {
                log.error("createTime string is wrong exception {}",ex.getMessage());
                result.setMsg("createTime is wrong "+ex.getMessage());
                return result;
            }
        }
        try {
            List<WorkOrder> list = workOrderService.selectByTimeRange(dateCreateTimeStart, dateCreateTimeEnd);
            if (null != list) {
                result.setData(list);
                result.setCode(200);
                result.setMsg("success");
            }
        } catch (Exception e) {
            log.warn("selectByTimeRange exception : {}",e.getMessage());
        }
        return result;
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

    @ApiOperation(value = "关爱通支付回调", notes = "关爱通支付回调")
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping(value = "refund/notify", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String gBack(GuanAiTongNotifyBean bean) {
        String param = JSON.toJSONString(bean);
        log.info("关爱通 refund notify: params : " + param);
        try {
            return workOrderService.handleNotify(bean);
        }catch (Exception e) {
            return "fail";
        }
    }

    @ApiOperation(value = "发起关爱通退款", notes = "发起关爱通退款")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("refund/guanaitong")
    public GuanAiTongNo sendRefund(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @RequestBody Map<String, Long> data) {

        if (null == authentication) {
            log.info("can not get authentication");
        }
        GuanAiTongNo result = new GuanAiTongNo();
        Long id = data.get("id");
        Integer hasFare = (int)(long)data.get("fare");
        if (null == id) {
            log.error("send refund to GuanAiTong for work-order: id is null");
            StringUtil.throw400Exp(response, "400002: id is wrong");
            return result;
        }
        log.info("send refund for work-order: id = " + id.toString());

        String guanAiTongTradeNo;
        try {
            guanAiTongTradeNo = workOrderService.sendRefund2GuangAiTong(id,hasFare);
        }catch (Exception e) {
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return null;
        }
        if (null == guanAiTongTradeNo) {
            StringUtil.throw400Exp(response, "400003: failed to find work-order");
            return result;
        } else {
            if (guanAiTongTradeNo.isEmpty()) {
                StringUtil.throw400Exp(response, "400004: send to GuanAiTong failed");
                return null;
            } else {
                if (guanAiTongTradeNo.contains("Error:")) {
                    String errMsg = guanAiTongTradeNo.replace(':','-');
                    StringUtil.throw400Exp(response, "400006: " + errMsg);
                    return result;
                }
            }
        }

        result.tradeNo = guanAiTongTradeNo;
        response.setStatus(MyErrorMap.e201.getCode());

        return result;
    }

    /**
     * 获取已退款的子订单id集合
     *
     * @param merchantId
     * @param startTime
     * @param endTime
     * @return
     */
    @ApiOperation(value = "获取已退款的子订单id集合", notes = "获取已退款的子订单id集合")
    @ResponseStatus(code = HttpStatus.CREATED)
    @GetMapping("refund/query/refunded")
    public ResultObject<List<String>> queryRefundedOrderDetailIdList(@RequestParam(value = "merchantId", required = false) Long merchantId,
                                                                     @RequestParam(value = "startTime") Date startTime,
                                                                     @RequestParam(value = "endTime") Date endTime) {
        // 返回值
        ResultObject<List<String>> resultObject = new ResultObject<>(500, "获取已退款的子订单id集合默认错误", null);

        log.info("获取已退款的子订单id集合 入参 merchantId:{}, startTime:{}, endTime:{}", merchantId, startTime, endTime);

        try {
            List<WorkOrder> workOrderList =
                    workOrderService.querySuccessRefundOrderDetailIdList(merchantId, startTime, endTime);

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
