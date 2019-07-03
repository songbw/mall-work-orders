package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="工单流程查询信息")
public class WorkFlowBean {

    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="工单状态码", example="1",required=false)
    private Integer status;

    @ApiModelProperty(value="流程处理意见", example="移交",required=true)
    private String comments;

    @ApiModelProperty(value="提交时间", example="2019-06-16 11:11:11",required=false)
    private Date createTime;

    @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11",required=false)
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
