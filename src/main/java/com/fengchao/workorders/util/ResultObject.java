package com.fengchao.workorders.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fengchao.workorders.constants.MyErrorEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
//import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
//@AllArgsConstructor
public class ResultObject<T> implements Serializable {
    public static final String CODE = "code";
    public static final String MESSAGE = "msg";
    public static final String DATA = "data";

    private Integer code;
    private String msg;
    private T data;

    public ResultObject(Integer code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public ResultObject() {
        this.code = MyErrorEnum.RESPONSE_SUCCESS.getCode();
        this.msg = MyErrorEnum.RESPONSE_SUCCESS.getMsg();
        this.data = null;
    }

    public ResultObject(String message) {
        this.code = MyErrorEnum.RESPONSE_FUNCTION_ERROR.getCode();
        this.msg = message;
        this.data = null;
    }

    public ResultObject( T data) {
        this.code = MyErrorEnum.RESPONSE_SUCCESS.getCode();
        this.msg = MyErrorEnum.RESPONSE_SUCCESS.getMsg();
        this.data = data;
    }

    public static ResultObject isOk(){
        return new ResultObject();
    }

    public ResultObject data(T data){
        this.setData(data);
        return this;
    }
}
