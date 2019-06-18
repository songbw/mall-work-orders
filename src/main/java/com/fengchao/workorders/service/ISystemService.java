package com.fengchao.workorders.service;

import com.fengchao.workorders.model.SysLog;
import com.fengchao.workorders.util.PageInfo;

import java.util.Date;
import java.util.List;

public interface ISystemService {

    PageInfo<SysLog> selectLog(int page, int rows, String s, String order, String method, String url, String param, Date createTimeStart, Date createTimeEnd, String user);

    void insertSysControllerLog(SysLog runningLog);

    void deleteLogById(Long id);

    void deleteLogByIdList(List<Long> ids);

    SysLog selectLogById(Long id);

}
