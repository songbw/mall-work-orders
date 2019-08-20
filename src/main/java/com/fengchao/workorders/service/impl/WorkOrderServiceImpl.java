package com.fengchao.workorders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.GuanAiTongNotifyBean;
import com.fengchao.workorders.bean.GuanAiTongRefundBean;
import com.fengchao.workorders.bean.QueryOrderBodyBean;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                                         Date createTimeStart, Date createTimeEnd) throws Exception{

        PageInfo<WorkOrder> pageInfo;
        try {

            pageInfo = workOrderDao.selectRange(pageIndex,pageSize,sort, order,
                    title, receiverId, receiverPhone, receiverName,
                    orderId, merchantId, typeId, status,
                    createTimeStart, createTimeEnd);
        }catch (Exception e) {
            throw new Exception(e);
        }
        return pageInfo;
    }

    @Override
    public List<WorkOrder> selectByOrderId(String orderId) {
        orderId = orderId.trim();

        try {
            List<WorkOrder> list = workOrderDao.selectByOrderId(orderId);
            if (null == list || 0 == list.size()) {
                log.info(" not found record by orderId: " + orderId);
                return null;
            } else {
                log.info("=== find: " + list);
                return list;
            }
        } catch (Exception ex) {
            log.error("selectByOrderId sql error: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public int countReturn(Date createTimeStart, Date createTimeEnd) {
        int typeId;

        typeId = WorkOrderType.RETURN.getCode();
        int c1 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        typeId = WorkOrderType.REFUND.getCode();
        int c2 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        return c1 + c2;
    }

    @Override
    public List<WorkOrder> selectByTimeRange(Date createTimeStart, Date createTimeEnd) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();

        criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);

        return mapper.selectByExample(example);
    }

    @Override
    public Integer queryRefundUserCount(Long merchantId) throws Exception{
        log.info("获取商户的退货人数 数据库入参:{}", merchantId);

        Integer count;
        try {
            count = workOrderDao.selectRefundUserCountByMerchantId(merchantId);
        }catch (Exception e) {
            throw new Exception(e);
        }
        log.info("获取商户的退货人数 数据库返回:{}", count);

        return count;
    }

    @Override
    public WorkOrder getValidNumOfOrder(String openId, String orderId) {
        int validNum ;

        if (null == openId || null == orderId) {
            WorkOrder w = new WorkOrder();
            w.setReturnedNum(0);
            return w;
        }

        openId = openId.trim();
        orderId = orderId.trim();

        try {
            List<WorkOrder> list = workOrderDao.selectByOrderId(orderId);
            if (null != list && 0 < list.size()) {
                WorkOrder baseWO = list.get(0);

                int usedNum = 0;
                int goodsNum= 0;
                String open_id = baseWO.getReceiverId();
                if (openId.equals(open_id) && null != baseWO.getOrderGoodsNum()) {
                    goodsNum = baseWO.getOrderGoodsNum();
                }
                for (WorkOrder wo : list) {
                    if (null != wo.getReceiverId() && wo.getReceiverId().equals(openId) && null != wo.getReturnedNum()) {
                        usedNum += wo.getReturnedNum();
                    }
                }

                validNum = goodsNum - usedNum;
                if (0 > validNum) {
                    validNum = 0;
                }
                WorkOrder workOrder = list.get(0);
                workOrder.setReturnedNum(validNum);
                return workOrder;
            } else {
                log.info(" not found record by orderId: " + orderId);
                return null;
            }
        } catch (Exception ex) {
            log.error("selectByOrderId sql error: " + ex.getMessage());
            WorkOrder mockRecord = new WorkOrder();
            mockRecord.setReturnedNum(-1);
            return mockRecord;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject getOrderInfo(String openId, String subOrderId, Long merchantId) {
        QueryOrderBodyBean body = new QueryOrderBodyBean();
        body.setOpenId(openId);
        body.setPageIndex(1);
        body.setPageSize(10);
        body.setSubOrderId(subOrderId);
        body.setStatus(2);
        Map<String, Object> map = new HashedMap();
        map.put("merchant", merchantId);
        OperaResult result = orderService.getOrderList(body,map);
        log.info(result.toString());
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
            log.info("getOrderInfo, orderInfo: " + list.get(0).toString());
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

        wo.setGuanaitongRefundAmount(refund_amount);
        wo.setGuanaitongTradeNo(trade_no);
        wo.setStatus(WorkOrderStatusType.CLOSED.getCode());

        try {
            workOrderDao.updateByPrimaryKey(wo);
        } catch (Exception ex) {
            log.error("sql error when insert work-order " + ex.getMessage());
            return result;
        }
        log.info("关爱通 refund notify handle success");

        return "success";
    }

    @Override
    public String sendRefund2GuangAiTong(Long workOrderId) {

        log.info("sendRefund2GuangAiTong enter : workOrderId = ", workOrderId);
        WorkOrder wo = workOrderDao.selectByPrimaryKey(workOrderId);
        if (null == wo) {
            log.info("failed to find work-order record by id : " + workOrderId);
            return null;
        }

        String result = "";
        log.info("find work-order: " + wo.toString());
        String tradeNo = wo.getTradeNo();
        String appId = wo.gettAppId();
        Float refundAmount = wo.getRefundAmount();
        String reason = wo.getTitle();
        if (null == tradeNo || null == appId || null == refundAmount || null == reason) {
            log.warn("find wrong value in work-order ");
            return result;
        }

        String notifyUrl = "http://api.weesharing.com/v2/workorders/refund/notify";

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
            return result;
        }

        Integer code = gResult.getCode();
        String guanAiTongNo = gResult.getData();
        if (null == code || null == guanAiTongNo ||200 != code || guanAiTongNo.isEmpty()) {
            log.info("post to GuanAiTong refund failed : {}", gResult.getMsg());

            if (null != code) {
                result = "Error: guanAiTong error: " + code.toString() + " : "+gResult.getMsg();
                log.info(result);
            }
            return result;
        }

        wo.setRefundNo(refundNo);
        wo.setGuanaitongTradeNo(guanAiTongNo);
        try {
            workOrderDao.updateByPrimaryKey(wo);
        } catch (Exception ex) {
            log.error("update work-order sql error: " + ex.getMessage());
        }
        log.info("sendRefund2GuangAiTong success ");
        return guanAiTongNo;
    }
}
