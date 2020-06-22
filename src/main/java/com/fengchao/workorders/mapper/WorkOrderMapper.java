package com.fengchao.workorders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fengchao.workorders.entity.WorkOrder;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

}
