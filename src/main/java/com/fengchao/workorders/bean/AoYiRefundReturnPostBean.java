package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value="怡亚通卖家已发货,退货退款申请POST body")
public class AoYiRefundReturnPostBean {

    @ApiModelProperty(value="orderSn,怡亚通订单号（主订单aoyiId字段)", example="3421",required=true)
    private String orderSn;

    @ApiModelProperty(value="reason,原因(必填)", example="wrong size",required=true)
    private String reason;

    @ApiModelProperty(value="code 商品编码（子订单skuId字段）", example="123",required=true)
    private String code;

    @ApiModelProperty(value="returnType 退货类型，0：退货退款，1：仅退款", example="0",required=true)
    private String returnType;

}
