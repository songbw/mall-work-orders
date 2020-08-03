package com.fengchao.workorders.service;

import com.fengchao.workorders.model.WorkFlow;
import com.fengchao.workorders.util.PageInfo;

import java.util.Date;
import java.util.List;
//import java.util.Map;

public interface IWorkFlowService {

    Long insert(WorkFlow workFlow) throws Exception;

    void deleteById(long id) throws Exception;

    WorkFlow selectById(long id) throws Exception;

    void update(WorkFlow workFlow) throws Exception;


    PageInfo<WorkFlow> selectPage(int pageIndex, int pageSize, String sort, String order,
                                  Long workOrderId, Date createTimeStart, Date createTimeEnd) throws Exception;

    List<WorkFlow> selectByWorkOrderId(Long workOrderId, Integer status) throws Exception;

}
