package com.fengchao.workorders.service.impl;

import com.fengchao.workorders.mapper.DefaultAddressMapper;
import com.fengchao.workorders.model.DefaultAddress;
import com.fengchao.workorders.model.DefaultAddressExample;
import com.fengchao.workorders.service.IDefaultAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
//@Service
public class DefaultAddressServiceImpl implements IDefaultAddressService {

    private DefaultAddressMapper mapper;

    @Autowired
    public DefaultAddressServiceImpl(DefaultAddressMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public Long insert(DefaultAddress record) throws Exception{
        int newId;

        try {
            newId =mapper.insert(record);
        }catch (Exception e){
            log.error("defaultAddressMapper exception {}",e.getMessage());
            throw new Exception(e);
        }

        if (0 == newId){
            log.error("defaultAddressMapper insert failed");
            throw new Exception("defaultAddressMapper insert failed");
        }

        return record.getId();
    }

    @Override
    public void deleteById(long id) throws Exception{
        try {
            mapper.deleteByPrimaryKey(id);
        }catch (Exception e){
            log.error("defaultAddressMapper exception {}",e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public DefaultAddress selectById(long id) throws Exception{
        try {
            return mapper.selectByPrimaryKey(id);
        }catch (Exception e){
            log.error("defaultAddressMapper exception {}",e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public void update(DefaultAddress record) throws Exception{
        try {
            mapper.updateByPrimaryKeySelective(record);
        }catch (Exception e){
            log.error("defaultAddressMapper exception {}",e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public List<DefaultAddress> selectAll() throws Exception{
        DefaultAddressExample example = new DefaultAddressExample();
        DefaultAddressExample.Criteria criteria = example.createCriteria();
        criteria.andIdIsNotNull();

        try {
            return mapper.selectByExample(example);
        }catch (Exception e){
            log.error("defaultAddressMapper exception {}",e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public DefaultAddress selectDefault() throws Exception{
        DefaultAddressExample example = new DefaultAddressExample();
        DefaultAddressExample.Criteria criteria = example.createCriteria();
        criteria.andAsDefaultEqualTo(1);
        List<DefaultAddress> list;
        try {
            list = mapper.selectByExample(example);
        }catch (Exception e){
            log.error("defaultAddressMapper exception {}",e.getMessage());
            throw new Exception(e);
        }
        if (null != list && 0 < list.size()){
            return list.get(0);
        } else {
            throw new Exception("failed to find default address");
        }

    }

}
