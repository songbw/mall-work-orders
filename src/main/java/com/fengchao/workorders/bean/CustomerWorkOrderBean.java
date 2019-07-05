package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="客户工单更新信息")
public class CustomerWorkOrderBean {
    @ApiModelProperty(value="所属订单ID", example="111",required=true)
    private String orderId;

    @ApiModelProperty(value="商户ID", example="11",required=false)
    private Long merchantId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=false)
    private String description;

    @ApiModelProperty(value="客户", example="张三",required=false)
    private String customer;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Long typeId;

}
