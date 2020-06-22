package com.fengchao.workorders.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum WorkOrderStatusType {
    /**
     * */
    EDITING(1, "待审核"),
    PENDING(2, "审核中"),
    ACCEPTED(3, "审核通过"),
    REJECT(4, "审核有问题"),
    HANDLING(5, "处理中"),
    CLOSED(6, "处理完成"),
    REFUNDING(7, "退款处理中"),
    REFUND_FAILED(8, "退款失败"),

    RESERVED(888, "工单工作流记录"),
    ;

    @EnumValue
    private final Integer code;

    private final String msg;

    WorkOrderStatusType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static boolean isClosedStatus(WorkOrderStatusType status){
        return CLOSED.equals(status)||REFUND_FAILED.equals(status);
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

    public static WorkOrderStatusType checkByCode(Integer code) {
        if (null != code && 0 != code) {
            for (WorkOrderStatusType theEnum : WorkOrderStatusType.values()) {
                if (theEnum.getCode().equals(code)) {
                    return theEnum;
                }
            }
        }
        return null;
    }

    public static WorkOrderStatusType checkByName(String name) {
        for (WorkOrderStatusType theEnum : WorkOrderStatusType.values()) {
            if (name.equals(theEnum.name())) {
                return theEnum;
            }
        }
        return null;
    }
}
