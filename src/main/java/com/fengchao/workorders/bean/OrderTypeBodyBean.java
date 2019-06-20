package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="工单类型登记信息")
public class OrderTypeBodyBean {
    @ApiModelProperty(value="工单流程名称", example="退货退款",required=true)
    private String name;

    @ApiModelProperty(value="工单流程图链接", example="反馈",required=false)
    private String workflowUrl;

    @ApiModelProperty(value="工单流程文字说明", example="反馈",required=true)
    private String workflowText;

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkflowUrl(String workflowUrl) {
        this.workflowUrl = workflowUrl;
    }

    public void setWorkflowText(String workflowText) {
        this.workflowText = workflowText;
    }

    public String getName() {
        return name;
    }

    public String getWorkflowUrl() {
        return workflowUrl;
    }

    public String getWorkflowText() {
        return workflowText;
    }
}
