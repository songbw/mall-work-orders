package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value="审批信息")
public class StatusBean implements Serializable {
    @ApiModelProperty(value="Status", example="3",required=true)
    private Integer status;

    @ApiModelProperty(value="comments", example="good",required=true)
    private String comments;

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }
}
