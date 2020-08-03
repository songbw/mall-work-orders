package com.fengchao.workorders.dao.impl;

import com.fengchao.workorders.dao.WorkOrderDao;
import com.fengchao.workorders.mapper.WorkFlowMapper;
import com.fengchao.workorders.mapper.WorkOrderMapper;
import com.fengchao.workorders.mapper.WorkOrderXMapper;
import com.fengchao.workorders.model.WorkFlow;
import com.fengchao.workorders.model.WorkFlowExample;
import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.model.WorkOrderExample;
import com.fengchao.workorders.util.PageInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class WorkOrderDaoImpl implements WorkOrderDao {

    private WorkOrderMapper mapper;

    private WorkOrderXMapper workOrderXMapper;

    private WorkFlowMapper workFlowMapper ;

    @Autowired
    public WorkOrderDaoImpl(WorkOrderMapper mapper, WorkOrderXMapper workOrderXMapper, WorkFlowMapper workFlowMapper) {
        this.mapper = mapper;
        this.workOrderXMapper = workOrderXMapper;
        this.workFlowMapper = workFlowMapper ;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(WorkOrder record) {
        return mapper.insertSelective(record);
    }

    @Override
    public int insertSelective(WorkOrder record) {
        return mapper.insertSelective(record);
    }

    @Override
    public WorkOrder selectByPrimaryKey(Long id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(WorkOrder record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(WorkOrder record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<WorkOrder> selectByOrderId(String orderId) throws Exception{
        log.info("selectByOrderId: " + orderId);
        if (null == orderId) {
            return null;
        }
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        example.setOrderByClause("id DESC");
        List<WorkOrder> list = null;
        try {
            list = mapper.selectByExample(example);
        } catch (Exception ex) {
            log.error("workOrderMapper exception {}",ex.getMessage());
            throw new Exception(ex);
        }
        return list;
    }

    @Override
    public PageInfo<WorkOrder> selectRange(int pageIndex, int pageSize,String sort, String order,
                                           String title, String receiverId,
                                           String receiverPhone, String receiverName,
                                           String orderId, Long merchantId,
                                           Integer typeId, Integer status,
                                           Date createTimeStart, Date createTimeEnd
                                ) throws Exception{

        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        WorkOrderExample.Criteria orCriteria = example.createCriteria();
        if (null != title) {
            criteria.andTitleLike(title);
            orCriteria.andTitleLike(title);
        }
        if (null != receiverId) {
            criteria.andReceiverIdEqualTo(receiverId);
            orCriteria.andReceiverIdEqualTo(receiverId);
        }
        if (null != receiverPhone) {
            criteria.andReceiverPhoneEqualTo(receiverPhone);
            orCriteria.andReceiverPhoneEqualTo(receiverPhone);
        }
        if (null != receiverName) {
            criteria.andReceiverNameLike(receiverName);
            orCriteria.andReceiverNameLike(receiverName);
        }
        if (null != orderId) {
            criteria.andOrderIdEqualTo(orderId);
            orCriteria.andOrderIdEqualTo(orderId);
        }
        if (null != merchantId) {
            criteria.andMerchantIdEqualTo(merchantId);
            orCriteria.andMerchantIdEqualTo(merchantId);
        }
        if (null != typeId) {
            criteria.andTypeIdEqualTo(typeId);
            orCriteria.andTypeIdEqualTo(typeId);
        }
        if (null != status) {
            criteria.andStatusEqualTo(status);
            orCriteria.andStatusEqualTo(status);
        }

        if (null != createTimeStart && null != createTimeEnd) {
            criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);
            orCriteria.andCreateTimeEqualTo(createTimeEnd);
            example.or(orCriteria);
            example.or(criteria);
        }
        example.setOrderByClause(sort + " " + order);

        Page pages;
        List<WorkOrder> list;

        try {
            pages = PageHelper.startPage(pageIndex, pageSize, true);
            list = mapper.selectByExample(example);
        }catch (Exception e) {
            log.warn("work-order map selectByExample exception {}",e.getMessage());
            throw new Exception(e);
        }
        return new PageInfo<>((int)pages.getTotal(), pages.getPageSize(),pageIndex,list);
    }

    @Override
    public int countType(Integer typeId, Date createTimeStart, Date createTimeEnd) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        WorkOrderExample.Criteria orCriteria = example.createCriteria();

        if (null != typeId) {
            criteria.andTypeIdEqualTo(typeId);
            orCriteria.andTypeIdEqualTo(typeId);
        }
        if (null != createTimeStart && null != createTimeEnd) {
            criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);
            orCriteria.andCreateTimeEqualTo(createTimeEnd);
            example.or(criteria);
            example.or(orCriteria);
        }

        return (int)mapper.countByExample(example);

    }

    @Override
    public WorkOrder selectByRefundNo(String refundNo) {
        log.info("selectByRefundNo: " + refundNo);
        if (null == refundNo) {
            return null;
        }
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andRefundNoEqualTo(refundNo);
        List<WorkOrder> list = null;
        try {
            list = mapper.selectByExample(example);
        } catch (Exception ex) {
            log.error("数据库操作异常: {}", ex.getMessage() ,ex);
        }
        if (null == list || 0 == list.size()) {
            log.error("没有找到 refund_no = {} 的记录",refundNo);
        } else {
            return list.get(0);
        }

        return null;
    }

    @Override
    public Integer selectRefundUserCountByMerchantId(Long merchantId) throws Exception{
        try {
            return workOrderXMapper.selectRefundUserCountByMerchantId(merchantId);
        }catch (Exception e) {
            log.warn("workOrderXMapper.selectRefundUserCountByMerchantId exception  {}",e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public List<WorkOrder> selectRefundSuccessOrderDetailIdList(Long merchantId, Date startTime, Date endTime) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andRefundTimeBetween(startTime, endTime);
        if (merchantId != null) {
            criteria.andMerchantIdEqualTo(merchantId);
        }

        List<WorkOrder> workOrderList = mapper.selectByExample(example);
        return workOrderList;
    }

    @Override
    public List<WorkFlow> selectWorkFlowByWorkOrderId(Long workOrderId) {
        WorkFlowExample example = new WorkFlowExample();
        WorkFlowExample.Criteria criteria = example.createCriteria();
        criteria.andWorkOrderIdEqualTo(workOrderId);
        List<WorkFlow> list = workFlowMapper.selectByExample(example);
        return list;
    }
}
