package com.fengchao.workorders.service.impl;

import com.github.pagehelper.PageHelper;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.service.IWorkFlowService;
import com.fengchao.workorders.util.PageInfo;
//import org.joda.time.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;

//import java.io.IOException;
//import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Slf4j
@Service(value="WorkFlowServiceImpl")
@Transactional
public class WorkFlowServiceImpl implements IWorkFlowService {

    private WorkFlowMapper workFlowMapper;
    private WorkOrderMapper workOrderMapper;

    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkFlowServiceImpl(WorkFlowMapper workFlowMapper,WorkOrderMapper workOrderMapper
                              ) {
        this.workFlowMapper = workFlowMapper;
        this.workOrderMapper = workOrderMapper;
    }

    @Override
    public Long insert(WorkFlow workFlow) {
        int rst = workFlowMapper.insertSelective(workFlow);
        if (0 < rst) {
            WorkOrder workOrder = workOrderMapper.selectByPrimaryKey(1L);
            if (null != workOrder && null != workOrder.getStatus()) {
                if (!workFlow.getStatus().equals(workOrder.getStatus())) {
                    workOrder.setStatus(workFlow.getStatus());
                    workOrderMapper.updateByPrimaryKey(workOrder);
                }
            }

            return workFlow.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public void deleteById(long id) {
        workFlowMapper.deleteByPrimaryKey(id);
    }

    @Override
    public WorkFlow selectById(long id) {
        return workFlowMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(WorkFlow workFlow) {
        if (null == workFlow) {
            return;
        }
        workFlowMapper.updateByPrimaryKeySelective(workFlow);
        //System.out.println("updated workFlow for: " + workFlow.getId());
        log.info("updated user for: " + workFlow.getId());
    }

    @Override
    public List<WorkFlow> selectAll() {

        return workFlowMapper.selectRange("id", "DESC",  null, null, null);
    }

    @Override
    public PageInfo<WorkFlow> selectPage(int pageIndex, int pageSize, String sort, String order,
                                         Long workOrderId, Date createTimeStart, Date createTimeEnd) {

        int counts = workFlowMapper.selectRange(sort, order, workOrderId, createTimeStart, createTimeEnd).size();

        PageHelper.startPage(pageIndex, pageSize);
        List<WorkFlow> workFlows = workFlowMapper.selectRange(sort, order, workOrderId, createTimeStart, createTimeEnd);

        return new PageInfo<>(counts, pageSize, pageIndex,workFlows);
    }

    @Override
    public List<WorkFlow> selectByWorkOrderId(Long workOrderId) {
        return workFlowMapper.selectByWorkOrderId(workOrderId);
    }

}
