package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.OrderType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component(value = "OrderTypeMapper")
public interface OrderTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderType record);

    int insertSelective(OrderType record);

    OrderType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderType record);

    int updateByPrimaryKey(OrderType record);

    List<OrderType> selectAll();

    OrderType selectByName(@Param("name") String name);

    List<OrderType> selectRange(@Param("sort") String sort, @Param("order") String order,
                                @Param("name") String name,
                                @Param("createTimeStart") Date createTimeStart,
                                @Param("createTimeEnd") Date createTimeEnd);
}
