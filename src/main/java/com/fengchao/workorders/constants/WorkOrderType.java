package com.fengchao.workorders.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author Clark
 * */
public enum WorkOrderType {
    /**
     * */
    RETURN(1, "退货退款"),
    EXCHANGE(2, "换货"),
    REFUND(3, "仅退款");

    @EnumValue
    private final Integer code;
    private final String msg;

    WorkOrderType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
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
                if (WorkOrderType.values()[i].getCode().equals(code)) {
                    return WorkOrderType.values()[i].toString();
                }
            }
        }
        return "";
    }

    public static WorkOrderType checkByCode(Integer code) {
        for (WorkOrderType workOrderType : WorkOrderType.values()) {
            if (workOrderType.getCode().equals(code)) {
                return workOrderType;
            }
        }
        return null;
    }

    public static WorkOrderType checkByName(String name) {
        for (WorkOrderType workOrderType : WorkOrderType.values()) {
            if (name.equals(workOrderType.name())) {
                return workOrderType;
            }
        }
        return null;
    }
}

