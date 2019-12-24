package com.fengchao.workorders.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

@Data
public class ResultMessage<T> {
    private Integer code = 200;
    private String message = "success";
    private T data;

}
