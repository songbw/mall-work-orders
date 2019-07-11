package com.fengchao.workorders.service.impl;

import com.fengchao.workorders.util.WorkOrderType;
import com.github.pagehelper.PageHelper;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.service.IWorkOrderService;
import com.fengchao.workorders.util.PageInfo;
//import org.joda.time.DateTime;
//import org.springframework.beans.BeanUtils;
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
@Service(value="WorkOrderServiceImpl")
@Transactional
public class WorkOrderServiceImpl implements IWorkOrderService {

    private WorkOrderMapper workOrderMapper;

    // @Autowired
    // private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkOrderServiceImpl(WorkOrderMapper workOrderMapper
                              ) {
        this.workOrderMapper = workOrderMapper;
    }

    @Override
    public Long insert(WorkOrder workOrder) {
        int rst = workOrderMapper.insert(workOrder);
        if (0 < rst) {
            return workOrder.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public void deleteById(Long id) {
        workOrderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public WorkOrder selectById(Long id) {
        return workOrderMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(WorkOrder workOrder) {
        if (null == workOrder) {
            return;
        }
        workOrderMapper.updateByPrimaryKeySelective(workOrder);
        //System.out.println("updated workOrder for: " + workOrder.getId());
        log.info("updated user for: " + workOrder.getId());
    }

    @Override
    public PageInfo<WorkOrder> selectPage(int pageIndex, int pageSize, String sort, String order,
                                          String title, String receiverId, String receiverName, String receiverPhone,
                                          String orderId, Long typeId, Long merchantId,Integer status,
                                          Date finishTimeStart, Date finishTimeEnd,
                                         Date createTimeStart, Date createTimeEnd) {

        int counts = workOrderMapper.selectRange(sort, order,
                                                    title, receiverId, receiverPhone, receiverName,
                                                    orderId, merchantId,typeId,status,
                                                    finishTimeStart, finishTimeEnd,
                                                    createTimeStart, createTimeEnd).size();

        PageHelper.startPage(pageIndex, pageSize);
        List<WorkOrder> workOrders = workOrderMapper.selectRange(sort, order,
                title, receiverId, receiverPhone, receiverName,
                orderId, merchantId,typeId,status,
                finishTimeStart, finishTimeEnd,
                createTimeStart, createTimeEnd);

        return new PageInfo<>(counts, pageSize, pageIndex,workOrders);
    }

    @Override
    public List<WorkOrder> selectByOrderId(Long orderId) {
        return workOrderMapper.selectByOrderId(orderId);
    }

    @Override
    public int countReturn(Date createTimeStart, Date createTimeEnd) {
        long typeId;

        typeId = (long)WorkOrderType.RETURN.getCode();
        int c1 = workOrderMapper.countType(typeId,createTimeStart, createTimeEnd);

        typeId = (long)WorkOrderType.REFUND.getCode();
        int c2 = workOrderMapper.countType(typeId,createTimeStart, createTimeEnd);

        return c1 + c2;
    }
}
