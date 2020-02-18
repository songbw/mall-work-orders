package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value="怡亚通卖家未发货退款申请POST body")
public class AoYiRefundOnlyPostBean {

    @ApiModelProperty(value="orderSn,怡亚通订单号（子订单thirdOrderSn字段)", example="3421",required=true)
    private String orderSn;

    @ApiModelProperty(value="reason,原因(必填)", example="do not like",required=true)
    private String reason;

}
