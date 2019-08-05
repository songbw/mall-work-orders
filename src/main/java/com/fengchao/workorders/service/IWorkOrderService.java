package com.fengchao.workorders.service;

import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.GuanAiTongNotifyBean;
import com.fengchao.workorders.bean.GuanAiTongRefundBean;
import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.util.PageInfo;

//import java.io.Serializable;
import java.util.Date;
import java.util.List;
//import java.util.Map;

public interface IWorkOrderService {

    Long insert(WorkOrder workOrder);

    void deleteById(Long id);

    WorkOrder selectById(Long id);

    void update(WorkOrder workOrder);

    PageInfo<WorkOrder> selectPage(int page, int rows, String sort, String order,
                                   String title, String receiverId, String receiverName, String receiverPhone,
                                   String orderId, Integer typeId, Long merchantId,Integer status,
                                   Date finishTimeStart, Date finishTimeEnd,
                                   Date createTimeStart, Date createTimeEnd);

    List<WorkOrder> selectByOrderId(String orderId);

    int countReturn(Date createTimeStart, Date createTimeEnd);

    WorkOrder getValidNumOfOrder(String openId, String sbuOrderId);

    JSONObject getOrderInfo(String openId, String sbuOrderId, Long merchantId);

    String handleNotify(GuanAiTongNotifyBean backBean);

    String sendRefund2GuangAiTong(Long workOrderId);
}
