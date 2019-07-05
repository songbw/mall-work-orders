package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(value="Log List")
public class LogListBean {
    @ApiModelProperty(value="ID List", example="[1,2]",required=true)
    private List<Long> idList;

}
