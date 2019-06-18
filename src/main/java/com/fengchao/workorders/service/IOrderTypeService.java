package com.fengchao.workorders.service;

import com.fengchao.workorders.util.PageInfo;
import com.fengchao.workorders.model.OrderType;

import java.util.Date;
import java.util.List;

public interface IOrderTypeService {

    Long insert(OrderType orderType);

    void update(OrderType orderType);

    OrderType selectById(long id);

    PageInfo<OrderType> selectPage(int page, int row, String sort, String order,
                                   String name, Date createTimeStart, Date createTimeEnd);

    List<OrderType> selectAll();

    void deleteById(Long id);

    OrderType selectByName(String name);
}
