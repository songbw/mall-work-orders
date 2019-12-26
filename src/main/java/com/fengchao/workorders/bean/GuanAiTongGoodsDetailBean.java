package com.fengchao.workorders.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="关爱通交易商品详情 Bean")
public class GuanAiTongGoodsDetailBean {

    @ApiModelProperty(value="商品编号[1,50]", example="111",required=true)
    private String sku_id;

    @ApiModelProperty(value="商品名称[1,50]", example="miniCard",required=true)
    private String name;

    @ApiModelProperty(value="商品数量,最少为1", example="1",required=true)
    private Integer quantity;

    @ApiModelProperty(value="支付价(元)[0.00  99999999.99]", example="1.11",required=true)
    private Float good_pay_amount;

    @ApiModelProperty(value="关爱通结算价(元)[0.00  99999999.99]", example="1.11",required=true)
    private Float good_cost_amount;

}
