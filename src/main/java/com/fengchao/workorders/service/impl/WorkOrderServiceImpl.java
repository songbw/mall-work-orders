package com.fengchao.workorders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.QueryOrderBodyBean;
import com.fengchao.workorders.feign.OrderService;
import com.fengchao.workorders.util.OperaResult;
import com.fengchao.workorders.util.WorkOrderType;
import com.github.pagehelper.PageHelper;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.dao.impl.WorkOrderDaoImpl;
import com.fengchao.workorders.service.IWorkOrderService;
import com.fengchao.workorders.util.PageInfo;
//import org.joda.time.DateTime;
//import org.springframework.beans.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
//import org.springframework.util.StringUtils;

//import java.io.IOException;
//import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(value="WorkOrderServiceImpl")
@Transactional
public class WorkOrderServiceImpl implements IWorkOrderService {


    private OrderService orderService;
    private WorkOrderDaoImpl workOrderDao;
    private RestTemplate restTemplate;

    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkOrderServiceImpl(WorkOrderDaoImpl workOrderDao,
                                RestTemplate restTemplate,
                                OrderService orderService
                              ) {
        this.workOrderDao = workOrderDao;
        this.restTemplate = restTemplate;
        this.orderService = orderService;
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
                                          String orderId, Long typeId, Long merchantId,Integer status,
                                          Date finishTimeStart, Date finishTimeEnd,
                                         Date createTimeStart, Date createTimeEnd) {

        int counts = workOrderDao.selectRange(sort, order,
                                                    title, receiverId, receiverPhone, receiverName,
                                                    orderId, merchantId,typeId,status,
                                                    finishTimeStart, finishTimeEnd,
                                                    createTimeStart, createTimeEnd).size();

        PageHelper.startPage(pageIndex, pageSize);
        List<WorkOrder> workOrders = workOrderDao.selectRange(sort, order,
                title, receiverId, receiverPhone, receiverName,
                orderId, merchantId,typeId,status,
                finishTimeStart, finishTimeEnd,
                createTimeStart, createTimeEnd);

        return new PageInfo<>(counts, pageSize, pageIndex,workOrders);
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
        long typeId;

        typeId = (long)WorkOrderType.RETURN.getCode();
        int c1 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        typeId = (long)WorkOrderType.REFUND.getCode();
        int c2 = workOrderDao.countType(typeId,createTimeStart, createTimeEnd);

        return c1 + c2;
    }

    @Override
    public WorkOrder getValidNumOfOrder(String openId, String orderId) {
        int validNum = 0;

        openId = openId.trim();
        orderId = orderId.trim();

        try {
            List<WorkOrder> list = workOrderDao.selectByOrderId(orderId);
            if (null != list && 0 < list.size()) {
                WorkOrder baseWO = list.get(0);

                int usedNum = 0;
                int goodsNum= 0;
                String open_id = baseWO.getReceiverId();
                if (null != open_id && openId.equals(open_id) && null != baseWO.getOrderGoodsNum()) {
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
            if (null == jsonString) {
                return null;
            }
            JSONObject theJson = JSON.parseObject(jsonString);
            if (null == theJson) {
                return null;
            }
            JSONArray theList = theJson.getJSONArray("list");
            if (null == theList || 0 == theList.size()) {
                return null;
            }
            List<JSONObject> list = JSONObject.parseArray(JSON.toJSONString(theList), JSONObject.class);
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
}
