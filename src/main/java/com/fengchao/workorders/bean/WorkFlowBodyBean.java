package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="工单流程内容信息")
public class WorkFlowBodyBean {
    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="工单状态码", example="1",required=false)
    private Integer status;

    @ApiModelProperty(value="流程处理意见", example="移交",required=true)
    private String comments;

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
