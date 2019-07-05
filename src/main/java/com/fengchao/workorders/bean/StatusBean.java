package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel(value="审批信息")
public class StatusBean implements Serializable {
    @ApiModelProperty(value="Status", example="3",required=true)
    private Integer status;

    @ApiModelProperty(value="comments", example="good",required=true)
    private String comments;
    
}
