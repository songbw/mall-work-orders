package com.fengchao.workorders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.entity.WorkFlow;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.service.IWorkFlowService;
import com.fengchao.workorders.util.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 工作流
 * @author Clark
 * */

@Slf4j
@Service
public class WorkFlowServiceImpl extends ServiceImpl<WorkFlowMapper, WorkFlow> implements IWorkFlowService {

    private WorkFlowMapper workFlowMapper;

    @Autowired
    public WorkFlowServiceImpl(WorkFlowMapper workFlowMapper
                              ) {
        this.workFlowMapper = workFlowMapper;
    }

    @Override
    public PageInfo<WorkFlow>
    selectPage(int pageIndex, int pageSize,
               Long workOrderId,
               String createTimeStart,
               String createTimeEnd){

        QueryWrapper<WorkFlow> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(WorkFlow.CREATE_TIME);

        if (null != createTimeStart){
            queryWrapper.ge(WorkFlow.CREATE_TIME,createTimeStart);
        }
        if (null != createTimeEnd){
            queryWrapper.le(WorkFlow.CREATE_TIME,createTimeEnd);
        }
        if(null != workOrderId){
            queryWrapper.eq(WorkFlow.WORK_ORDER_ID,workOrderId);
        }

        IPage<WorkFlow> pages = page(new Page<>(pageIndex, pageSize), queryWrapper);
        return new PageInfo<>((int)pages.getTotal(), pageSize, pageIndex, pages.getRecords());

    }

    @Override
    public List<WorkFlow>
    selectByWorkOrderId(Long workOrderId, Integer status) throws Exception{
        if (null == workOrderId) {
            throw new Exception("selectByWorkOrderId, workOrderId is null");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderByDesc(WorkFlow.CREATE_TIME);

        wrapper.eq(WorkFlow.WORK_ORDER_ID,workOrderId);
        if (null != status){
            wrapper.eq(WorkFlow.STATUS,status);
        }

        return list(wrapper);

    }

    @Override
    public List<WorkFlow>
    selectByWorkOrderIdExcludeReserved(Long workOrderId) {
        if (null == workOrderId) {
            throw new RuntimeException("selectByWorkOrderIdExcludeReserved, workOrderId is null");
        }

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderByDesc(WorkFlow.CREATE_TIME);

        wrapper.eq(WorkFlow.WORK_ORDER_ID,workOrderId);
        wrapper.ne(WorkFlow.STATUS,WorkOrderStatusType.RESERVED.getCode());

        return list(wrapper);
    }
}
