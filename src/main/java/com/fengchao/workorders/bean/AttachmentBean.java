package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value="附件信息")
public class AttachmentBean implements Serializable {
    @ApiModelProperty(value="附件ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="附件名称", example="商品图",required=true)
    private String name;

    @ApiModelProperty(value="附件链接", example="http://cdn.qos.com/company_business_license.jpg",required=true)
    private String path;

    @ApiModelProperty(value="附件提交者", example="张赞",required=true)
    private String submitter;

    @ApiModelProperty(value="附件提交时间", example="2019-06-16 11:11:11",required=false)
    private Date createTime;

    @ApiModelProperty(value="附件更新时间", example="2019-06-16 11:11:11",required=false)
    private Date updateTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
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

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSubmitter() {
        return submitter;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
