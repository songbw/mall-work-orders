package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(value="关爱通交易详情内容")
public class GuanAiTongTradeInfo {

    @ApiModelProperty(value="第三方订单号(外部交易号,order:paymentNo)", example="7d96c92e0a01000",required=true)
    private String third_trade_no;

    @ApiModelProperty(value="第三方退款单号", example="111",required=true)
    private String third_refund_no;

    @ApiModelProperty(value="是否有子订单 1有 2无", example="2",required=true)
    private Integer is_third_orders;

    @ApiModelProperty(value="退款总金额(元)[0.01  99999999.99]", example="1.11",required=true)
    private Float third_refund_amount;

    @ApiModelProperty(value="结算总成本价(元)[0.00  99999999.99]", example="1.11",required=true)
    private Float third_cost_amount;

    @ApiModelProperty(value="无子订单必填。商品信息列表，List<goods_detail>", example="",required=true)
    private List<GuanAiTongGoodsDetailBean> goods_detail;

}

