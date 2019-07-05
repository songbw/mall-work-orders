package com.fengchao.workorders.service.impl;

import com.github.pagehelper.PageHelper;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.util.PageInfo;
import com.fengchao.workorders.service.IOrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderTypeServiceImpl implements IOrderTypeService {

    private OrderTypeMapper orderTypeMapper;

    @Autowired
    public OrderTypeServiceImpl(OrderTypeMapper orderTypeMapper
                              ) {
        this.orderTypeMapper = orderTypeMapper;
    }

    @Override
    public Long insert(OrderType orderType) {
        int rst = orderTypeMapper.insertSelective(orderType);

        if (0 >= rst) {
            return 0L;
        }
        return orderType.getId();
    }

    @Override
    public OrderType selectById(long id) {

        return orderTypeMapper.selectByPrimaryKey(id);

    }

    @Override
    public OrderType selectByName(String name) {
        return orderTypeMapper.selectByName(name);
    }

    @Override
    public List<OrderType> selectAll() {
        return orderTypeMapper.selectAll();
    }

    @Override
    public PageInfo<OrderType> selectPage(int pageIndex, int pageSize, String sort, String order,
                                          String name, Date createTimeStart, Date createTimeEnd) {
        int counts = orderTypeMapper.selectRange(sort,order,name, createTimeStart, createTimeEnd).size();

        PageHelper.startPage(pageIndex,pageSize);
        List<OrderType> orderTypes = orderTypeMapper.selectRange(sort,order,name, createTimeStart, createTimeEnd);


        return new PageInfo<>(counts, pageSize, pageIndex,orderTypes);
    }

    @Override
    public void deleteById(Long id) {
        orderTypeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(OrderType orderType) {

        orderTypeMapper.updateByPrimaryKeySelective(orderType);
    }

}
