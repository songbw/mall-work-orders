package com.fengchao.workorders.dto;

import com.fengchao.workorders.bean.OrderRefundBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ApiModel(value = "主订单售后信息")
public class ParentOrderRefundData implements Serializable {
    @ApiModelProperty(value = "主订单Id", example = "1", required = true)
    private Integer parentOrderId;

    @ApiModelProperty(value = "主订单付款金额(分)", example = "1", required = true)
    private Integer paymentAmount;

    @ApiModelProperty(value = "主订单退款金额(分)", example = "1", required = true)
    private int refundAmount;

    @ApiModelProperty(value = "主订单退款金额(分)", example = "1", required = true)
    private int realRefundAmount;

    private List<OrderRefundBean> result;
}
