package com.fengchao.workorders.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WorkFlow {
    private Long id;

    private Long workOrderId;

    private Integer status;

    private String comments;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

}
