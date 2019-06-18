package com.fengchao.workorders.util;

public enum UserStatusType {
    NORMAL(1, "正常"),
    ABNORMAL(2, "锁定");
    private int code;
    private String msg;

    UserStatusType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Integer String2Int(String status) {
        if (null != status && !status.isEmpty()) {
            int i;
            for (i = 0; i < UserStatusType.values().length; i++) {
                if (UserStatusType.values()[i].toString().equals(status)) {
                    return UserStatusType.values()[i].getCode();
                }
            }
        }
        return 0;
    }

    public static String Int2String(Integer code) {
        if (null != code && 0 != code) {
            int i;
            for (i = 0; i < UserStatusType.values().length; i++) {
                if (UserStatusType.values()[i].getCode() == code) {
                    return UserStatusType.values()[i].toString();
                }
            }
        }
        return "";
    }
}
