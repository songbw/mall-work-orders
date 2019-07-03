package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="工单更新信息")
public class WorkOrderBodyBean {
    @ApiModelProperty(value="所属订单ID", example="111",required=false)
    private String orderId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=false)
    private String description;

    @ApiModelProperty(value="客户姓名", example="张三",required=false)
    private String receiverName;

    @ApiModelProperty(value="客户ID", example="1111",required=false)
    private String receiverId;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Long typeId;

    @ApiModelProperty(value="客户电话", example="12345678901",required=false)
    private String receiverPhone;

    @ApiModelProperty(value="预计完成时间", example="2019-06-18 11:11:11",required=false)
    private String finishTime;

    @ApiModelProperty(value="工单紧急程度, 数字越大级别越高", example="11",required=false)
    private Integer urgentDegree;

    @ApiModelProperty(value="供货商ID", example="11",required=false)
    private Long merchantId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getUrgentDegree() {
        return urgentDegree;
    }

    public void setUrgentDegree(Integer urgentDegree) {
        this.urgentDegree = urgentDegree;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
