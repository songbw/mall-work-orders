package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(value="聚合支付回调PostBody")
public class AggPayNotifyBean {
    @ApiModelProperty(value="out联机账户退款号", example="201108431564470073001",required=true)
    private String outRefundNo;

    @ApiModelProperty(value="原订单号", example="7d96c92e0a01000",required=true)
    private String sourceOutTradeNo;

    @ApiModelProperty(value="支付订单号", example="7d96c92e0a01000",required=true)
    private String orderNo;

    //@ApiModelProperty(value="联机账户订单号", example="7d96c92e0a01000",required=true)
    //private String tradeNo;

    @ApiModelProperty(value="联机账户退款号", example="201108431564470073001",required=true)
    private String  refundNo;

    @ApiModelProperty(value="商户编号", example="20",required=true)
    private String merchantCode;

    @ApiModelProperty(value="交易总金额", example="20",required=true)
    private String totalFee;

    @ApiModelProperty(value="交易实际金额", example="20",required=true)
    private String refundFee;

    @ApiModelProperty(value="交易状态 : 1: 成功, 2: 失败, 0: 新创建", example="20",required=true)
    private Integer status;

    @ApiModelProperty(value="退款时间", example="2019-09-09 00:00:00",required=true)
    private String tradeDate;

    //@ApiModelProperty(value="退款时间", example="2019-09-09 00:00:00",required=true)
    //private LocalDateTime createDate;
}
