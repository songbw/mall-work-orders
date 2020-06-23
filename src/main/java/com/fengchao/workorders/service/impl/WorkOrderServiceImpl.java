package com.fengchao.workorders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.constants.WorkOrderType;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.feign.IAoYiClient;
import com.fengchao.workorders.feign.IGuanAiTongClient;
import com.fengchao.workorders.feign.OrderService;
import com.fengchao.workorders.mapper.WorkOrderMapper;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.entity.*;
import com.fengchao.workorders.service.IWorkOrderService;
//import org.joda.time.DateTime;
//import org.springframework.beans.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * */
@Slf4j
@Service(value="WorkOrderServiceImpl")
@Transactional(rollbackFor = Exception.class)
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper,WorkOrder> implements IWorkOrderService {


    private OrderService orderService;
    private IGuanAiTongClient guanAiTongClient;
    //private RestTemplate restTemplate;
    private WorkOrderMapper mapper;
    private IAoYiClient aoYiClient;
    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkOrderServiceImpl(
                                WorkOrderMapper mapper,
                                IGuanAiTongClient guanAiTongClient,
                                IAoYiClient aoYiClient,
                                OrderService orderService
                              ) {

        //this.restTemplate = restTemplate;
        this.orderService = orderService;
        this.guanAiTongClient = guanAiTongClient;
        this.mapper = mapper;
        this.aoYiClient = aoYiClient;
    }


    @Override
    public WorkOrder
    selectByRefundNo(String refundNo){
        if(null == refundNo || refundNo.isEmpty()){
            return null;
        }
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(WorkOrder.REFUND_NO,refundNo);
        return getOne(wrapper);
    }

    @Override
    public PageInfo<WorkOrder>
    selectPage(int pageIndex, int pageSize, String iAppId,
               String title, String receiverId, String receiverName, String receiverPhone,
               String orderId, Integer typeId, Long merchantId,Integer statusCode,
               String createTimeStart, String createTimeEnd,String refundTimeBegin, String refundTimeEnd){

        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(WorkOrder.CREATE_TIME);

        if(null != title){
            wrapper.eq(WorkOrder.TITLE,title);
        }
        if(null != receiverId){
            wrapper.eq(WorkOrder.RECEIVER_ID,receiverId);
        }
        if(null != receiverName){
            wrapper.like(WorkOrder.RECEIVER_NAME,receiverName);
        }
        if(null != orderId){
            wrapper.eq(WorkOrder.ORDER_ID,orderId);
        }
        if(null != typeId){
            WorkOrderType workOrderType = WorkOrderType.checkByCode(typeId);
            if(null != workOrderType) {
                wrapper.eq(WorkOrder.TYPE_ID, workOrderType);
            }
        }
        if (null != merchantId){
            wrapper.eq(WorkOrder.MERCHANT_ID,merchantId);
        }
        if (null != statusCode){
            WorkOrderStatusType status = WorkOrderStatusType.checkByCode(statusCode);
            if(null != status){
                wrapper.eq(WorkOrder.STATUS,status);
            }
        }
        if (null != createTimeStart){
            LocalDateTime createStart = getDateTimeByDate(createTimeStart,false);
            if(null != createStart) {
                wrapper.ge(WorkOrder.CREATE_TIME, createStart);
            }
        }
        if (null != createTimeEnd){
            LocalDateTime createEnd = getDateTimeByDate(createTimeEnd,true);
            if(null != createEnd) {
                wrapper.le(WorkOrder.CREATE_TIME, createEnd);
            }
        }
        if (null != refundTimeBegin){
            LocalDateTime refundStart = getDateTimeByDate(refundTimeBegin,false);
            if(null != refundStart) {
                wrapper.ge(WorkOrder.REFUND_TIME, refundStart);
            }
        }
        if (null != refundTimeEnd){
            LocalDateTime refundEnd = getDateTimeByDate(refundTimeEnd,true);
            if(null != refundEnd) {
                wrapper.le(WorkOrder.REFUND_TIME, refundEnd);
            }
        }

        IPage<WorkOrder> pages = page(new Page<>(pageIndex, pageSize), wrapper);
        return new PageInfo<>((int)pages.getTotal(), pageSize, pageIndex, pages.getRecords());
    }

    @Override
    public PageInfo<WorkOrder>
    selectAbnormalRefundList(int pageIndex, int pageSize,
                             String iAppId,
                             String orderId, Long merchantId,
                             String createTimeStart, String createTimeEnd){

        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(WorkOrder.CREATE_TIME);

        if(null != iAppId){
            wrapper.eq(WorkOrder.I_APP_ID,iAppId);
        }

        if(null != orderId){
            wrapper.eq(WorkOrder.ORDER_ID,orderId);
        }

        if (null != merchantId){
            wrapper.eq(WorkOrder.MERCHANT_ID,merchantId);
        }

        if (null != createTimeStart){
            LocalDateTime createStart = getDateTimeByDate(createTimeStart,false);
            if(null != createStart) {
                wrapper.ge(WorkOrder.CREATE_TIME, createStart);
            }
        }
        if (null != createTimeEnd){
            LocalDateTime createEnd = getDateTimeByDate(createTimeEnd,true);
            if(null != createEnd) {
                wrapper.le(WorkOrder.CREATE_TIME, createEnd);
            }
        }

        IPage<WorkOrder> pages = page(new Page<>(pageIndex, pageSize), wrapper);
        return new PageInfo<>((int)pages.getTotal(), pageSize, pageIndex, pages.getRecords());
    }

    @Override
    public List<WorkOrder>
    selectByOrderIdList(List<String> orderIdList){

        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.in(WorkOrder.ORDER_ID,orderIdList);

        return list(wrapper);

    }

    @Override
    public List<WorkOrder>
    selectByParentOrderId(Integer parentOrderId) {
        log.info("selectByParentOrderId param {}",parentOrderId);
        if(null == parentOrderId){ return new ArrayList<>();}
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(WorkOrder.CREATE_TIME);
        wrapper.isNotNull(WorkOrder.ORDER_ID);
        wrapper.eq(WorkOrder.PARENT_ORDER_ID,parentOrderId);
        wrapper.ne(WorkOrder.STATUS,WorkOrderStatusType.REJECT);

        return list(wrapper);
    }

    @Override
    public int countReturn(String createTimeStart, String createTimeEnd) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(WorkOrder.CREATE_TIME);
        if(null != createTimeStart){
            wrapper.ge(WorkOrder.CREATE_TIME, StringUtil.String2Date(createTimeStart));
        }
        if(null != createTimeEnd){
            wrapper.le(WorkOrder.CREATE_TIME, StringUtil.String2Date(createTimeEnd));
        }

        return count(wrapper);
    }

    @Override
    public List<WorkOrder> selectByTimeRange(String dateStart, String dateEnd) {
        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(WorkOrder.CREATE_TIME);
        if (null != dateStart) {
            LocalDateTime start = getDateTimeByDate(dateStart, false);
            log.info("start time = {}",start);
            wrapper.ge(WorkOrder.CREATE_TIME, start);
        }
        if(null != dateEnd) {
            wrapper.le(WorkOrder.CREATE_TIME, getDateTimeByDate(dateEnd, true));
        }
        return list(wrapper);
    }

    @Override
    public Integer
    queryRefundUserCount(Long merchantId){
        log.info("获取商户的退货人数 数据库入参:{}", merchantId);

        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.isNotNull(WorkOrder.RECEIVER_ID);
        if(null != merchantId){
            wrapper.eq(WorkOrder.MERCHANT_ID,merchantId);
        }

        List<WorkOrder> workOrders = list(wrapper);
        if(null == workOrders || 0 == workOrders.size()){
            return 0;
        }

        Map<String, List<WorkOrder>> groupMap = workOrders.stream().collect(Collectors.groupingBy(WorkOrder::getReceiverId));
        int total = 0;
        for(Map.Entry<String,List<WorkOrder>> m: groupMap.entrySet()){
            if (!m.getKey().isEmpty()) {
                total++;
            }
        }
        log.info("获取商户的退货人数 数据库返回:{}",total);
        return total;
    }

    @Override
    public WorkOrder
    getValidNumOfOrder(String openId, String orderId){
        log.info("getValidNumOfOrder param: openId={}, orderId={}",openId,orderId);
        int validNum;

        if(null == orderId || null == openId){
            return null;
        }

        openId = openId.trim();
        orderId = orderId.trim();

        QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(WorkOrder.ORDER_ID,orderId);
        wrapper.ne(WorkOrder.STATUS,WorkOrderStatusType.REJECT);
        List<WorkOrder> workOrders = list(wrapper);

        if (null == workOrders || 0 == workOrders.size()) {
            log.info("getValidNumOfOrder : 该订单尚无工单，可生成新工单");
            return null;
        }
        List<WorkOrder> list = new ArrayList<>();
        for(WorkOrder w: workOrders){
            //已关闭,且没有退款完成的工单不影响提交新的工单(当前列表里已经没有拒绝状态的工单)
            String validDate = "2000-01-01 00:00:00";
            String refundTime = StringUtil.Date2String(w.getRefundTime());

            boolean isRefunding = !WorkOrderStatusType.isClosedStatus(w.getStatus());
            boolean isRefundedAndClose = WorkOrderStatusType.isClosedStatus(w.getStatus()) &&
                    (0 > validDate.compareTo(refundTime));
            if (isRefunding || isRefundedAndClose){
                list.add(w);
            }
        }
        if (0 == list.size()){
            //不存在已经退款的工单
            log.info("getValidNumOfOrder : 该订单尚无退款工单，可生成新工单");
            return null;
        }

        //list中第一个记录作为传递信息对象
        WorkOrder baseWO = list.get(0);

        int usedNum = 0;
        int goodsNum = 0;
        String open_id = baseWO.getReceiverId();
        if (openId.equals(open_id) && null != baseWO.getOrderGoodsNum()) {
            goodsNum = baseWO.getOrderGoodsNum();
        }
        for (WorkOrder wo : list) {
            if (null != wo.getReceiverId()
                    && wo.getReceiverId().equals(openId)
                    && null != wo.getReturnedNum()
                    && null != wo.getStatus()
                    ) {
                usedNum += wo.getReturnedNum();
            }
        }

        validNum = goodsNum - usedNum;
        if (0 > validNum) {
            validNum = 0;
        }
        WorkOrder workOrder = list.get(0);
        //list中第一个记录作为传递信息对象，临时复用returnedNum存储可申请退款的商品数量
        //该返回对象不可用于判定申请退款的商品数量外的其他目的
        workOrder.setReturnedNum(validNum);
        log.info("getValidNumOfOrder : {}",JSON.toJSONString(workOrder));
        return workOrder;

    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject getOrderInfo(String openId, String subOrderId, Long merchantId){
        QueryOrderBodyBean body = new QueryOrderBodyBean();
        body.setOpenId(openId);
        body.setPageIndex(1);
        body.setPageSize(10);
        body.setSubOrderId(subOrderId);
        Map<String, Object> map = new HashedMap();
        map.put("merchant", 0);//fix it

        OperaResult result = orderService.getOrderList(body,map);
        log.info("searchOrder result : {}",JSON.toJSONString(result));
        if(null == result || null == result.getCode()){
            throw new MyException(MyErrorEnum.API_SEARCH_ORDER_FAILED);
        }
        if (result.getCode() == 200) {
            Map<String, Object> data = result.getData();
            if (null == data) {
                return null;
            }
            Object objectResult = data.get("result");
            if (null == objectResult){
                return null;
            }

            String jsonString = JSON.toJSONString(objectResult);
            JSONObject theJson = JSON.parseObject(jsonString);
            if (null == theJson) {
                return null;
            }
            JSONArray theList = theJson.getJSONArray("list");
            if (null == theList || 0 == theList.size()) {
                return null;
            }

            List<JSONObject> list = JSONObject.parseArray(JSON.toJSONString(theList), JSONObject.class);
            log.info("searchOrder: success");
            return list.get(0);

        }
        return null;
        /*
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("merchant",merchantId.toString());

        JSONObject postBody = new JSONObject();
        postBody.put("pageIndex",1);
        postBody.put("pageSize",1);
        postBody.put("subOrderId", subOrderId);
        postBody.put("openId",openId);
        postBody.put("status",2);

        HttpEntity entity = new HttpEntity<>(postBody, requestHeaders);
        //ServiceInstance serviceInstance = loadBalancerClient.choose("orders");
        //String theUrl = String.format("http://%s:%s/%s", serviceInstance.getHost(), serviceInstance.getPort(), "/order/searchOrder");
        String theUrl = "http://api.weesharing.com/v2/orders/order/searchOrder";

        JSONObject json = restTemplate.postForObject(
                theUrl,
                entity,
                JSONObject.class);
        if (null == json) {
            log.info("try post:orders/order/searchOrder failed");
        }

        Integer code = json.getInteger("code");
        JSONObject responseData = json.getJSONObject("data");
        if (null == code || 200 != code || null == responseData) {
            return null;
        }

        JSONObject responseResult = responseData.getJSONObject("result");
        if (null == responseResult) {
            return null;
        }

        JSONArray list = responseResult.getJSONArray("list");
        if (null == list || 0 == list.size()){
            return null;
        }

        return list.getJSONObject(0);
        */
    }

    @Override
    public String handleAggPaysNotify(AggPayNotifyBean bean) {

        String result = "fail";
        String outerRefundNo = bean.getOutRefundNo();//json.getString("outRefundNo");
        String refundTimeStr = bean.getTradeDate();//json.getString("tradeDate");//
        Integer status = bean.getStatus();//json.getInteger("status");//
        String refundFeeStr = bean.getRefundFee();//json.getFloat("refundFee");

        if (null == status) {
            log.error("聚合支付退款回调 post body is wrong, status is null");
            return result;
        }
        if (null == outerRefundNo || outerRefundNo.isEmpty()) {
            log.error("聚合支付退款回调 post body is wrong, outerRefundNo is null");
            return result;
        }

        if (null == refundFeeStr) {
            log.error("聚合支付退款回调 post body is wrong, refundFee is null");
            return result;
        }

        WorkOrder wo = getOne(
                       new QueryWrapper<WorkOrder>()
                        .eq(WorkOrder.GUANAITONG_TRADE_NO,outerRefundNo)
                        .or()
                        .eq(WorkOrder.REFUND_NO,outerRefundNo)
                        );
        if(null == wo) {
            log.error("聚合支付退款回调 handle notify, but not found work-order by refundNo: {}", outerRefundNo);
            return result;
        }

        BigDecimal decRefundFee = new BigDecimal(refundFeeStr);
        BigDecimal dec100f = new BigDecimal("100");
        Float refundFee = decRefundFee.divide(dec100f).floatValue();
        if (0 > refundFee) {
            log.warn("聚合支付退款回调 refund_amount 异常");
            return result;
        }

        WorkOrder updateRecord = new WorkOrder();
        updateRecord.setId(wo.getId());
        //2:退款失败, 1:成功； 3：部分成功
        if (1 == status || 3 == status || 2 == status) {
            if (null != refundTimeStr && !refundTimeStr.isEmpty() && 1 == status) {
                try {
                    updateRecord.setRefundTime(StringUtil.String2Date(refundTimeStr));
                } catch (Exception ex) {
                    log.error("convert refundTime error {}", ex.getMessage());
                }
            }
            if (1 == status) {
                updateRecord.setGuanaitongRefundAmount(refundFee);
                String msg = "聚合支付退款编码="+outerRefundNo+", 聚合支付退款成功";
                log.info(msg);
                updateRecord.setComments(msg);
                updateRecord.setStatus(WorkOrderStatusType.CLOSED);
            } else if (3 == status) {
                updateRecord.setGuanaitongRefundAmount(refundFee);
                String msg = "聚合支付退款编码="+outerRefundNo + ", 聚合支付退款,部分成功";
                log.info(msg);
                updateRecord.setComments(msg);
                updateRecord.setStatus(WorkOrderStatusType.CLOSED);
            } else {
                String msg = "聚合支付退款编码="+outerRefundNo + ", 聚合支付退款失败";
                log.error(msg);
                updateRecord.setComments(msg);
                updateRecord.setStatus(WorkOrderStatusType.REFUND_FAILED);
            }
        }

        updateRecord.setUpdateTime(LocalDateTime.now());
        updateById(updateRecord);

        log.info("聚合支付退款回调 处理完成 {}",JSON.toJSONString(getById(wo.getId())));

        return "success";
    }

    @Override
    public String handleNotify(GuanAiTongNotifyBean bean) {

        String result = "fail";
        String outer_refund_no = bean.getOuter_refund_no();
        String trade_no = bean.getTrade_no();
        String appid = bean.getAppid();
        String outer_trade_no = bean.getOuter_trade_no();
        Float refund_amount = bean.getRefund_amount();

        if (null == outer_refund_no || outer_refund_no.isEmpty() ||
                null == trade_no || trade_no.isEmpty() ||
                null == appid || appid.isEmpty() ||
                null == outer_trade_no || outer_trade_no.isEmpty() ||
                null == refund_amount) {
            return result;
        }

        WorkOrder wo = getOne(
                new QueryWrapper<WorkOrder>()
                        .eq(WorkOrder.GUANAITONG_TRADE_NO,outer_refund_no)
                        .or()
                        .eq(WorkOrder.REFUND_NO,outer_refund_no)
        );
        if (null == wo) {
            log.warn("handle notify, but not found work-order by refundNo: " + outer_refund_no);
            return result;
        }

        if (0 >= refund_amount) {
            log.warn("notify parameters refund_amount abnormal");
            return result;
        }

        WorkOrder updateRecord = new WorkOrder();
        updateRecord.setId(wo.getId());

        updateRecord.setRefundTime(LocalDateTime.now());
        updateRecord.setGuanaitongRefundAmount(refund_amount);
        updateRecord.setGuanaitongTradeNo(trade_no);
        updateRecord.setStatus(WorkOrderStatusType.CLOSED);
        updateRecord.setUpdateTime(LocalDateTime.now());
        updateById(updateRecord);

        log.info("关爱通 refund notify handle success");

        return "success";
    }

    @Override
    public String
    sendRefund2GuangAiTong(Long workOrderId, Integer handleFare, Float refund) {

        log.info("sendRefund2GuangAiTong enter : workOrderId = ", workOrderId);
        WorkOrder wo = getById(workOrderId);
        if (null == wo) {
            throw new MyException(MyErrorEnum.WORK_ORDER_NO_NOT_FOUND);
        }

        log.info("找到工单:{} " ,JSON.toJSONString(wo));
        if (null == wo.getOrderId()) {
            throw new MyException(MyErrorEnum.RESPONSE_FUNCTION_ERROR,"工单记录异常");
        }

        String tradeNo = wo.getTradeNo();
        String appId = wo.getIAppId();
        Float refundAmount = (null == refund) ? wo.getRefundAmount() : refund;
        String reason = wo.getTitle();
        if (null == tradeNo || null == appId || null == refundAmount || null == reason) {
            throw new MyException(MyErrorEnum.RESPONSE_FUNCTION_ERROR,"工单记录异常");
        }

        if (null != handleFare && 0 != handleFare) {
            boolean handledFare = hasHandledFare(wo);
            if (handledFare) {
                throw new MyException(MyErrorEnum.WORK_ORDER_FARE_RETURNED);
            }else {
                refundAmount += wo.getFare();
            }
        }

        String notifyUrl = GuanAiTongConfig.getConfigGatNotifyUrl();//"http://api.weesharing.com/v2/workorders/refund/notify";

        Long timeStampMs = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long timeStampS = timeStampMs / 1000;
        String timeStamp = timeStampS.toString();
        Random random = new Random();

        String triRandom = random.nextInt(1000) + "";
        StringBuilder sb = new StringBuilder();
        int randLength = triRandom.length();
        if (randLength < 3) {
            for (int i = 1; i <= 3 - randLength; i++) {
                sb.append("0");
            }
        }
        sb.append(triRandom);
        String refundNo = appId + timeStamp + sb.toString();

        GuanAiTongRefundBean bean = new GuanAiTongRefundBean();
        bean.setNotify_url(notifyUrl);
        bean.setOuter_refund_no(refundNo);
        bean.setOuter_trade_no(tradeNo);
        bean.setReason(reason);
        bean.setRefund_amount(refundAmount);

        ResultObject<String> gResult = guanAiTongClient.postRefund(wo.getIAppId(), bean);
        if (null == gResult) {
            log.warn("post to GuanAiTong refund failed");
            throw new MyException(MyErrorEnum.API_GAT_CLIENT_POST_FAILED);
        }

        Integer code = gResult.getCode();
        String guanAiTongNo = gResult.getData();
        if (null == code || null == guanAiTongNo || 200 != code || guanAiTongNo.isEmpty()) {
            log.info("post to GuanAiTong refund failed : {}", gResult.getMsg());
            StringBuilder errMsgSb = new StringBuilder();
            errMsgSb.append("got error from Guan Ai Tong ");
            if (null != code) {
                errMsgSb.append("Error code = ");
                errMsgSb.append(code.toString());
            }
            if (null != gResult && null != gResult.getMsg()) {
                errMsgSb.append(" message = ");
                errMsgSb.append(gResult.getMsg());
            }

            throw new MyException(MyErrorEnum.RESPONSE_FUNCTION_ERROR,errMsgSb.toString());
        }

        wo = getById(workOrderId);
        if (null != wo) {
            WorkOrder updateRecord = new WorkOrder();
            updateRecord.setId(wo.getId());
            if (!WorkOrderStatusType.isClosedStatus(wo.getStatus())) {
                updateRecord.setStatus(WorkOrderStatusType.REFUNDING);
            }
            if (null == wo.getRefundNo()) {
                //怡亚通的订单,退款号来自申请接口返回的serviceSn
                updateRecord.setRefundNo(refundNo);
            }
            updateRecord.setGuanaitongTradeNo(guanAiTongNo);
            updateRecord.setRefundAmount(refundAmount);
            if (null == handleFare || 0 == handleFare) {
                //since we will check fare when create work-order. and set it by query order info.
                //to CLOSE work_order, if did not handle fare, set it to 0.00f, then can handle fare later.
                updateRecord.setFare(0.00f);
            }
            updateRecord.setUpdateTime(LocalDateTime.now());
            updateById(updateRecord);
        }

        log.info("sendRefund2GuangAiTong success ");
        return guanAiTongNo;
    }

    @Override
    public List<WorkOrder>
    querySuccessRefundOrderDetailIdList(String iAppId,Long merchantId, String startTime, String endTime) {
        List<WorkOrder> workOrderList ;
        log.info("查询已退款的记录 数据库入参 iAppId={} ,merchantId:{}, startTime:{}, endTime:{}", iAppId,merchantId, startTime, endTime);
        try {
            QueryWrapper<WorkOrder> wrapper = new QueryWrapper<>();
            if(null != iAppId){
                wrapper.eq(WorkOrder.I_APP_ID,iAppId);
            }
            if(null != merchantId){
                wrapper.eq(WorkOrder.MERCHANT_ID,merchantId);
            }
            if(null != startTime){
                wrapper.ge(WorkOrder.REFUND_TIME,getDateTimeByDate(startTime,false));
            }
            if(null != endTime){
                wrapper.le(WorkOrder.REFUND_TIME,getDateTimeByDate(endTime,true));
            }
            workOrderList = list(wrapper);
            log.info("查询已退款的记录 数据库返回:{}", JSONUtil.toJsonString(workOrderList));
        } catch (Exception e) {
            log.error("查询已退款的记录 异常:{}", e.getMessage(), e);

            throw e;
        }

        return workOrderList;
    }

    @Override
    public void sendExpressInfo(WorkOrder workOrder, String comments) {

        YiYaTongReturnGoodsBean bean = YiYaTong.parseReturnGoodsComments(comments);
        if (null == bean){
            return;
        }

        bean.setDeliverySn(workOrder.getExpressNo());
        bean.setServiceSn(workOrder.getRefundNo());
        bean.setOrderSn(workOrder.getGuanaitongTradeNo());

        log.info("怡亚通买家退货发物流 {}",JSON.toJSONString(bean));
        String response = aoYiClient.postReturnGoods(bean);
        log.info("怡亚通买家退货发物流 返回 response = {}",JSON.toJSONString(response));
        AoYiRefundResponseBean responseBean = YiYaTong.parseRefundReturnResponse(response);
        if (null != responseBean) {
            if (workOrder.getGuanaitongTradeNo().equals(responseBean.getOrderSn()) &&
                    workOrder.getRefundNo().equals(responseBean.getServiceSn())){
                log.info("怡亚通买家退货发物流 返回信息正常");
            }else {
                log.error("怡亚通买家退货发物流 返回信息{},{} ： 与工单记录不符 {}, {}",
                        responseBean.getServiceSn(),responseBean.getOrderSn(),workOrder.getRefundNo(),workOrder.getGuanaitongTradeNo());
            }
        }else {
            log.error("怡亚通买家退货发物流 返回信息缺失");
        }
    }

    @Override
    public AoYiRefundResponseBean
    getYiYaTongRefundNo(String reason,Integer subStatus,String thirdOrderSn,String skuId) {

        log.info("处理怡亚通订单退款申请 enter");

        if (null == reason) {
            reason = "退款";
        }

        //AoYiClientResponseObject<AoYiRefundResponseBean> resp;
        String resp;
        try {
            if (1 == subStatus) {
                AoYiRefundOnlyPostBean bean = new AoYiRefundOnlyPostBean();
                bean.setOrderSn(thirdOrderSn);
                bean.setReason(reason);
                log.info("处理怡亚通订单退款申请 try request aoyi client {}",JSON.toJSONString(bean));
                resp = aoYiClient.postRefundOnly(bean);
            } else {
                AoYiRefundReturnPostBean bean = new AoYiRefundReturnPostBean();
                bean.setOrderSn(thirdOrderSn);
                bean.setReason(reason);
                bean.setCode(skuId);
                //0：退货退款, 1：仅退款
                bean.setReturnType("0");
                log.info("处理怡亚通订单退款申请 try request aoyi client {}",JSON.toJSONString(bean));
                resp = aoYiClient.postRefundReturn(bean);
            }
        } catch (Exception e) {
            throw e;
        }

        log.info("处理怡亚通订单退款申请 response = {}",JSON.toJSONString(resp));
        AoYiRefundResponseBean responseBean = YiYaTong.parseRefundReturnResponse(resp);
        if (null == responseBean) {
            throw new RuntimeException("420006:怡亚通退货退款申请无回应,请重试");
        }
        return responseBean;

    }

    private boolean hasHandledFare(WorkOrder wo){

        List<WorkOrderStatusType> statusList = new ArrayList<>();
        statusList.add(WorkOrderStatusType.CLOSED);
        statusList.add(WorkOrderStatusType.REFUND_FAILED);

        List<WorkOrder> workOrders = list(
                new QueryWrapper<WorkOrder>()
                        .eq(WorkOrder.PARENT_ORDER_ID,wo.getParentOrderId())
                        .in(WorkOrder.STATUS,statusList)
                        .gt(WorkOrder.FARE,0)
        );

        if (null == workOrders || 1 > workOrders.size()) {
            return false;
        }else {
            return true;
        }

    }

    private LocalDateTime
    getDateTimeByDate(String dateStr, boolean isEnd){

        if (null == dateStr || dateStr.isEmpty()) {
            return null;
        }

        String timeString = dateStr.trim();
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

        return LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern(DateUtil.DATE_YYYY_MM_DD_HH_MM_SS));
    }
}
