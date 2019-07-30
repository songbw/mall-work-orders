package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="关爱通退款notify信息")
public class GuanAiTongRefundBean {

    @ApiModelProperty(value="外部交易号,order:paymentNo", example="7d96c92e0a01000",required=true)
    private String outer_trade_no;

    @ApiModelProperty(value="外部退款号,发起退款时生成：appId+timestamp+xxx", example="201108431564470073001",required=true)
    private String outer_refund_no;

    @ApiModelProperty(value="退款原因", example="7天无理由",required=true)
    private String reason;

    @ApiModelProperty(value="退款金额", example="201",required=true)
    private Float refund_amount;

    @ApiModelProperty(value="通知url", example=" http://api.weesharing.com/v2/workorders/refund/notify",required=true)
    private String notify_url;
}

