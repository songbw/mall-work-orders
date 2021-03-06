package com.fengchao.workorders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.AggPayNotifyBean;
import com.fengchao.workorders.bean.GuanAiTongNotifyBean;
import com.fengchao.workorders.bean.GuanAiTongRefundBean;
import com.fengchao.workorders.bean.QueryOrderBodyBean;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.feign.IAggPayClient;
import com.fengchao.workorders.feign.IGuanAiTongClient;
import com.fengchao.workorders.feign.OrderService;
import com.fengchao.workorders.mapper.WorkOrderMapper;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.dao.impl.WorkOrderDaoImpl;
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
import java.util.*;

@Slf4j
@Service(value="WorkOrderServiceImpl")
@Transactional
public class WorkOrderServiceImpl implements IWorkOrderService {


    private OrderService orderService;
    private IGuanAiTongClient guanAiTongClient;
    private WorkOrderDaoImpl workOrderDao;
    //private RestTemplate restTemplate;
    private WorkOrderMapper mapper;

    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkOrderServiceImpl(WorkOrderDaoImpl workOrderDao,
                                WorkOrderMapper mapper,
                                IGuanAiTongClient guanAiTongClient,
                                OrderService orderService
                              ) {
        this.workOrderDao = workOrderDao;
        //this.restTemplate = restTemplate;
        this.orderService = orderService;
        this.guanAiTongClient = guanAiTongClient;
        this.mapper = mapper;
    }

