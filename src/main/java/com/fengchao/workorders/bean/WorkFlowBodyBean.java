package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(value="工单流程内容信息")
public class WorkFlowBodyBean {
    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="工单目标状态码", example="1",required=true)
    private Integer status;

    @ApiModelProperty(value="流程处理意见", example="移交")
    private String comments;

    @ApiModelProperty(value="流程处理人名称", example="somebody")
    private String operator;

    @ApiModelProperty(value="实际退费金额（元）", example="9.9")
    private Float refund;

    @ApiModelProperty(value="是否处理运费, 0 : 不处理运费", example="0")
    private Integer handleFare;

}
