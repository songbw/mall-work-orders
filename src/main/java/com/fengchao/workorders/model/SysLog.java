package com.fengchao.workorders.model;

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

    /**
     * get 主键
     *
     * @return Long
     */
    public Long getId() {
        return id;
    }

    /**
     * set 主键
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * get 创建时间
     *
     * @return java.util.Date
     */
    public java.util.Date getCreateTime() {
        return createTime;
    }

    /**
     * set 创建时间
     *
     * @param createTime
     */
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    /**
     * get 数据状态,1:正常,2:删除
     *
     * @return Integer
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * set 数据状态,1:正常,2:删除
     *
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * get 请求ip
     *
     * @return String
     */
    public String getIp() {
        return ip;
    }

    /**
     * set 请求ip
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * get 操作用户
     *
     * @return String
     */
    public String getUser() {
        return user;
    }

    /**
     * set 操作用户
     *
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * get 请求方法
     *
     * @return String
     */
    public String getMethod() {
        return method;
    }

    /**
     * set 请求方法
     *
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * get 请求参数
     *
     * @return String
     */
    public String getParam() {
        return param;
    }

    /**
     * set 请求参数
     *
     * @param param
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * get 请求结果
     *
     * @return String
     */
    public String getResult() {
        return result;
    }

    /**
     * set 请求结果
     *
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * get 持续时间
     *
     * @return Long
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * set 持续时间
     *
     * @param duration
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * get 请求url
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }

    /**
     * set 请求url
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * get 请求ua标识
     *
     * @return String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * set 请求ua标识
     *
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "SysLog{" +
                "id='" + id + '\'' +
                ", createTime='" + createTime + '\'' +
                ", status='" + status + '\'' +
                ", ip='" + ip + '\'' +
                ", user='" + user + '\'' +
                ", method='" + method + '\'' +
                ", param='" + param + '\'' +
                ", result='" + result + '\'' +
                ", duration='" + duration + '\'' +
                ", url='" + url + '\'' +
                ", userAgent=" + userAgent +
                '}';
    }
}
