package com.fengchao.workorders.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Attachment {
    private Long id;

    private String name;

    private Long workOrderId;

    private String submitter;

    private String path;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

}
