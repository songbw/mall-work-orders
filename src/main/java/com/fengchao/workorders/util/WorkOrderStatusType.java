package com.fengchao.workorders.util;

public enum WorkOrderStatusType {
    PENDING(1, "等待审核"),
    RETURN(2, "待退货"),
    RETURNED(3, "已退货"),
    REFUND(4, "待退款"),
    REFUNDED(5, "退款成功"),
    CLOSED(6, "退款关闭"),
    REJECT(7, "审核已拒绝");

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
