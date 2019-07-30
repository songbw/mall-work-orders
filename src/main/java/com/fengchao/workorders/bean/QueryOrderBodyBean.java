package com.fengchao.workorders.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryOrderBodyBean {
    private Integer pageIndex;
    private Integer pageSize;
    private String subOrderId;
    private String openId;
    private Integer status;
}
