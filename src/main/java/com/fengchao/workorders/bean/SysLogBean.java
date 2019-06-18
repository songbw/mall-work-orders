package com.fengchao.workorders.bean;

import java.util.List;

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

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIdList() {
        return this.idList;
    }
    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateTimeStart() {return this.createTimeStart; }
    public void setCreateTimeStart(String createTime) { this.createTimeStart = createTime; }

    public String getCreateTimeEnd() {return this.createTimeEnd; }
    public void setCreateTimeEnd(String createTime) { this.createTimeEnd = createTime; }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public String getTimeUsed() {
        return this.timeUsed;
    }
    public void setTimeUsed(String duration) {
        this.timeUsed = duration;
    }

    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