    @Override
    public Long insert(WorkOrder workOrder) {
        int rst = workOrderDao.insert(workOrder);
        if (0 < rst) {
            return workOrder.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public void deleteById(Long id) {
        workOrderDao.deleteByPrimaryKey(id);
    }

    @Override
    public WorkOrder selectById(Long id) {
        return workOrderDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(WorkOrder workOrder) {
        if (null == workOrder) {
            return;
        }
        workOrderDao.updateByPrimaryKeySelective(workOrder);
        //System.out.println("updated workOrder for: " + workOrder.getId());
        log.info("updated user for: " + workOrder.getId());
    }

    @Override
    public PageInfo<WorkOrder> selectPage(int pageIndex, int pageSize, String sort, String order,
                                          String title, String receiverId, String receiverName, String receiverPhone,
                                          String orderId, Integer typeId, Long merchantId,Integer status,
                                         Date createTimeStart, Date createTimeEnd,Date refundTimeBegin, Date refundTimeEnd) throws Exception{

        PageInfo<WorkOrder> pageInfo;
        try {

            pageInfo = workOrderDao.selectRange(pageIndex,pageSize,sort, order,
                    title, receiverId, receiverPhone, receiverName,
                    orderId, merchantId, typeId, status,
                    createTimeStart, createTimeEnd, refundTimeBegin, refundTimeEnd);
        }catch (Exception e) {
            throw new Exception(e);
        }
        return pageInfo;
    }

    @Override
    public List<WorkOrder> selectByOrderId(String orderId) throws Exception{
        orderId = orderId.trim();
        List<WorkOrder> list = null;
        try {
            list = workOrderDao.selectByOrderId(orderId);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return list;
    }

    @Override
    public List<WorkOrder> selectByParentOrderId(Integer parentOrderId) throws Exception{
        log.info("selectByParentOrderId param {}",parentOrderId);
        List<WorkOrder> list;
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andParentOrderIdEqualTo(parentOrderId);
        criteria.andOrderIdIsNotNull();
        criteria.andStatusNotEqualTo(WorkOrderStatusType.REJECT.getCode());
        try {
            list = mapper.selectByExample(example);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        log.info("selectByParentOrderId success {}",JSON.toJSONString(list));
        return list;
    }

    @Override
    public int countReturn(Date createTimeStart, Date createTimeEnd) {
        int typeId;

        typeId = WorkOrderType.RETURN.getCode();
        int c1 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        typeId = WorkOrderType.REFUND.getCode();
        int c2 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        typeId = WorkOrderType.EXCHANGE.getCode();
        int c3 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        return c1 + c2 + c3;
    }

    @Override
    public List<WorkOrder> selectByTimeRange(Date createTimeStart, Date createTimeEnd) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();

        if (null != createTimeStart && null != createTimeEnd) {
            criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);
        }

        return mapper.selectByExample(example);
    }

    @Override
    public Integer queryRefundUserCount(Long merchantId) throws Exception{
        log.info("??????????????????????????? ???????????????:{}", merchantId);

        Integer count;
        try {
            count = workOrderDao.selectRefundUserCountByMerchantId(merchantId);
        }catch (Exception e) {
            throw new Exception(e);
        }
        log.info("??????????????????????????? ???????????????:{}", count);

        return count;
    }

    @Override
    public WorkOrder getValidNumOfOrder(String openId, String orderId) throws Exception {
        log.info("getValidNumOfOrder param: openId={}, orderId={}",openId,orderId);
        int validNum;

        openId = openId.trim();
        orderId = orderId.trim();
        List<WorkOrder> list;
        try {
            list = workOrderDao.selectByOrderId(orderId);
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        if (null == list || 0 == list.size()) {
            return null;
        }

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
                    && !WorkOrderStatusType.REJECT.getCode().equals(wo.getStatus())) {
                usedNum += wo.getReturnedNum();
            }
        }

        validNum = goodsNum - usedNum;
        if (0 > validNum) {
            validNum = 0;
        }
        WorkOrder workOrder = list.get(0);
        workOrder.setReturnedNum(validNum);//???????????????
        log.info("getValidNumOfOrder : {}",JSON.toJSONString(workOrder));
        return workOrder;

    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject getOrderInfo(String openId, String subOrderId, Long merchantId) throws Exception{
        QueryOrderBodyBean body = new QueryOrderBodyBean();
        body.setOpenId(openId);
        body.setPageIndex(1);
        body.setPageSize(10);
        body.setSubOrderId(subOrderId);
        Map<String, Object> map = new HashedMap();
        map.put("merchant", 0);//fix it
        if (null == orderService) {
            throw new Exception("???????????????: order");
        }
        OperaResult result = orderService.getOrderList(body,map);
        if (null == result) {
            throw new Exception("order searchOrder got response null");
        }
        log.info("searchOrder result : {}",JSON.toJSONString(result));
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

        //JSONObject json = JSON.parseObject(body);
        String result = "fail";
        String outerRefundNo = bean.getOutRefundNo();//json.getString("outRefundNo");
        // tradeNo = bean.getSourceOutTradeNo();//json.getString("sourceOutTradeNo");//
        //Float refundAmount = json.getFloat("totalFee");//Float.valueOf(bean.getRefundFee());
        String refundTimeStr = bean.getTradeDate();//json.getString("tradeDate");//
        Integer status = bean.getStatus();//json.getInteger("status");//
        String refundFeeStr = bean.getRefundFee();//json.getFloat("refundFee");

        if (null == status) {
            log.error("???????????????????????? post body is wrong, status is null");
            return result;
        }
        if (null == outerRefundNo || outerRefundNo.isEmpty()) {
            log.error("???????????????????????? post body is wrong, outerRefundNo is null");
            return result;
        }
        //if ( null == tradeNo || tradeNo.isEmpty()) {
        ////    log.error("aggpays notify post body is wrong, tradeNo is null");
        //   return result;
        //}
        if (null == refundFeeStr) {
            log.error("???????????????????????? post body is wrong, refundFee is null");
            return result;
        }
        WorkOrder wo = workOrderDao.selectByRefundNo(outerRefundNo);
        if (null == wo) {
            log.warn("???????????????????????? handle notify, but not found work-order by refundNo: " + outerRefundNo);
            return result;
        }

        BigDecimal decRefundFee = new BigDecimal(refundFeeStr);
        BigDecimal dec100f = new BigDecimal("100");
        Float refundFee = decRefundFee.divide(dec100f).floatValue();
        if (0 > refundFee) {
            log.warn("notify parameters refund_amount abnormal");
            return result;
        }

        if (1 == status || 3 == status || 2 == status) {//2:????????????, 1:????????? 3???????????????
            if (null != refundTimeStr && !refundTimeStr.isEmpty()) {
                try {
                    wo.setRefundTime(StringUtil.String2Date(refundTimeStr));
                } catch (Exception ex) {
                    log.error("convert refundTime error {}", ex.getMessage());
                }
            }
            if (1 == status) {
                wo.setGuanaitongRefundAmount(refundFee);
                String msg = "????????????????????????";
                log.error(msg);
                wo.setComments(msg);
            } else if (3 == status) {
                wo.setGuanaitongRefundAmount(refundFee);
                String msg = "??????????????????????????????";
                log.error(msg);
                wo.setComments(msg);
            } else {
                String msg = "????????????????????????";
                log.error(msg);
                wo.setComments(msg);
            }
        }

        wo.setStatus(WorkOrderStatusType.CLOSED.getCode());

        try {
            workOrderDao.updateByPrimaryKey(wo);
        } catch (Exception ex) {
            log.error("sql error when insert work-order " + ex.getMessage());
            return result;
        }
        log.info("AggPays refund notify handle success");

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
        WorkOrder wo = workOrderDao.selectByRefundNo(outer_refund_no);
        if (null == wo) {
            log.warn("handle notify, but not found work-order by refundNo: "+outer_refund_no);
            return result;
        }

        if (!appid.equals(wo.gettAppId()) ||
                !outer_trade_no.equals(wo.getTradeNo()) ) {
            log.warn("notify parameters do not match work-order");
            return result;
        }

        if (0 >= refund_amount) {
            log.warn("notify parameters refund_amount abnormal");
            return result;
        }

        wo.setRefundTime(new Date());
        wo.setGuanaitongRefundAmount(refund_amount);
        wo.setGuanaitongTradeNo(trade_no);
        wo.setStatus(WorkOrderStatusType.CLOSED.getCode());
        wo.setRefundTime(new Date());
        try {
            workOrderDao.updateByPrimaryKey(wo);
        } catch (Exception ex) {
            log.error("sql error when insert work-order " + ex.getMessage());
            return result;
        }
        log.info("????????? refund notify handle success");

        return "success";
    }

    private boolean hasHandledFare(WorkOrder wo) throws Exception{

        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        if (null != wo.getParentOrderId()) {
            criteria.andParentOrderIdEqualTo(wo.getParentOrderId());
        }else {
            criteria.andOrderIdEqualTo(wo.getOrderId());
        }
        criteria.andFareGreaterThan(0f);
        criteria.andStatusEqualTo(WorkOrderStatusType.CLOSED.getCode());

        List<WorkOrder> list;
        try{
            list = mapper.selectByExample(example);
        }catch (Exception e) {
            log.error("workOrderMapper.selectByExample exception {}",e.getMessage());
            throw new Exception(e);
        }

        if (null == list || 1 > list.size()) {
            return false;
        }else {
            return true;
        }

    }

    @Override
    public String sendRefund2GuangAiTong(Long workOrderId, Integer handleFare, Float refund) throws Exception{

        log.info("sendRefund2GuangAiTong enter : workOrderId = ", workOrderId);
        WorkOrder wo = workOrderDao.selectByPrimaryKey(workOrderId);
        if (null == wo) {
            log.info("failed to find work-order record by id : " + workOrderId);
            throw new Exception("failed to find work-order record by id : "+workOrderId);
        }

        String result = "";
        log.info("find work-order: " + wo.toString());
        if (null == wo.getOrderId()) {
            log.error("sendRefund2GuangAiTong: can not find orderId in work_order");
            throw new Exception("can not find orderId in work_order");
        }

        String tradeNo = wo.getTradeNo();
        String appId = wo.gettAppId();
        Float refundAmount = (null == refund)?wo.getRefundAmount():refund;
        String reason = wo.getTitle();
        if (null == tradeNo || null == appId || null == refundAmount || null == reason) {
            log.warn("tradeNo, appId, refundAmount or reason are missing ");
            throw new Exception("check workOrder, found tradeNo, appId, refundAmount or reason are missing");
        }

        if (null != handleFare && 0 != handleFare) {
            boolean handledFare;
            try {
                handledFare = hasHandledFare(wo);
            } catch (Exception e) {
                throw new Exception(e);
            }

            if (handledFare) {
                throw new Exception("??????????????????????????????");
            }

            refundAmount += wo.getFare();
        }

        String notifyUrl = GuanAiTongConfig.getConfigGatNotifyUrl();//"http://api.weesharing.com/v2/workorders/refund/notify";

        Long timeStampMs = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long timeStampS = timeStampMs/1000;
        String timeStamp = timeStampS.toString();
        Random random = new Random();

        String triRandom = random.nextInt(1000) + "";
        StringBuilder sb = new StringBuilder();
        int randLength = triRandom.length();
        if (randLength < 3) {
            for (int i = 1; i <= 3 - randLength; i++)
                sb.append("0");
        }
        sb.append(triRandom);
        String refundNo = appId + timeStamp + sb.toString();

        GuanAiTongRefundBean bean = new GuanAiTongRefundBean();
        bean.setNotify_url(notifyUrl);
        bean.setOuter_refund_no(refundNo);
        bean.setOuter_trade_no(tradeNo);
        bean.setReason(reason);
        bean.setRefund_amount(refundAmount);

        ResultObject<String> gResult = guanAiTongClient.postRefund(bean);
        if (null == gResult) {
            log.warn("post to GuanAiTong refund failed");
            throw new Exception("post to GuanAiTong refund failed");
        }

        Integer code = gResult.getCode();
        String guanAiTongNo = gResult.getData();
        if (null == code || null == guanAiTongNo ||200 != code || guanAiTongNo.isEmpty()) {
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

            throw new Exception(errMsgSb.toString());
        }

        wo.setRefundNo(refundNo);
        wo.setGuanaitongTradeNo(guanAiTongNo);

        if (null == handleFare || 0 == handleFare) {
            //since we will check fare when create work-order. and set it by query order info.
            //to CLOSE work_order, if did not handle fare, set it to 0.00f, then can handle fare later.
            wo.setFare(0.00f);
        }
        try {
            workOrderDao.updateByPrimaryKey(wo);
        } catch (Exception ex) {
            log.error("update work-order sql error: " + ex.getMessage());
            throw new Exception(ex);
        }
        log.info("sendRefund2GuangAiTong success ");
        return guanAiTongNo;
    }

    @Override
    public List<WorkOrder> querySuccessRefundOrderDetailIdList(Long merchantId, Date startTime, Date endTime) throws Exception {
        List<WorkOrder> workOrderList = new ArrayList<>();

        try {
            log.info("???????????????????????? ??????????????? merchantId:{}, startTime:{}, endTime:{}", merchantId, startTime, endTime);
            workOrderList =
                    workOrderDao.selectRefundSuccessOrderDetailIdList(merchantId, startTime, endTime);
            log.info("???????????????????????? ???????????????:{}", JSONUtil.toJsonString(workOrderList));
        } catch (Exception e) {
            log.error("???????????????????????? ??????:{}", e.getMessage(), e);

            throw e;
        }

        return workOrderList;
    }
}
