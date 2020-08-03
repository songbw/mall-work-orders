package com.fengchao.workorders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.config.GuanAiTongConfig;
import com.fengchao.workorders.feign.IGuanAiTongClient;
import com.fengchao.workorders.feign.OrderService;
import com.fengchao.workorders.mapper.WorkOrderMapper;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.dao.impl.WorkOrderDaoImpl;
import com.fengchao.workorders.service.IWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * 工单处理
 * @author Clark
 * @date 2019/09/09
 */

@Slf4j
@Service(value="WorkOrderServiceImpl")
@Transactional(rollbackFor = Exception.class)
public class WorkOrderServiceImpl implements IWorkOrderService {

    private OrderService orderService;
    private IGuanAiTongClient guanAiTongClient;
    private WorkOrderDaoImpl workOrderDao;
    private WorkOrderMapper mapper;

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
    public List<WorkOrder> selectByOrderId(String orderId) throws Exception{
        orderId = orderId.trim();
        List<WorkOrder> list;
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

    /**
     * 统计特定订单的售后工单
     * return: 如果没有工单记录，直接返回 null;
     *         如果有工单记录，返回可以售后的商品数量
     */
    @Override
    public WorkOrder getValidNumOfOrder(String orderId) throws Exception {
        int validNum;

        orderId = orderId.trim();
        List<WorkOrder> list;
        try {
            list = workOrderDao.selectByOrderId(orderId);
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        if (null == list || 0 == list.size()) {
            log.info("订单 {} 没有售后记录, 可以直接创建售后工单",orderId);
            return null;
        }

        WorkOrder baseWo = list.get(0);

        int usedNum = 0;
        int goodsNum = 0;
        if (null != baseWo.getOrderGoodsNum()) {
            goodsNum = baseWo.getOrderGoodsNum();
        }
        for (WorkOrder wo : list) {
            //以下情况认为售后完成：已经/正在退款. (已经完成换货的,审核失败的, 没有用户ID及无效记录除外）
            boolean isValidRecord = (null != wo.getReturnedNum() && null != wo.getStatus()
                    && !(WorkOrderStatusType.CLOSED.getCode().equals(wo.getStatus()) && WorkOrderType.EXCHANGE.getCode().equals(wo.getTypeId()))
                    && !WorkOrderStatusType.REJECT.getCode().equals(wo.getStatus())
                    );
            if (isValidRecord ) {
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
        //fix below merchant=0 means admin
        map.put(Constant.MERCHANT_KEY, 0);
        if (null == orderService) {
            throw new Exception("未发现服务: order");
        }
        OperaResult result = orderService.getOrderList(body,map);
        if (null == result) {
            throw new Exception("order searchOrder got response null");
        }
        log.info("searchOrder result : {}",JSON.toJSONString(result));
        if (result.getCode().equals(Constant.HTTP_STATUS_CODE_OK)) {
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
    public String handleNotify(GuanAiTongNotifyBean bean) {

        String result = "fail";
        String outerRefundNo = bean.getOuter_refund_no();
        String tradeNo = bean.getTrade_no();
        String appid = bean.getAppid();
        String outerTradeNo = bean.getOuter_trade_no();
        Float refundAmount = bean.getRefund_amount();

        if (null == outerRefundNo || outerRefundNo.isEmpty() ||
               null == tradeNo || tradeNo.isEmpty() ||
            null == appid || appid.isEmpty() ||
            null == outerTradeNo || outerTradeNo.isEmpty() ||
            null == refundAmount) {
            log.error("关爱通退款回调处理 参数不全: outerRefundNo, tradeNo, outerTradeNo, refundAmount");
            return result;
        }
        WorkOrder wo = workOrderDao.selectByRefundNo(outerRefundNo);
        if (null == wo) {
            log.error("关爱通退款回调处理 not found work-order by refundNo: {}",outerRefundNo);
            return result;
        }

        if (!appid.equals(wo.gettAppId()) ||
                !outerTradeNo.equals(wo.getTradeNo()) ) {
            log.error("关爱通退款回调处理 notify parameters do not match work-order");
            return result;
        }

        if (0 >= refundAmount) {
            log.error("关爱通退款回调处理 notify parameters refund_amount abnormal");
            return result;
        }

        if (!wo.getStatus().equals(WorkOrderStatusType.REFUNDING.getCode())){
            int timeOut = 3;
            for(int i = 0; i < timeOut; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                //再次确认工单状态
                wo = workOrderDao.selectByRefundNo(outerRefundNo);
                if (null != wo && wo.getStatus().equals(WorkOrderStatusType.REFUNDING.getCode())){
                    break;
                }
            }
        }

        if (null != wo) {
            wo.setGuanaitongRefundAmount(refundAmount);

            wo.setGuanaitongTradeNo(tradeNo);
            wo.setStatus(WorkOrderStatusType.CLOSED.getCode());
            wo.setUpdateTime(new Date());
            wo.setRefundTime(new Date());
            try {
                workOrderDao.updateByPrimaryKey(wo);
            } catch (Exception ex) {
                log.error("关爱通退款回调处理 sql error when update work-order {}" , ex.getMessage());
                return result;
            }
        }else{
            log.warn("关爱通退款回调处理 再次确认工单状态选择工单记录失败");
        }
        log.info("关爱通 refund notify handle success {}",JSON.toJSONString(wo));

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

        return !(null == list || 1 > list.size());

    }


    private void sendTradeInfo(WorkOrder wo, Float refundAmount) {

        /* 发送交易详情 */

        JSONObject json;
        try {
            json = getOrderInfo(wo.getReceiverId(), wo.getOrderId(), wo.getMerchantId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }


        if (null == json) {
            log.error("获取订单记录失败, 不发送交易详情到关爱通");
            return;
        }
        Object skuIdObj = json.get("skuId");
        Object nameObj = json.get("name");
        if (null == skuIdObj || null == nameObj) {
            log.error("订单skuId,name失败, 不发送交易详情到关爱通");
            return;
        }
        String nameStr = nameObj.toString();
        String skuIdStr = skuIdObj.toString();

        GuanAiTongGoodsDetailBean goodsDetail = new GuanAiTongGoodsDetailBean();
        goodsDetail.setSku_id(skuIdStr);
        goodsDetail.setName((50 < nameStr.length()) ? nameStr.substring(0, 50) : nameStr);
        goodsDetail.setQuantity(wo.getReturnedNum());
        Float amount = Float.valueOf(FeeUtil.Fen2Yuan(wo.getPaymentAmount().toString()));
        goodsDetail.setGood_pay_amount(amount);
        goodsDetail.setGood_cost_amount(amount);

        List<GuanAiTongGoodsDetailBean> goodsDetailBeans = new ArrayList<>();
        goodsDetailBeans.add(goodsDetail);

        GuanAiTongTradeInfo tradeInfo = new GuanAiTongTradeInfo();
        tradeInfo.setGoods_detail(goodsDetailBeans);
        //fix below. 是否有子订单 1有 2无
        tradeInfo.setIs_third_orders(2);
        tradeInfo.setThird_trade_no(wo.getTradeNo());
        tradeInfo.setThird_refund_no(wo.getRefundNo());
        tradeInfo.setThird_refund_amount(refundAmount);
        tradeInfo.setThird_cost_amount(amount);

        GuanAiTongTradeInfoPostBean infoPostBean = new GuanAiTongTradeInfoPostBean();
        infoPostBean.setOuter_trade_no(wo.getTradeNo());
        infoPostBean.setOuter_refund_no(wo.getRefundNo());
        infoPostBean.setTrade_info(tradeInfo);

        try {
            ResultObject<String> resp = guanAiTongClient.postTradeInfo(infoPostBean);
            log.info("发送关爱通交易详情接口 返回： {}", JSON.toJSONString(resp));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        /* End*/

    }

    @Override
    public String sendRefund2GuangAiTong(Long workOrderId, Integer handleFare, Float refund) throws Exception{

        log.info("sendRefund2GuangAiTong enter : workOrderId = {}, refundAmount={}", workOrderId,refund);
        WorkOrder wo = workOrderDao.selectByPrimaryKey(workOrderId);
        if (null == wo) {
            log.info("failed to find work-order record by id: {}" , workOrderId);
            throw new Exception("failed to find work-order record by id : "+workOrderId);
        }

        log.info("find work-order: {}", JSON.toJSONString(wo));
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
                throw new Exception("该订单已经处理过运费");
            }

            refundAmount += wo.getFare();
        }

        String notifyUrl = GuanAiTongConfig.getConfigGatNotifyUrl();

        Long timeStampMs = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long timeStampS = timeStampMs/1000;
        String timeStamp = timeStampS.toString();
        Random random = new Random();

        String triRandom = random.nextInt(1000) + "";
        StringBuilder sb = new StringBuilder();
        int randomSize = 3;
        int randLength = triRandom.length();
        if (randLength < randomSize) {
            for (int i = 1; i <= randomSize - randLength; i++) {
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

        ResultObject<String> gResult = guanAiTongClient.postRefund(bean);
        if (null == gResult) {
            log.error("post to GuanAiTong refund failed");
            throw new Exception("post to GuanAiTong refund failed");
        }

        Integer code = gResult.getCode();
        String guanAiTongNo = gResult.getData();
        if ( null == guanAiTongNo ||
                !Constant.HTTP_STATUS_CODE_OK.equals(code) || guanAiTongNo.isEmpty()) {
            log.error("post to GuanAiTong refund failed : {}", gResult.getMsg());
            StringBuilder errMsgSb = new StringBuilder();
            errMsgSb.append("got error from Guan Ai Tong ");
            if (null != code) {
                errMsgSb.append("Error code = ");
                errMsgSb.append(code.toString());
            }
            if (null != gResult.getMsg()) {
                errMsgSb.append(" message = ");
                errMsgSb.append(gResult.getMsg());
            }

            throw new Exception(errMsgSb.toString());
        }

        try {
            wo = workOrderDao.selectByPrimaryKey(workOrderId);
            if (null != wo) {
                if (!WorkOrderStatusType.CLOSED.getCode().equals(wo.getStatus())){
                    wo.setStatus(WorkOrderStatusType.REFUNDING.getCode());
                }
                wo.setRefundAmount(refundAmount);
                wo.setRefundNo(refundNo);
                wo.setGuanaitongTradeNo(guanAiTongNo);
                if (null == handleFare || 0 == handleFare) {
                    //since we will check fare when create work-order. and set it by query order info.
                    //to CLOSE work_order, if did not handle fare, set it to 0.00f, then can handle fare later.
                    wo.setFare(0.00f);
                }
                wo.setUpdateTime(new Date());
                workOrderDao.updateByPrimaryKey(wo);
            }
        } catch (Exception ex) {
            log.error("update work-order sql error: {}", ex.getMessage(),ex);
            throw new Exception(ex);
        }
        log.info("sendRefund2GuangAiTong success {}",JSON.toJSONString(wo));

        /* 发送交易详情 */
        //sendTradeInfo(wo,refundAmount);
        /* End*/

        return guanAiTongNo;
    }

    @Override
    public List<ThirdWorkOrderBean> selectWorkOrderByOrderId(Integer orderId) {
        try {
            List<WorkOrder> workOrders = workOrderDao.selectByParentOrderId(orderId) ;
            List<ThirdWorkOrderBean> thirdWorkOrderBeans = new ArrayList<>() ;
            workOrders.forEach(workOrder -> {
                ThirdWorkOrderBean thirdWorkOrderBean = new ThirdWorkOrderBean();
                BeanUtils.copyProperties(workOrder, thirdWorkOrderBean);
                List<WorkFlow> workFlows = workOrderDao.selectWorkFlowByWorkOrderId(workOrder.getId()) ;
                thirdWorkOrderBean.setWorkFlowList(workFlows);
                thirdWorkOrderBeans.add(thirdWorkOrderBean) ;
            });
            return thirdWorkOrderBeans ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<WorkOrder> querySuccessRefundOrderDetailIdList(Long merchantId, Date startTime, Date endTime) {
        List<WorkOrder> workOrderList;
        try {
            log.info("查询已退款的记录 数据库入参 merchantId:{}, startTime:{}, endTime:{}", merchantId, startTime, endTime);
            workOrderList =
                    workOrderDao.selectRefundSuccessOrderDetailIdList(merchantId, startTime, endTime);
            log.info("查询已退款的记录 数据库返回:{}", JSONUtil.toJsonString(workOrderList));
        } catch (Exception e) {
            log.error("查询已退款的记录 异常:{}", e.getMessage(), e);

            throw e;
        }

        return workOrderList;
    }
}
