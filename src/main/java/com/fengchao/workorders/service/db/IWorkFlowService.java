package com.fengchao.workorders.service.db;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fengchao.workorders.entity.WorkFlow;
import com.fengchao.workorders.util.PageInfo;

import java.util.List;

/**
 * 工作流数据处理接口
 * @author clark
 * */
public interface IWorkFlowService extends IService<WorkFlow> {

    PageInfo<WorkFlow>
    selectPage(int pageIndex, int pageSize,
               Long workOrderId, String createTimeStart, String createTimeEnd);

    List<WorkFlow>
    selectByWorkOrderId(Long workOrderId, Integer status);

    List<WorkFlow>
    selectByWorkOrderIdExcludeReserved(Long workOrderId);


}
