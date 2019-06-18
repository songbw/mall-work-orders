package com.fengchao.workorders.service;

import com.fengchao.workorders.model.*;
import com.fengchao.workorders.util.PageInfo;

import java.util.Date;
import java.util.List;

public interface IAttachmentService {
    Attachment selectById(Long id);

    Boolean isExistName(String name);

    Long insertRecord(Attachment attachment);

    Boolean isExistNameExcludeId(String name, Long id);

    void updateSelectById(Attachment attachment);

    void deleteById(Long id);

    List<Attachment> selectAll();

    List<Attachment> selectListByOrderId(Long workOrderId);

    PageInfo<Attachment> selectAttachments(int pageIndex, int pageSize, String sort, String order,
                                           Long workOrderId,
                                           String name,
                                           String submitter,
                                           Date createTimeStart,
                                           Date createTimeEnd);
}
