package com.fengchao.workorders.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WorkOrder {
    private Long id;

    private Long merchantId;

    private String orderId;

    private String title;

    private String description;

    private String receiverId;

    private String receiverName;

    private Long typeId;

    private String receiverPhone;

    private String outcome;

    private Date finishTime;

    private Integer urgentDegree;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String createdBy;

    private String updatedBy;

}
