package com.fengchao.workorders.util;

public enum WorkOrderStatusType {
    /**/
    EDITING(1, "待审核"),
    PENDING(2, "审核中"),
    ACCEPTED(3, "审核通过"),
    REJECT(4, "审核有问题"),
    HANDLING(5, "处理中"),
    CLOSED(6, "处理完成"),
    REFUNDING(7, "退款处理中");

    private Integer code;
    private String msg;

    WorkOrderStatusType(Integer code, String msg) {
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
            for (i = 0; i < WorkOrderStatusType.values().length; i++) {
                if (WorkOrderStatusType.values()[i].toString().equals(status)) {
                    return WorkOrderStatusType.values()[i].getCode();
                }
            }
        }
        return 0;
    }

    public static String Int2String(Integer code) {
        if (null != code && 0 != code) {
            int i;
            for (i = 0; i < WorkOrderStatusType.values().length; i++) {
                if (WorkOrderStatusType.values()[i].getCode() == code) {
                    return WorkOrderStatusType.values()[i].toString();
                }
            }
        }
        return "";
    }
}
