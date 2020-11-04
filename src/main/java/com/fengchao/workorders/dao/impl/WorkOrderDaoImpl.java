package com.fengchao.workorders.dao.impl;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.dao.WorkOrderDao;
import com.fengchao.workorders.mapper.WorkOrderMapper;
import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.model.WorkOrderExample;
import com.fengchao.workorders.util.PageInfo;
import com.fengchao.workorders.util.StringUtil;
import com.fengchao.workorders.util.WorkOrderStatusType;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    public List<WorkOrder> selectValidByOrderId(String orderId) throws Exception{
        log.info("selectByOrderId param : {} ",orderId);
        if (null == orderId) {
            return null;
        }
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        criteria.andStatusNotEqualTo(WorkOrderStatusType.REJECT.getCode());
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
    public List<WorkOrder> selectByOrderIdList(List<String> orderIdList){
        log.info("selectByOrderIdList param : {} ",JSON.toJSONString(orderIdList));

        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdIn(orderIdList);
        example.setOrderByClause("id DESC");
        List<WorkOrder> list;
        try {
            list = mapper.selectByExample(example);
        } catch (Exception ex) {
            log.error("workOrderMapper exception {}",ex.getMessage(),ex);
            throw ex;
        }
        log.info("selectByOrderIdList exit: {}", JSON.toJSONString(list));
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
                                           Date refundTimeBegin, Date refundTimeEnd,
                                           List<String> appIds
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
        if (appIds != null && appIds.size() > 0) {
            criteria.andIAppIdIn(appIds) ;
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
            log.error("work-order map selectByExample exception {}",e.getMessage(),e);
            throw new Exception(e);
        }
        return new PageInfo<>((int)pages.getTotal(), pages.getPageSize(),pageIndex,list);
    }

    @Override
    public int countType(Integer typeId, Date createTimeStart, Date createTimeEnd, List<String> appIds) {
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
        if (appIds != null && appIds.size() > 0) {
            criteria.andIAppIdIn(appIds);
            orCriteria.andIAppIdIn(appIds);
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
        criteria.andRefundNoEqualTo(refundNo);

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
    public WorkOrder selectByOutRefundNo(String outRefundNo) {
        log.info("selectByOuterRefundNo: " + outRefundNo);
        if (null == outRefundNo) {
            return null;
        }
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andGuanaitongTradeNoEqualTo(outRefundNo);

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
    public List<WorkOrder> selectRefundSuccessOrderDetailIdList(String iAppId,Long merchantId, Date startTime, Date endTime) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andRefundTimeBetween(startTime, endTime);
        if (merchantId != null) {
            criteria.andMerchantIdEqualTo(merchantId);
        }
        if (null != iAppId){
            criteria.andIAppIdEqualTo(iAppId);
        }

        List<WorkOrder> workOrderList = null;
        try {
            workOrderList = mapper.selectByExample(example);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return workOrderList;
    }

    @Override
    public PageInfo<WorkOrder> selectAbnormalRefund(int pageIndex, int pageSize,String sort, String order,
                                           String iAppId,
                                           String orderId, Long merchantId,
                                           Date createTimeStart, Date createTimeEnd
                                ) throws Exception{

        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();

        List<Integer> statusList = new ArrayList<>();
        statusList.add(WorkOrderStatusType.CLOSED.getCode());
        criteria.andStatusIn(statusList);

        criteria.andRefundTimeEqualTo(StringUtil.String2Date("1970-01-01 00:00:00"));

        if (null != iAppId && !iAppId.isEmpty()){
            criteria.andIAppIdEqualTo(iAppId);
        }

        if (null != orderId) {
            criteria.andOrderIdEqualTo(orderId);
        }
        if (null != merchantId) {
            criteria.andMerchantIdEqualTo(merchantId);
        }

        if (null != createTimeStart){
            criteria.andCreateTimeGreaterThanOrEqualTo(createTimeStart);
        }
        if (null != createTimeEnd){
            criteria.andCreateTimeLessThanOrEqualTo(createTimeEnd);
        }

        example.setOrderByClause(sort + " " + order);

        Page pages;
        List<WorkOrder> list;

        try {
            pages = PageHelper.startPage(pageIndex, pageSize, true);
            list = mapper.selectByExample(example);
        }catch (Exception e) {
            log.error("work-order map selectByExample exception {}",e.getMessage(),e);
            throw e;
        }
        return new PageInfo<>((int)pages.getTotal(), pages.getPageSize(),pageIndex,list);
    }

}
