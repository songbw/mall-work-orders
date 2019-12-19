package com.fengchao.workorders.dao.impl;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.dao.WorkOrderDao;
import com.fengchao.workorders.mapper.WorkOrderMapper;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class WorkOrderDaoImpl implements WorkOrderDao {

    private WorkOrderMapper mapper;



    @Autowired
    public WorkOrderDaoImpl(WorkOrderMapper mapper) {
        this.mapper = mapper;
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
        log.info("selectByOrderId param : {} ",orderId);
        if (null == orderId) {
            return null;
        }
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        example.setOrderByClause("id DESC");
        List<WorkOrder> list;
        try {
            list = mapper.selectByExample(example);
        } catch (Exception ex) {
            log.error("workOrderMapper exception {}",ex.getMessage());
            throw new Exception(ex);
        }
        log.info("selectByOrderId exit: {}", JSON.toJSONString(list));
        return list;
    }

    @Override
    public PageInfo<WorkOrder> selectRange(int pageIndex, int pageSize,String sort, String order,
                                           String iAppId,
                                           String title, String receiverId,
                                           String receiverPhone, String receiverName,
                                           String orderId, Long merchantId,
                                           Integer typeId, Integer status,
                                           Date createTimeStart, Date createTimeEnd,
                                           Date refundTimeBegin, Date refundTimeEnd
                                ) throws Exception{

        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();

        if (null != iAppId && !iAppId.isEmpty()){
            criteria.andIAppIdEqualTo(iAppId);
        }

        if (null != title) {
            criteria.andTitleLike(title);
        }
        if (null != receiverId) {
            criteria.andReceiverIdEqualTo(receiverId);
        }
        if (null != receiverPhone) {
            criteria.andReceiverPhoneEqualTo(receiverPhone);
        }
        if (null != receiverName) {
            criteria.andReceiverNameLike(receiverName);
        }
        if (null != orderId) {
            criteria.andOrderIdEqualTo(orderId);
        }
        if (null != merchantId) {
            criteria.andMerchantIdEqualTo(merchantId);
        }
        if (null != typeId) {
            criteria.andTypeIdEqualTo(typeId);
        }
        if (null != status) {
            criteria.andStatusEqualTo(status);
        }

        if (null != refundTimeBegin){
            criteria.andRefundTimeGreaterThanOrEqualTo(refundTimeBegin);
        }
        if (null != refundTimeEnd){
            criteria.andRefundTimeLessThanOrEqualTo(refundTimeEnd);
        }
        if (null != createTimeStart){
            criteria.andCreateTimeGreaterThanOrEqualTo(createTimeStart);
        }
        if (null != createTimeEnd){
            criteria.andCreateTimeLessThanOrEqualTo(createTimeEnd);
        }
        /*
        if (null != createTimeStart && null != createTimeEnd) {
            criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);
            orCriteria.andCreateTimeEqualTo(createTimeEnd);
            example.or(orCriteria);
            example.or(criteria);
        }*/
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
        log.info("selectByOuterRefundNo: " + refundNo);
        if (null == refundNo) {
            return null;
        }
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andGuanaitongTradeNoEqualTo(refundNo);

        try {
            List<WorkOrder> list = mapper.selectByExample(example);
            if (null == list) {
                log.warn(" Not found record by example");
            } else {
                return list.get(0);
            }
            return null;
        } catch (Exception ex) {
            if (null != ex.getMessage()) {
                log.warn(" sql failed: {}", ex.getMessage() );
            } else {
                log.warn(" sql failed");
            }
        }
        return null;
    }

    @Override
    public Integer selectRefundUserCountByMerchantId(Long merchantId) throws Exception{
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMerchantIdEqualTo(merchantId);
        List<WorkOrder> list;
        try {
            list = mapper.selectByExample(example);
        }catch (Exception e) {
            log.warn("workOrderXMapper.selectRefundUserCountByMerchantId exception  {}",e.getMessage());
            throw new Exception(e);
        }

        if (null == list || 0 == list.size()){
            return 0;
        }

        Map<String, List<WorkOrder>> groupMap = list.stream().collect(Collectors.groupingBy(WorkOrder::getReceiverId));
        int total = 0;
        for(Map.Entry<String,List<WorkOrder>> m: groupMap.entrySet()){
            if (!m.getKey().isEmpty()) {
                total++;
            }
        }
        return total;
    }

    @Override
    public List<WorkOrder> selectRefundSuccessOrderDetailIdList(Long merchantId, Date startTime, Date endTime) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andRefundTimeBetween(startTime, endTime);
        if (merchantId != null) {
            criteria.andMerchantIdEqualTo(merchantId);
        }

        List<WorkOrder> workOrderList = null;
        try {
            workOrderList = mapper.selectByExample(example);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return workOrderList;
    }
}
