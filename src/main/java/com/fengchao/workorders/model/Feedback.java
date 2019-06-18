package com.fengchao.workorders.model;

import java.util.Date;

public class Feedback {
    private Long id;

    private String title;

    private Long workOrderId;

    private String customer;

    private String feedbackText;

    private Date feedbackTime;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public void setFeedbackTime(Date feedbackTime) {
        this.feedbackTime = feedbackTime;
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

    public String getTitle() {
        return title;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public String getCustomer() {
        return customer;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public Date getFeedbackTime() {
        return feedbackTime;
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
