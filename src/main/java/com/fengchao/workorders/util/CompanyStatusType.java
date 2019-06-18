package com.fengchao.workorders.util;

public enum CompanyStatusType {
    EDITING(1, "待审核"),
    PENDING(2, "审核中"),
    APPROVED(3, "审核通过"),
    REJECT(4, "审核失败");
    private Integer code;
    private String msg;

    CompanyStatusType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
            for (i = 0; i < CompanyStatusType.values().length; i++) {
                if (CompanyStatusType.values()[i].toString().equals(status)) {
                    return CompanyStatusType.values()[i].getCode();
                }
            }
        }
        return 0;
    }

    public static String Int2String(Integer code) {
        if (null != code && 0 != code) {
            int i;
            for (i = 0; i < CompanyStatusType.values().length; i++) {
                if (CompanyStatusType.values()[i].getCode() == code) {
                    return CompanyStatusType.values()[i].toString();
                }
            }
        }
        return "PENDING";
    }
}
