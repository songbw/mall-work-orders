package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="聚合支付退款PostBody")
public class AggPayRefundBean {
    @ApiModelProperty(value="外部退款号,发起退款时生成：appId+timestamp+xxx", example="201108431564470073001",required=true)
    private String outRefundNo;

    @ApiModelProperty(value="原订单号,order:orderId", example="71c0283e-1685-4f70-ae8c-af4e1fc64736",required=true)
    private String orderNo;

    @ApiModelProperty(value="商户编号,order:merchantNo", example="20",required=true)
    private String merchantCode;

    @ApiModelProperty(value="退款金额,单位：分", example="2000",required=true)
    private String refundFee;

    @ApiModelProperty(value="前端返回地址", example="2000",required=false)
    private String returnUrl;

    @ApiModelProperty(value="异步通知地址", example="/refund/notify",required=true)
    private String notifyUrl;
}
