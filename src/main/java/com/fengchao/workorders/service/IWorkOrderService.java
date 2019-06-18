package com.fengchao.workorders.service;

import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.util.PageInfo;

//import java.io.Serializable;
import java.util.Date;
import java.util.List;
//import java.util.Map;

public interface IWorkOrderService {

    Long insert(WorkOrder workOrder);

    void deleteById(long id);

    WorkOrder selectById(long id);

    void update(WorkOrder workOrder);

    PageInfo<WorkOrder> selectPage(int page, int rows, String sort, String order,
                                   String title, String description, String customer,
                                   String receptionist, Long typeId,  Integer urgentDegree,Integer status,
                                   Date finishTimeStart, Date finishTimeEnd,
                                   Date createTimeStart, Date createTimeEnd);

    List<WorkOrder> selectByOrderId(Long orderId);

}
