package com.fengchao.workorders.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderType {
    private Long id;

    private String name;

    private String workflowUrl;

    private String workflowText;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

}
