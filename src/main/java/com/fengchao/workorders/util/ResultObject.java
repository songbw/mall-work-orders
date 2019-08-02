package com.fengchao.workorders.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
//import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
//@AllArgsConstructor
public class ResultObject<T>{
    private Integer code;
    private String msg;
    private T data;

    public ResultObject(Integer code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

}
