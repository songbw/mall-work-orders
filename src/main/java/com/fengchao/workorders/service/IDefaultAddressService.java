package com.fengchao.workorders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fengchao.workorders.entity.DefaultAddress;

import java.util.List;


public interface IDefaultAddressService extends IService<DefaultAddress> {

    DefaultAddress createRecord(DefaultAddress address);

    DefaultAddress updateRecordById(DefaultAddress address,Long id);

    DefaultAddress selectById(Long id);

    List<DefaultAddress> selectAll() throws Exception;

    DefaultAddress selectDefault() throws Exception;
}
