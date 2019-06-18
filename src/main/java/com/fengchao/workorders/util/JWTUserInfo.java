package com.fengchao.workorders.util;

import java.io.Serializable;

public class JWTUserInfo implements Serializable {
    private String userId;
    private String userName;
    private String realName;

    public JWTUserInfo(){}

    public JWTUserInfo(String userId, String userName, String realName) {
        this.userId = userId;
        this.userName = userName;
        this.realName = realName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
