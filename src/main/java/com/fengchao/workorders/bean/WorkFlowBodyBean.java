package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="工单流程内容信息")
public class WorkFlowBodyBean {
    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="上一步操作者", example="1",required=true)
    private Long sender;

    @ApiModelProperty(value="当前流程操作者", example="1",required=true)
    private Long receiver;

    @ApiModelProperty(value="流程处理意见", example="移交",required=true)
    private String comments;

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
}
