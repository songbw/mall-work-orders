package com.fengchao.workorders.dao.impl;

import com.fengchao.workorders.dao.WorkOrderDao;
import com.fengchao.workorders.mapper.WorkOrderMapper;
import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.model.WorkOrderExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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
        return mapper.insert(record);
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
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<WorkOrder> selectByOrderId(String orderId) {
        log.info("selectByOrderId: " + orderId);
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        example.setOrderByClause("id DESC");

        try {
            List<WorkOrder> list = mapper.selectByExample(example);
            if (null == list) {
                log.warn(" Not found record by example");
            }
            return list;
        } catch (Exception ex) {
            if (null != ex || null != ex.getMessage()) {
                log.warn(" sql failed: {}", ex.getMessage() );
            } else {
                log.warn(" sql failed");
            }
        }
        return null;
    }

    @Override
    public List<WorkOrder> selectRange(String sort, String order,
                                String title, String receiverId,
                                String receiverPhone, String receiverName,
                                String orderId, Long merchantId,
                                Long typeId, Integer status,
                                Date finishTimeStart, Date finishTimeEnd,
                                Date createTimeStart, Date createTimeEnd
                                ) {

        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();


        criteria.andTitleLike(title);
        criteria.andReceiverIdEqualTo(receiverId);
        criteria.andReceiverPhoneEqualTo(receiverPhone);
        criteria.andReceiverNameLike(receiverName);
        criteria.andOrderIdEqualTo(orderId);
        criteria.andMerchantIdEqualTo(merchantId);
        criteria.andTypeIdEqualTo(typeId);
        criteria.andStatusEqualTo(status);
        criteria.andFinishTimeBetween(finishTimeStart, finishTimeEnd);
        criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);

        example.setOrderByClause(sort + " " + order);
        return mapper.selectByExample(example);

    }

    @Override
    public int countType(Long typeId, Date createTimeStart, Date createTimeEnd) {
        WorkOrderExample example = new WorkOrderExample();
        WorkOrderExample.Criteria criteria = example.createCriteria();

        criteria.andTypeIdEqualTo(typeId);
        criteria.andCreateTimeBetween(createTimeStart, createTimeEnd);

        example.setOrderByClause("id DESC");
        return (int)mapper.countByExample(example);

    }
}
