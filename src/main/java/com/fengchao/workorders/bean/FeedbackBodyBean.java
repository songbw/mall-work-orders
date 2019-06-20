package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="反馈信息体")
public class FeedbackBodyBean {
    @ApiModelProperty(value="反馈客户名", example="tom",required=true)
    private String customer;

    @ApiModelProperty(value="描述", example="反馈",required=true)
    private String title;

    @ApiModelProperty(value="反馈", example="good",required=true)
    private String feedbackText;

    @ApiModelProperty(value="工单ID", example="111",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="反馈时间", example="2019-06-06 11:11:11",required=true)
    private String feedbackTime;

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

    public void setFeedbackTime(String feedbackTime) {
        this.feedbackTime = feedbackTime;
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

    public String getFeedbackTime() {
        return feedbackTime;
    }
}
