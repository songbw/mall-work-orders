package com.fengchao.workorders.service.impl;

import com.github.pagehelper.PageHelper;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.service.IWorkFlowService;
import com.fengchao.workorders.util.PageInfo;
//import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;

//import java.io.IOException;
//import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(value="WorkFlowServiceImpl")
@Transactional
public class WorkFlowServiceImpl implements IWorkFlowService {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowServiceImpl.class);

    private WorkFlowMapper workFlowMapper;

    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkFlowServiceImpl(WorkFlowMapper workFlowMapper
                              ) {
        this.workFlowMapper = workFlowMapper;
    }

    @Override
    public Long insert(WorkFlow workFlow) {
        int rst = workFlowMapper.insert(workFlow);
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
        //System.out.println("updated workFlow for: " + workFlow.getId());
        logger.info("updated user for: " + workFlow.getId());
    }

    @Override
    public List<WorkFlow> selectAll() {

        return workFlowMapper.selectRange("id", "DESC", null,null, null, null, null);
    }

    @Override
    public PageInfo<WorkFlow> selectPage(int pageIndex, int pageSize, String sort, String order,
                                         Long workOrderId, Long sender, Long receiver,
                                         Date createTimeStart, Date createTimeEnd) {

        int counts = workFlowMapper.selectRange(sort, order, workOrderId,sender, receiver, createTimeStart, createTimeEnd).size();

        PageHelper.startPage(pageIndex, pageSize);
        List<WorkFlow> workFlows = workFlowMapper.selectRange(sort, order, workOrderId,sender, receiver, createTimeStart, createTimeEnd);

        return new PageInfo<>(counts, pageSize, pageIndex,workFlows);
    }

    @Override
    public List<WorkFlow> selectByWorkOrderId(Long workOrderId) {
        return workFlowMapper.selectByWorkOrderId(workOrderId);
    }

    @Override
    public List<WorkFlow> selectList(Long sender, Long receiver) {
        return workFlowMapper.selectRange("id", "ASC", null,sender, receiver, null, null);

    }
}
