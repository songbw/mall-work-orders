package com.fengchao.workorders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fengchao.workorders.entity.DefaultAddress;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface DefaultAddressMapper extends BaseMapper<DefaultAddress> {

}
