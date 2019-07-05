package com.fengchao.workorders.service.impl;

import com.fengchao.workorders.mapper.FeedbackMapper;
import com.fengchao.workorders.model.Feedback;
import com.fengchao.workorders.util.PageInfo;
import com.fengchao.workorders.service.IFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service
public class FeedbackServiceImpl implements IFeedbackService {

    private FeedbackMapper feedbackMapper;

    @Autowired
    public FeedbackServiceImpl(
            FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public Long insert(Feedback Feedback) {

        int rst = feedbackMapper.insert(Feedback);
        if (0 >= rst) {
            return 0L;
        }
        return Feedback.getId();
    }

    @Override
    public Feedback selectById(Long id) {
        return feedbackMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Feedback Feedback) {
        feedbackMapper.updateByPrimaryKey(Feedback);
    }

    @Override
    public PageInfo<Feedback> selectPage(int pageIndex, int pageSize, String sort, String order,
                                         Long workOrderId, String customer, String title,
                                         String feedbackText,
                                         Date dateTimeStart, Date dateTimeEnd) {

        int counts = feedbackMapper.selectRange(sort, order, workOrderId,customer, title, feedbackText, dateTimeStart, dateTimeEnd).size();

        PageHelper.startPage(pageIndex, pageSize);
        List<Feedback> list = feedbackMapper.selectRange(sort, order, workOrderId,customer, title, feedbackText, dateTimeStart, dateTimeEnd);

        return new PageInfo<>(counts, pageSize, pageIndex,list);
    }

    @Override
    public boolean deleteById(Long id) {
        feedbackMapper.deleteByPrimaryKey(id);
        log.info("delete feedback by id = " + id);
        Feedback feedback = feedbackMapper.selectByPrimaryKey(id);
        return (null == feedback);

    }

    @Override
    public List<Feedback> selectByWorkOrderId(Long workOrderId) {
        return feedbackMapper.selectByWorkOrderId(workOrderId);
    }
}
