package com.fengchao.workorders.model;

import java.util.Date;

public class WorkFlow {
    private Long id;

    private Long workOrderId;

    private Long sender;

    private Long receiver;

    private String comments;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

    public void setId(Long id) {
        this.id = id;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public Long getSender() {
        return sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public String getComments() {
        return comments;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
