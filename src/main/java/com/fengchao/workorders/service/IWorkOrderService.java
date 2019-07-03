package com.fengchao.workorders.service;

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
                                   String orderId, Long typeId, Long merchantId,Integer status,
                                   Date finishTimeStart, Date finishTimeEnd,
                                   Date createTimeStart, Date createTimeEnd);

    List<WorkOrder> selectByOrderId(Long orderId);

}
