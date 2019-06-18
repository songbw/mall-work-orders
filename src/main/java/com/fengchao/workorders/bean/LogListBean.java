package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

@ApiModel(value="Log List")
public class LogListBean {
    @ApiModelProperty(value="ID List", example="[1,2]",required=true)
    private List<Long> idList;

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public List<Long> getIdList() {
        return idList;
    }
}
