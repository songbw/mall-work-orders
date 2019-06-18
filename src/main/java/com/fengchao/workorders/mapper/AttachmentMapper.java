package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component(value = "AttachmentMapper")
public interface AttachmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Attachment record);

    int insertSelective(Attachment record);

    Attachment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Attachment record);

    int updateByPrimaryKey(Attachment record);

    Attachment selectByName(String name);

    List<Attachment> selectAll();

    List<Attachment> selectByWorkOrderId(@Param("workOrderId")Long workOrderId);

    List<Attachment> selectRange(@Param("sort") String sort, @Param("order") String order,
                                 @Param("workOrderId")Long workOrderId,
                                 @Param("name") String name,
                                 @Param("submitter") String submitter,
                                 @Param("createTimeStart") Date createTimeStart,
                                 @Param("createTimeEnd") Date createTimeEnd
                                 );

}
