package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="工单更新信息")
public class WorkOrderBodyBean {
    @ApiModelProperty(value="所属订单ID", example="111",required=true)
    private String orderId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=true)
    private String description;

    @ApiModelProperty(value="客户", example="张三",required=true)
    private String customer;

    @ApiModelProperty(value="工单接待员", example="李四",required=true)
    private String receptionist;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Long typeId;

    @ApiModelProperty(value="工单流程文字记录", example="submit, handle, close",required=false)
    private String workFlow;

    @ApiModelProperty(value="预计完成时间", example="2019-06-18 11:11:11",required=false)
    private String finishTime;

    @ApiModelProperty(value="工单紧急程度, 数字越大级别越高", example="11",required=false)
    private Integer urgentDegree;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setReceptionist(String receptionist) {
        this.receptionist = receptionist;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public void setWorkFlow(String workFlow) {
        this.workFlow = workFlow;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public void setUrgentDegree(Integer urgentDegree) {
        this.urgentDegree = urgentDegree;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomer() {
        return customer;
    }

    public String getReceptionist() {
        return receptionist;
    }

    public Long getTypeId() {
        return typeId;
    }

    public String getWorkFlow() {
        return workFlow;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public Integer getUrgentDegree() {
        return urgentDegree;
    }
}
