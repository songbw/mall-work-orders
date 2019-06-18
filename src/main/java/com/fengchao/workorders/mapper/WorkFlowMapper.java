package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.WorkFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component(value = "WorkFlowMapper")
public interface WorkFlowMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WorkFlow record);

    int insertSelective(WorkFlow record);

    WorkFlow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkFlow record);

    int updateByPrimaryKey(WorkFlow record);

    List<WorkFlow> selectByWorkOrderId(@Param("workOrderId")Long workOrderId);

    List<WorkFlow> selectRange(@Param("sort") String sort, @Param("order") String order,
                               @Param("workOrderId")Long workOrderId,
                               @Param("sender") Long sender,
                               @Param("receiver") Long receiver,
                               @Param("createTimeStart") Date createTimeStart,
                               @Param("createTimeEnd") Date createTimeEnd);
}
