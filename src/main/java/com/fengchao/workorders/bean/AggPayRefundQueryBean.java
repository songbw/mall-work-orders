package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(value="退款对象", description="")
public class AggPayRefundQueryBean {
    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "退款号")
    private String outRefundNo;

    @ApiModelProperty(value = "原订单号")
    private String sourceOutTradeNo;

    @ApiModelProperty(value = "支付订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantCode;

    @ApiModelProperty(value = "交易总金额")
    private String totalFee;

    @ApiModelProperty(value = "交易实际金额")
    private String refundFee;

    @ApiModelProperty(value = "交易状态: 1: 成功, 2: 失败, 0: 新创建")
    private Integer status;

    @ApiModelProperty(value = "交易状态解释")
    private String statusMsg;

    @ApiModelProperty(value = "退款时间")
    private String tradeDate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

}
