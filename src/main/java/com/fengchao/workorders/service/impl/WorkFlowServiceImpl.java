package com.fengchao.workorders.service.impl;

import com.fengchao.workorders.util.WorkOrderStatusType;
import com.github.pagehelper.Page;
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
    //private WorkOrderMapper workOrderMapper;

    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkFlowServiceImpl(WorkFlowMapper workFlowMapper
                              ) {
        this.workFlowMapper = workFlowMapper;
        //this.workOrderMapper = workOrderMapper;
    }

    @Override
    public Long insert(WorkFlow workFlow) {
        int rst = workFlowMapper.insertSelective(workFlow);
        if (0 < rst) {
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
        log.info("updated user for: " + workFlow.getId());
    }

    @Override
    public PageInfo<WorkFlow> selectPage(int pageIndex, int pageSize, String sort, String order,
                                         Long workOrderId, Date createTimeStart, Date createTimeEnd) throws Exception{

        WorkFlowExample example = new WorkFlowExample();
        WorkFlowExample.Criteria criteria = example.createCriteria();

        if (null != workOrderId) {
            criteria.andWorkOrderIdEqualTo(workOrderId);
        }
        if (null != createTimeStart){
            criteria.andCreateTimeGreaterThanOrEqualTo(createTimeStart);
        }
        if (null != createTimeEnd){
            criteria.andCreateTimeLessThanOrEqualTo(createTimeEnd);
        }
        if (null != sort && null != order){
            example.setOrderByClause(sort + " " + order);
        }

        Page pages;
        List<WorkFlow> list;

        try {
            pages = PageHelper.startPage(pageIndex, pageSize, true);
            list = workFlowMapper.selectByExample(example);
        }catch (Exception e) {
            log.error("workFlow selectByExample exception {}",e.getMessage());
            throw new Exception(e);
        }
        return new PageInfo<>((int)pages.getTotal(), pages.getPageSize(),pageIndex,list);
    }

    @Override
    public List<WorkFlow> selectByWorkOrderId(Long workOrderId, Integer status) throws Exception{
        if (null == workOrderId) {
            throw new Exception("selectByWorkOrderId, workOrderId is null");
        }
        WorkFlowExample example = new WorkFlowExample();
        WorkFlowExample.Criteria criteria = example.createCriteria();
        criteria.andWorkOrderIdEqualTo(workOrderId);
        if (null != status){
            criteria.andStatusEqualTo(status);
        }
        example.setOrderByClause("update_time DESC");
        try {
            return workFlowMapper.selectByExample(example);
        }catch (Exception e){
            log.error("selectByWorkOrderId error {}",e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public List<WorkFlow>
    selectByWorkOrderIdExcludeReserved(Long workOrderId) {
        if (null == workOrderId) {
            throw new RuntimeException("selectByWorkOrderIdExcludeReserved, workOrderId is null");
        }
        WorkFlowExample example = new WorkFlowExample();
        WorkFlowExample.Criteria criteria = example.createCriteria();
        criteria.andWorkOrderIdEqualTo(workOrderId);
        criteria.andStatusNotEqualTo(WorkOrderStatusType.RESERVED.getCode());

        example.setOrderByClause("update_time DESC");
        try {
            return workFlowMapper.selectByExample(example);
        }catch (Exception e){
            log.error("selectByWorkOrderIdExcludeReserved error {}",e.getMessage());
            return null;
        }
    }
}
