package com.fengchao.workorders.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysLog {

    // id :主键
    private Long id;

    // create_time :创建时间
    private java.util.Date createTime;

    // status :数据状态,1:正常,2:删除
    private Integer status;

    // ip :请求ip
    private String ip;

    // user :操作用户
    private String user;

    // method :请求方法
    private String method;

    // param :请求参数
    private String param;

    // result :请求结果
    private String result;

    // duration :持续时间
    private Long duration;

    // url :请求url
    private String url;

    // user_agent :请求ua标识
    private String userAgent;

}
