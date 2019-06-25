package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="客户工单更新信息")
public class CustomerWorkOrderBean {
    @ApiModelProperty(value="所属订单ID", example="111",required=true)
    private String orderId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=true)
    private String description;

    @ApiModelProperty(value="客户", example="张三",required=true)
    private String customer;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Long typeId;

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

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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

    public Long getTypeId() {
        return typeId;
    }
}
