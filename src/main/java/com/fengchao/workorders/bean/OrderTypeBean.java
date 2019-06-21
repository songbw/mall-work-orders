package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="工单类型信息")
public class OrderTypeBean {

    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="工单流程名称", example="退货退款",required=true)
    private String name;

    @ApiModelProperty(value="工单流程图链接", example="http://example.cn/feedback001.png",required=false)
    private String workflowUrl;

    @ApiModelProperty(value="工单流程文字说明", example="反馈",required=true)
    private String workflowText;

    @ApiModelProperty(value="创建时间", example="2019-06-06 10:10:10",required=false)
    private Date createTime;

    @ApiModelProperty(value="更新时间", example="2019-06-06 20:10:10",required=false)
    private Date updateTime;

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
}
