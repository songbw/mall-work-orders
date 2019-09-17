package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.WorkFlow;
import com.fengchao.workorders.model.WorkFlowExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "WorkFlowMapper")
public interface WorkFlowMapper {
    long countByExample(WorkFlowExample example);

    int deleteByExample(WorkFlowExample example);

    int deleteByPrimaryKey(Long id);

    int insert(WorkFlow record);

    int insertSelective(WorkFlow record);

    List<WorkFlow> selectByExample(WorkFlowExample example);

    WorkFlow selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") WorkFlow record, @Param("example") WorkFlowExample example);

    int updateByExample(@Param("record") WorkFlow record, @Param("example") WorkFlowExample example);

    int updateByPrimaryKeySelective(WorkFlow record);

    int updateByPrimaryKey(WorkFlow record);
}
