package com.fengchao.workorders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fengchao.workorders.entity.WorkFlow;
import com.fengchao.workorders.util.PageInfo;

import java.util.Date;
import java.util.List;

public interface IWorkFlowService extends IService<WorkFlow> {


    PageInfo<WorkFlow>
    selectPage(int pageIndex, int pageSize,
               Long workOrderId, String createTimeStart, String createTimeEnd);

    List<WorkFlow> selectByWorkOrderId(Long workOrderId, Integer status) throws Exception;

    List<WorkFlow>
    selectByWorkOrderIdExcludeReserved(Long workOrderId);
}
