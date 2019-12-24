package com.fengchao.workorders.util;
/**
* 聚合支付退款状态
* @author Clark
 * @date 2019/12/20
* */
public enum AggPayRefundStatusEnum {
    /*
    * ENUM
    * */
    SUCCESS(1, "成功"),
    FAILED(2, "失败"),
    NEW(3, "新创建");

    private Integer code;
    private String description;

    AggPayRefundStatusEnum(Integer code, String msg) {
        this.code = code;
        this.description = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String msg) {
        this.description = msg;
    }

    public static Integer string2Int(String status) {
        if (null != status && !status.isEmpty()) {
            int i;
            for (i = 0; i < AggPayRefundStatusEnum.values().length; i++) {
                if (AggPayRefundStatusEnum.values()[i].toString().equals(status)) {
                    return AggPayRefundStatusEnum.values()[i].getCode();
                }
            }
        }
        return 0;
    }

    public static String int2String(Integer code) {
        if (null != code && 0 != code) {
            int i;
            for (i = 0; i < AggPayRefundStatusEnum.values().length; i++) {
                if (AggPayRefundStatusEnum.values()[i].getCode().equals(code)) {
                    return AggPayRefundStatusEnum.values()[i].toString();
                }
            }
        }
        return "";
    }

}
