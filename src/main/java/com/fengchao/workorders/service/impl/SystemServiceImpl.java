package com.fengchao.workorders.service.impl;

import com.github.pagehelper.PageHelper;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.service.ISystemService;
import com.fengchao.workorders.util.PageInfo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SystemServiceImpl implements ISystemService {

    //private static final Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);

    private SysLogMapper sysLogMapper;

    @Autowired
    public SystemServiceImpl(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    @Override
    public PageInfo<SysLog> selectLog(int page, int rows, String sort, String order, String method, String url, String param, Date createTimeStart, Date createTimeEnd, String user) {
        int counts = sysLogMapper.selectLog(sort, order, method, url, param, createTimeStart, createTimeEnd, user).size();

        PageHelper.startPage(page, rows);
        List<SysLog> list = sysLogMapper.selectLog(sort, order, method, url, param, createTimeStart, createTimeEnd, user);

        int pageSize = list==null?0:list.size();
        return new PageInfo<>(counts, pageSize, page,list);
    }

    @Override
    public void insertSysControllerLog(SysLog runningLog) {

        sysLogMapper.insert(runningLog);
    }

    @Override
    public void deleteLogById(Long id) {
        sysLogMapper.deleteById(id);
    }

    @Override
    public void deleteLogByIdList(List<Long> ids) {
            sysLogMapper.deleteByIdList(ids);
    }

    @Override
    public SysLog selectLogById(Long id) {
        return sysLogMapper.selectById(id);
    }

}
