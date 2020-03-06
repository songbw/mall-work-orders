package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value="怡亚通卖家已发货,退货物流发送POST body")
public class YiYaTongReturnGoodsBean {
    /*
    * * serviceSn  退款订单编号
     * orderSn    星链子订单sn(子订单thirdOrderSn字段)
     * deliveryCorpSn  物流公司编码（物流公司信息ID）
     * deliveryCorpName    物流公司名称（物流公司信息名称）
     * deliverySn          物流单号

     * */

    @ApiModelProperty(value="serviceSn  怡亚通退款订单编号,由怡亚通返回,记录在工单refundNo中", example="3421",required=true)
    private String serviceSn;

    @ApiModelProperty(value="orderSn 星链子订单sn(子订单thirdOrderSn字段)", example="3421",required=true)
    private String orderSn;

    @ApiModelProperty(value="deliveryCorpSn  物流公司编码（物流公司信息ID）", example="3421",required=true)
    private String deliveryCorpSn;

    @ApiModelProperty(value="deliveryCorpName  物流公司名称（物流公司信息名称）", example="快递",required=true)
    private String deliveryCorpName;

    @ApiModelProperty(value="deliverySn 物流单号", example="653421",required=true)
    private String deliverySn;
}
