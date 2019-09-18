package com.fengchao.workorders.service;

import com.fengchao.workorders.model.DefaultAddress;

import java.util.List;


public interface IDefaultAddressService {
    Long insert(DefaultAddress workFlow) throws Exception;

    void deleteById(long id) throws Exception;

    DefaultAddress selectById(long id) throws Exception;

    void update(DefaultAddress workFlow) throws Exception;

    List<DefaultAddress> selectAll() throws Exception;

    DefaultAddress selectDefault() throws Exception;
}
