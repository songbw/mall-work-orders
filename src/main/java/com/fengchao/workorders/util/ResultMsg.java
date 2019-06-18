package com.fengchao.workorders.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class ResultMsg {
    private Integer code = 200;
    private String msg = "success";

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String message) {
        this.msg = message;
    }
}
