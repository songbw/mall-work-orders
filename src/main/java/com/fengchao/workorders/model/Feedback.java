package com.fengchao.workorders.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Feedback {
    private Long id;

    private String title;

    private Long workOrderId;

    private String customer;

    private String feedbackText;

    private Date feedbackTime;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;


}
