package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "子订单售后信息")
public class OrderRefundBean {
    @ApiModelProperty(value = "子订单Id", example = "1", required = true)
    private String orderId;

    @ApiModelProperty(value="子订单商品数", example="1",required=true)
    private Integer orderGoodsNum;

    @ApiModelProperty(value="退货商品数", example="1",required=true)
    private Integer returnedNum;

    @ApiModelProperty(value="申请退款金额(元)", example="1.1",required=false)
    private Float refundAmount;

    @ApiModelProperty(value="实际退款金额(元)", example="1.1",required=false)
    private Float realRefundAmount;
}
