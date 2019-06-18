package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component(value = "FeedbackMapper")
public interface FeedbackMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Feedback record);

    int insertSelective(Feedback record);

    Feedback selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Feedback record);

    int updateByPrimaryKey(Feedback record);

    List<Feedback> selectByWorkOrderId(@Param("workOrderId")Long workOrderId);

    List<Feedback> selectRange(@Param("sort") String sort, @Param("order") String order,
                               @Param("workOrderId") Long workOrderId,
                               @Param("customer") String customer,
                               @Param("title") String title,
                               @Param("feedbackText") String feedbackText,
                               @Param("createTimeStart") Date createTimeStart,
                               @Param("createTimeEnd") Date createTimeEnd);
}
