package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component(value = "WorkOrderMapper")
public interface WorkOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WorkOrder record);

    int insertSelective(WorkOrder record);

    WorkOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkOrder record);

    int updateByPrimaryKey(WorkOrder record);

    List<WorkOrder> selectByOrderId(@Param("orderId")Long orderId);

    List<WorkOrder> selectRange(@Param("sort") String sort, @Param("order") String order,
                               @Param("title") String title,
                               @Param("description") String description,
                               @Param("customer") String customer,
                               @Param("orderId") String orderId,
                               @Param("receptionist") String receptionist,
                               @Param("typeId") Long typeId,
                               @Param("urgentDegree") Integer urgentDegree,
                               @Param("status") Integer status,
                               @Param("finishTimeStart") Date finishTimeStart,
                               @Param("finishTimeEnd") Date finishTimeEnd,
                               @Param("createTimeStart") Date createTimeStart,
                               @Param("createTimeEnd") Date createTimeEnd);
}
