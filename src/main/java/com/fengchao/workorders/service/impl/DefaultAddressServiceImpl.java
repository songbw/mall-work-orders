package com.fengchao.workorders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengchao.workorders.constants.AsDefaultEnum;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.entity.DefaultAddress;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.mapper.DefaultAddressMapper;
import com.fengchao.workorders.service.IDefaultAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Clark
 * */

@Slf4j
@Transactional
@Service
public class DefaultAddressServiceImpl extends ServiceImpl<DefaultAddressMapper, DefaultAddress> implements IDefaultAddressService {

    @Override
    public DefaultAddress
    createRecord(DefaultAddress address){
        if(AsDefaultEnum.YES == address.getAsDefault()){
            checkDefaultExisted();
        }

        this.save(address);
        return address;
    }

    @Override
    public DefaultAddress
    updateRecordById(DefaultAddress address,Long id){
        DefaultAddress storedRecord = selectById(id);
        if(AsDefaultEnum.YES == address.getAsDefault() && AsDefaultEnum.NO == storedRecord.getAsDefault()){
            checkDefaultExisted();
        }
        DefaultAddress updateRecord = new DefaultAddress();
        updateRecord.setId(storedRecord.getId());
        if(null != address.getContent()){
            updateRecord.setContent(address.getContent());
        }
        if(address.getAsDefault() != storedRecord.getAsDefault()){
            updateRecord.setAsDefault(address.getAsDefault());
        }
        updateById(updateRecord);

        return getById(id);
    }

    @Override
    public DefaultAddress
    selectById(Long id){
        DefaultAddress record;
        try{
            record = getById(id);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new MyException(MyErrorEnum.COMMON_DB_SELECT_ERROR);
        }

        if(null == record){
            throw new MyException(MyErrorEnum.COMMON_DB_GET_RECORD_RESULT_NULL);
        }

        return record;
    }

    @Override
    public List<DefaultAddress> selectAll(){
        QueryWrapper<DefaultAddress> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(DefaultAddress.CREATE_TIME);
        wrapper.isNotNull(DefaultAddress.ID);

        try {
            return list(wrapper);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ArrayList<>();
        }
    }

    @Override
    public DefaultAddress selectDefault() {
        QueryWrapper<DefaultAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(DefaultAddress.AS_DEFAULT, AsDefaultEnum.YES);
        List<DefaultAddress> defaultAddressList;
        try {
            defaultAddressList = list(wrapper);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new MyException(MyErrorEnum.COMMON_DB_GET_RECORD_RESULT_NULL);
        }
        if(null != defaultAddressList && 0 < defaultAddressList.size()){
            return defaultAddressList.get(0);
        }else {
            throw new MyException(MyErrorEnum.COMMON_DB_GET_RECORD_RESULT_NULL);
        }
    }

    private void
    checkDefaultExisted(){
        QueryWrapper<DefaultAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(DefaultAddress.AS_DEFAULT, AsDefaultEnum.YES);
        List<DefaultAddress> defaultAddressList;
        try {
            defaultAddressList = list(wrapper);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new MyException(MyErrorEnum.MYSQL_ERROR);
        }
        if(null != defaultAddressList && 0 < defaultAddressList.size()){
            throw new MyException(MyErrorEnum.DEFAULT_ADDRESS_EXISTED);
        }
    }

}
