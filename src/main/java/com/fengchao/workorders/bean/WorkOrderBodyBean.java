package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value="工单更新信息")
public class WorkOrderBodyBean {
    @ApiModelProperty(value="所属订单ID", example="111",required=false)
    private String orderId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=false)
    private String description;

    @ApiModelProperty(value="客户姓名", example="张三",required=false)
    private String receiverName;

    @ApiModelProperty(value="客户ID", example="1111",required=false)
    private String receiverId;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Long typeId;

    @ApiModelProperty(value="客户电话", example="12345678901",required=false)
    private String receiverPhone;

    @ApiModelProperty(value="预计完成时间", example="2019-06-18 11:11:11",required=false)
    private String finishTime;

    @ApiModelProperty(value="工单紧急程度, 数字越大级别越高", example="11",required=false)
    private Integer urgentDegree;

    @ApiModelProperty(value="供货商ID", example="11",required=false)
    private Long merchantId;

}
