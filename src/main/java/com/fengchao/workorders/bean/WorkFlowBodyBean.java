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

    @ApiModelProperty(value="工单状态码", example="1",required=false)
    private Integer status;

    @ApiModelProperty(value="流程处理意见", example="移交",required=true)
    private String comments;

    @ApiModelProperty(value="流程处理人名称", example="tom",required=true)
    private String operator;

}
