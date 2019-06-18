package com.fengchao.workorders.util;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.JSONObject;
import java.util.*;

public class JSONResult{
    public static String fillResultString(Integer code, String msg, Object data){
        Map < String , Object > jsonMap = new HashMap< String , Object>();
        jsonMap.put("code",code);
        jsonMap.put("msg",msg);
        jsonMap.put("result",data);

        return JSONObject.toJSONString(jsonMap,SerializerFeature.WriteMapNullValue);
    }
    public static String fillLoginString(String token, String phone){
        Map < String , Object > jsonMap = new HashMap< String , Object>();
        jsonMap.put("token",token);
        jsonMap.put("phone",phone);

        return JSONObject.toJSONString(jsonMap,SerializerFeature.WriteMapNullValue);
    }

    public static String fillCodeLoginString(String token, String role){
        Map < String , Object > jsonMap = new HashMap< String , Object>();
        jsonMap.put("token",token);
        jsonMap.put("role",role);

        return JSONObject.toJSONString(jsonMap,SerializerFeature.WriteMapNullValue);
    }

    public static String fillErrorString(Integer code, String msg){
        Map < String , Object > jsonMap = new HashMap< String , Object>();
        jsonMap.put("error",code);
        jsonMap.put("message",msg);

        return JSONObject.toJSONString(jsonMap,SerializerFeature.WriteMapNullValue);
    }
}

