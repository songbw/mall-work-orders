package com.fengchao.workorders.service.impl;

import com.fengchao.workorders.model.*;
import com.fengchao.workorders.mapper.*;
import com.fengchao.workorders.service.IAttachmentService;
import com.fengchao.workorders.util.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
public class AttachmentServiceImpl implements IAttachmentService {

    private AttachmentMapper attachmentMapper;

    @Autowired
    public AttachmentServiceImpl(AttachmentMapper attachmentMapper
                            ) {
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public Attachment selectById(Long id) {

        return attachmentMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean isExistName(String name) {
        List<Attachment> attachments = attachmentMapper.selectByName(name);

        return (null != attachments && 0 < attachments.size());
    }

    @Override
    public Long insertRecord(Attachment attachment)
    {
        int rst = attachmentMapper.insertSelective(attachment);
        if (0 >= rst) {
            return 0L;
        } else {
            return attachment.getId();
        }
    }

    @Override
    public void deleteById(Long id) {
        attachmentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Boolean isExistNameExcludeId(String name, Long id) {
        List<Attachment> attachments = attachmentMapper.selectByName(name);
        if (null == attachments) {
            return false;
        }
        if (0 == attachments.size()) {
            return false;
        }

        return (id.equals(attachments.get(0).getId()));
    }

    @Override
    public List<Attachment> selectAll() {
        return attachmentMapper.selectAll();
    }

    @Override
    public List<Attachment> selectListByOrderId(Long workOrderId) {
        return attachmentMapper.selectByWorkOrderId(workOrderId);
    }

    @Override
    public PageInfo<Attachment> selectAttachments(int pageIndex, int pageSize, String sort, String order,
                                                  Long workOrderId,
                                                  String name, String submitter, Date createTimeStart,
                                                  Date createTimeEnd) {
        int counts = attachmentMapper.selectRange(sort, order, workOrderId,name, submitter, createTimeStart, createTimeEnd).size();

        PageHelper.startPage(pageIndex, pageSize);
        List<Attachment> list = attachmentMapper.selectRange(sort, order, workOrderId,name, submitter, createTimeStart, createTimeEnd);

        return new PageInfo<>(counts, pageSize, pageIndex,list);
    }

    @Override
    public void updateSelectById(Attachment attachment) {
        attachmentMapper.updateByPrimaryKey(attachment);
    }
}
