package com.fengchao.workorders.model;

import java.util.Date;

public class WorkOrder {
    private Long id;

    private Long merchantId;

    private Integer parentOrderId;

    private String orderId;

    private Integer orderGoodsNum;

    private String tradeNo;

    private Integer returnedNum;

    private Float fare;

    private Integer paymentAmount;

    private Float salePrice;

    private String refundNo;

    private Float refundAmount;

    private Float guanaitongRefundAmount;

    private String guanaitongTradeNo;

    private String iAppId;

    private String tAppId;

    private String title;

    private String description;

    private String receiverId;

    private String receiverName;

    private String receiverPhone;

    private Integer typeId;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(Integer parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public Integer getOrderGoodsNum() {
        return orderGoodsNum;
    }

    public void setOrderGoodsNum(Integer orderGoodsNum) {
        this.orderGoodsNum = orderGoodsNum;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public Integer getReturnedNum() {
        return returnedNum;
    }

    public void setReturnedNum(Integer returnedNum) {
        this.returnedNum = returnedNum;
    }

    public Float getFare() {
        return fare;
    }

    public void setFare(Float fare) {
        this.fare = fare;
    }

    public Integer getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Integer paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Float salePrice) {
        this.salePrice = salePrice;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo == null ? null : refundNo.trim();
    }

    public Float getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Float refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Float getGuanaitongRefundAmount() {
        return guanaitongRefundAmount;
    }

    public void setGuanaitongRefundAmount(Float guanaitongRefundAmount) {
        this.guanaitongRefundAmount = guanaitongRefundAmount;
    }

    public String getGuanaitongTradeNo() {
        return guanaitongTradeNo;
    }

    public void setGuanaitongTradeNo(String guanaitongTradeNo) {
        this.guanaitongTradeNo = guanaitongTradeNo == null ? null : guanaitongTradeNo.trim();
    }

    public String getiAppId() {
        return iAppId;
    }

    public void setiAppId(String iAppId) {
        this.iAppId = iAppId == null ? null : iAppId.trim();
    }

    public String gettAppId() {
        return tAppId;
    }

    public void settAppId(String tAppId) {
        this.tAppId = tAppId == null ? null : tAppId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId == null ? null : receiverId.trim();
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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