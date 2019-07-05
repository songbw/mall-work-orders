package com.fengchao.workorders.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SysLogBean {

    private Long id;
    private String user;
    private String method;
    private String param;
    private String url;
    private String createTimeStart;
    private String createTimeEnd;
    private String ip;
    private String timeUsed;
    private String userAgent;
    private String result;
    private List<Long> idList;


}
