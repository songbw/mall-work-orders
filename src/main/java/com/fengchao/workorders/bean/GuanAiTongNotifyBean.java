package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="关爱通退款notify信息")
public class GuanAiTongNotifyBean {
    @ApiModelProperty(value="应用appid", example="20110843",required=true)
    private String appid;

    @ApiModelProperty(value="外部交易号,order:paymentNo", example="7d96c92e0a01000",required=true)
    private String outer_trade_no;

    @ApiModelProperty(value="外部退款号,发起退款时生成：appId+timestamp+xxx", example="201108431564470073001",required=true)
    private String outer_refund_no;

    @ApiModelProperty(value="关爱通交易号", example="20110843000001",required=true)
    private String trade_no;

    @ApiModelProperty(value="退款金额", example="201",required=true)
    private Float refund_amount;

    @ApiModelProperty(value="时间戳,时区为GMT+8（东八区）", example="20110843",required=true)
    private String timestamp;

    @ApiModelProperty(value="签名", example="20jaksjflsfjlsdfslfsl22222",required=true)
    private String sign;
}
