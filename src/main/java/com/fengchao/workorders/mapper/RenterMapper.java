package com.fengchao.workorders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fengchao.workorders.entity.Renter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author Clark
 * */
@Mapper
@Component
public interface RenterMapper extends BaseMapper<Renter> {
}
