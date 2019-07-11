package com.fengchao.workorders.util;

public enum WorkOrderType {
    RETURN(1, "退货退款"),
    EXCHANGE(2, "换货"),
    REFUND(3, "仅退款");

    private Integer code;
    private String msg;

    WorkOrderType(Integer code, String msg) {
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
            for (i = 0; i < WorkOrderType.values().length; i++) {
                if (WorkOrderType.values()[i].toString().equals(status)) {
                    return WorkOrderType.values()[i].getCode();
                }
            }
        }
        return 0;
    }

    public static String Int2String(Integer code) {
        if (null != code && 0 != code) {
            int i;
            for (i = 0; i < WorkOrderType.values().length; i++) {
                if (WorkOrderType.values()[i].getCode() == code) {
                    return WorkOrderType.values()[i].toString();
                }
            }
        }
        return "";
    }
}

