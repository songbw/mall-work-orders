package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="关爱通交易详情Post Bean")
public class GuanAiTongTradeInfoPostBean {

    @ApiModelProperty(value="外部交易号,order:paymentNo", example="7d96c92e0a01000",required=true)
    private String outer_trade_no;

    @ApiModelProperty(value="外部退款号，视为一次退款行为，请以appid开头，例如：10000001201", example="2011834c92e0a01000",required=true)
    private String outer_refund_no;

    @ApiModelProperty(value="交易详情，JSON格式", example="",required=true)
    private GuanAiTongTradeInfo trade_info;

}
