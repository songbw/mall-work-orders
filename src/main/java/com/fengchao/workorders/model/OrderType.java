package com.fengchao.workorders.model;

import java.util.Date;

public class OrderType {
    private Long id;

    private String name;

    private String workflowUrl;

    private String workflowText;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
    }

    public void setWorkflowText(String workflowText) {
        this.workflowText = workflowText;
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

    public String getName() {
        return name;
    }

    public String getWorkflowUrl() {
        return workflowUrl;
    }

    public String getWorkflowText() {
        return workflowText;
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
