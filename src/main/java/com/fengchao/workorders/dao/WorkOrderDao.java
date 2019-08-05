package com.fengchao.workorders.dao;

import com.fengchao.workorders.model.WorkOrder;
import java.util.Date;
import java.util.List;

public interface WorkOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(WorkOrder record);

    int insertSelective(WorkOrder record);

    WorkOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkOrder record);

    int updateByPrimaryKey(WorkOrder record);

    List<WorkOrder> selectByOrderId(String orderId);

    List<WorkOrder> selectRange(String sort, String order,
                                String title,
                                String receiverId,
                                String receiverPhone,
                                String receiverName,
                                String orderId,
                                Long merchantId,
                                Integer typeId,
                                Integer status,
                                Date finishTimeStart,
                                Date finishTimeEnd,
                                Date createTimeStart,
                                Date createTimeEnd);

    int countType(Integer typeId,
                  Date createTimeStart,
                  Date createTimeEnd);

    WorkOrder selectByRefundNo(String refundNo);
}
