package com.fengchao.workorders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fengchao.workorders.entity.WorkFlow;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author Clark
 * */

@Mapper
@Component
public interface WorkFlowMapper extends BaseMapper<WorkFlow> {

}
