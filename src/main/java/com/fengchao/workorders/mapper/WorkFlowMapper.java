package com.fengchao.workorders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fengchao.workorders.entity.WorkFlow;
import com.fengchao.workorders.model.WorkFlowExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author Clark
 * */

@Mapper
@Component
public interface WorkFlowMapper extends BaseMapper<WorkFlow> {

}
