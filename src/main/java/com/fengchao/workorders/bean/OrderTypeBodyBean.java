package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value="工单类型登记信息")
public class OrderTypeBodyBean {
    @ApiModelProperty(value="工单流程名称", example="退货退款",required=true)
    private String name;

    @ApiModelProperty(value="工单流程图链接", example="反馈",required=false)
    private String workflowUrl;

    @ApiModelProperty(value="工单流程文字说明", example="反馈",required=true)
    private String workflowText;

}
