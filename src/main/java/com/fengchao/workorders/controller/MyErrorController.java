package com.fengchao.workorders.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

//@ControllerAdvice
@ResponseBody
public class MyErrorController {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map<String,Object> errorHandle(Exception e){
        Map<String,Object> map = new HashMap<>();

        if (null != e && null != e.getCause()) {
            String codeMsg = e.getCause().getMessage();

            if (null != codeMsg) {
                String[] errInfor = codeMsg.split(":");
                if (1 < errInfor.length) {
                    map.put("error", errInfor[1].trim());
                } else {
                    map.put("error", "500");
                }
                if (2 < errInfor.length) {
                    map.put("message", errInfor[2].trim());
                } else {
                    map.put("message", "exception");
                }
            }
        }
        return map;
    }

}
