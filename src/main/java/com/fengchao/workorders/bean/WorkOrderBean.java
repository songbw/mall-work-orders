package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="工单内容信息")
public class WorkOrderBean {

    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="所属订单ID", example="111",required=false)
    private String orderId;

    @ApiModelProperty(value="商户ID", example="111",required=false)
    private Long merchantId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=false)
    private String description;

    @ApiModelProperty(value="客户ID", example="123",required=false)
    private String receiverId;

    @ApiModelProperty(value="客户名称", example="李四",required=false)
    private String receiverName;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Long typeId;

    @ApiModelProperty(value="客户电话", example="13345678901",required=false)
    private String receiverPhone;

    @ApiModelProperty(value="工单处理结果", example="退货完成",required=false)
    private String outcome;

    @ApiModelProperty(value="预计完成时间", example="2019-06-18 11:11:11",required=false)
    private Date finishTime;

    @ApiModelProperty(value="工单紧急程度, 数字越大级别越高", example="11",required=false)
    private Integer urgentDegree;

    @ApiModelProperty(value="工单状态码", example="1",required=false)
    private Integer status;

    @ApiModelProperty(value="提交时间", example="2019-06-16 11:11:11",required=false)
    private Date createTime;

    @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11",required=false)
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getUrgentDegree() {
        return urgentDegree;
    }

    public void setUrgentDegree(Integer urgentDegree) {
        this.urgentDegree = urgentDegree;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
