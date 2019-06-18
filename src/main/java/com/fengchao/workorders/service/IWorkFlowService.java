package com.fengchao.workorders.service;

import com.fengchao.workorders.model.WorkFlow;
import com.fengchao.workorders.util.PageInfo;

//import java.io.Serializable;
import java.util.Date;
import java.util.List;
//import java.util.Map;

public interface IWorkFlowService {

    Long insert(WorkFlow workFlow);

    void deleteById(long id);

    WorkFlow selectById(long id);

    void update(WorkFlow workFlow);

    List<WorkFlow> selectAll();

    PageInfo<WorkFlow> selectPage(int pageIndex, int pageSize, String sort, String order,
                                  Long workOrderId, Long sender, Long receiver,
                                  Date createTimeStart, Date createTimeEnd);

    List<WorkFlow> selectByWorkOrderId(Long workOrderId);


    List<WorkFlow> selectList(Long sender, Long receiver);
}
