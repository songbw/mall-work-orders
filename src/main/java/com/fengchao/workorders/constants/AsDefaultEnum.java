package com.fengchao.workorders.constants;


import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 是否作为默认地址
 *
 * @author Clark
 * @since 2020-06-16
 */

public enum AsDefaultEnum {
    /**
     * */
    YES(1, "1", "作为默认地址"),
    NO(0, "0", "不作为默认地址"),
    ;

    @EnumValue
    private final Integer code;

    private final String value;
    private final String desc;

    AsDefaultEnum(Integer code, String value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public static AsDefaultEnum checkByName(String paySceneName) {
        for (AsDefaultEnum fcpPaySceneEnum : AsDefaultEnum.values()) {
            if (paySceneName.equals(fcpPaySceneEnum.name())) {
                return fcpPaySceneEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getDesc(){
        return desc;
    }
}
