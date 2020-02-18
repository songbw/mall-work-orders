package com.fengchao.workorders.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

@Getter
@Setter
@ToString
public class ResultMessage<T> {
    private Integer code = 200;
    private String message = "success";
    private T data;

    public ResultMessage(){
        this.code = 200;
    }

    public ResultMessage(Integer code,String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
