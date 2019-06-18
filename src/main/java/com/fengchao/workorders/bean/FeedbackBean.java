package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value="反馈信息")
public class FeedbackBean implements Serializable {

    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="反馈客户名", example="tom",required=true)
    private String customer;

    @ApiModelProperty(value="描述", example="反馈",required=true)
    private String title;

    @ApiModelProperty(value="反馈", example="good",required=true)
    private String feedbackText;

    @ApiModelProperty(value="工单ID", example="111",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="反馈时间", example="2019-06-06 11:11:11",required=true)
    private Date feedbackTime;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public void setFeedbackTime(Date feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public Long getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public String getTitle() {
        return title;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public Date getFeedbackTime() {
        return feedbackTime;
    }
}
