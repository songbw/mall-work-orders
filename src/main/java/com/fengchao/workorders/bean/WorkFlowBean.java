package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="工单流程内容信息")
public class WorkFlowBean {

    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="上一步操作者", example="tim",required=true)
    private Long sender;

    @ApiModelProperty(value="当前流程操作者", example="tim",required=true)
    private Long receiver;

    @ApiModelProperty(value="流程处理意见", example="移交",required=true)
    private String comments;

    @ApiModelProperty(value="提交时间", example="2019-06-16 11:11:11",required=false)
    private Date createTime;

    @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11",required=false)
    private Date updateTime;

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
}
