package com.fengchao.workorders.service;

import com.fengchao.workorders.util.PageInfo;
import com.fengchao.workorders.model.Feedback;

import java.util.Date;
import java.util.List;

public interface IFeedbackService {

    Long insert(Feedback feedback);

    Feedback selectById(Long id);

    void update(Feedback feedback);

    PageInfo<Feedback> selectPage(int page, int rows, String sort, String order,
                                  Long workOrderId, String customer, String title,
                                  String feedbackText,
                                  Date createTimeStart, Date createTimeEnd);

    List<Feedback> selectByWorkOrderId(Long workOrderId);

    boolean deleteById(Long id);

}
