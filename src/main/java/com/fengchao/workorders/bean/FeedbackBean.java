package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
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

}
