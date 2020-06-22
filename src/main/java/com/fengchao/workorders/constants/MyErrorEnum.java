package com.fengchao.workorders.constants;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum MyErrorEnum {
    /**返回码*/
    RESPONSE_SUCCESS(200, "success"),
    RESPONSE_FUNCTION_ERROR(400, "failed"),

    PARAM_DATE_TIME_STRING_WRONG(400005, "日期时间格式错误：YYYY-MM-DD hh:mm:ss"),
    PARAM_ADDRESS_BLANK(400005, "地址内容 content 缺失"),

    PARAM_BODY_BLANK(400101, "http post body不可为空"),
    PARAM_OPERATOR_BLANK(400133, "操作员不可为空"),
    PARAM_WORK_ORDER_ID_BLANK(400134, "工单ID不可为空"),
    PARAM_WORK_ORDER_STATUS_BLANK(400135, "工单状态码不可为空"),
    PARAM_WORK_ORDER_STATUS_INVALID(400136, "工单状态码错误"),
    PARAM_FLOW_STATUS_BLANK(400137, "处理流程状态码不可为空"),
    PARAM_FLOW_STATUS_INVALID(400138, "处理流程状态码错误"),
    PARAM_WORK_ORDER_TYPE_BLANK(400139, "工单类型码不可为空"),
    PARAM_WORK_ORDER_TYPE_INVALID(400140, "工单类型码错误"),
    PARAM_ORDER_PAY_CODE_BLANK(400141, "支付码不能为空"),
    PARAM_REFUND_AMOUNT_BLANK(400142, "退款金额不可为空"),
    PARAM_REFUND_AMOUNT_INVALID(400143, "退款金额错误"),

    PARAMETER_NAME_EXIST(400202, "名称已经存在"),
    PARAMETER_PHONE_EXIST(400203, "电话号码已经存在"),
    PARAMETER_CODE_EXIST(400204, "编码已经存在"),
    PARAMETER_CORPORATION_CODE_NOT_EXIST(400205, "企业编码不存在"),
    PARAMETER_EMPLOYEE_STATUS_CODE_INVALID(400206, "员工状态码错误"),
    PARAMETER_NAME_PASSWORD_INVALID(400207, "名字或密码错误"),
    PARAMETER_VERIFY_CODE_BLANK(400208, "验证码不能为空"),
    PARAMETER_VERIFY_CODE_INVALID(400209, "验证码错误"),
    DEFAULT_ADDRESS_EXISTED(400210, "默认地址已经存在"),

    COMMON_DB_RECORD_BLANK(410101, "新建数据库记录,记录缺失"),
    COMMON_DB_ID_BLANK(410102, "删除数据库记录,id未输入"),
    COMMON_DB_UPDATE_RECORD_BLANK(410103, "更新数据库记录,记录缺失"),
    COMMON_DB_GET_RECORD_ID_BLANK(410104, "获取数据库记录,id未输入"),
    COMMON_DB_GET_RECORD_RESULT_NULL(410105, "记录不存在"),
    COMMON_DB_PUT_PHONE_OCCUPIED(410106, "电话号码已经存在"),
    COMMON_DB_PUT_NAME_OCCUPIED(410107, "名称已经存在"),
    COMMON_DB_PUT_CODE_OCCUPIED(410108, "编码已经存在"),

    COMMON_DB_INSERT_ERROR(410201, "新建数据库记录,插入新纪录失败"),
    COMMON_DB_DELETE_ERROR(410202, "删除数据库记录失败"),
    COMMON_DB_UPDATE_ERROR(410203, "更新数据库记录失败"),
    COMMON_DB_SELECT_ERROR(410204, "查询数据库记录失败"),
    COMMON_DB_UPDATE_SELECTIVE_ERROR(410205, "选择更新数据库记录失败"),

    REFUND_COUNT_OVERFLOW(410402, "回购数量超出订单购买数量"),
    REFUND_AMOUNT_OVERFLOW(410403, "退款金额超出订单总金额"),
    WORK_ORDER_ABNORMAL_ORDER_NO_FOUND(410404, "异常工单,订单号缺失"),
    WORK_ORDER_CAN_NO_UPDATE(410405, "工单处于不可修改状态"),
    WORK_ORDER_ABNORMAL_STATUS(410406, "异常工单,状态码异常"),
    WORK_ORDER_NO_NOT_FOUND(410407, "无此序号的工单记录"),
    WORK_ORDER_CAN_NOT_RESET(410408, "处于审核失败或关闭状态且无退款的工单才可以重置"),
    WORK_ORDER_CAN_NOT_PENDING(410409, "处于待审核/编辑状态的工单才可以设置为审核中"),
    WORK_ORDER_CAN_NOT_ACCEPT(410410, "处于待审核/编辑状态的工单才可以设置为审核通过"),
    WORK_ORDER_CAN_NOT_REJECT(410411, "处于待审核/编辑状态的工单才可以设置为审核失败"),
    WORK_ORDER_CAN_NOT_HANDLING(410412, "处于审核通过状态的工单才可以设置为处理中"),
    WORK_ORDER_CAN_NOT_REFUNDING(410413, "处于审核通过或处理中的工单才可以设置为退款处理中"),
    WORK_ORDER_HAS_CLOSED(410414, "工单已经处于关闭状态"),
    WORK_ORDER_REJECT_CAN_NOT_CLOSE(410415, "审核失败的工单不需要关闭"),
    WORK_ORDER_SET_STATUS_FAILED(410416, "设置工单状态失败"),


    NEED_RETRY(420003, "系统忙，请重试"),
    MYSQL_ERROR(420005, "数据存取异常，请联系管理员"),

    API_EQUITY_CREATE_TICKETS_NULL(420101,"权益服务调用返回为空 "),
    API_EQUITY_CREATE_TICKETS_FAILED(420102,"权益服务调用返回失败 "),
    ;

    private Integer code;
    private String msg;

    MyErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String buildCodeMsg(Integer code, String msg){
        //log.error(msg);
        return "@"+code.toString()+"@"+msg;
    }

    public String buildCodeMsg(){
        //log.error(this.msg);
        return "@"+this.code.toString()+"@"+this.msg;
    }

    public String buildCodeMsg(String msg){
        //log.error(this.msg);
        return "@"+this.code.toString()+"@"+this.msg+msg;
    }

    public String getCodeMsg(){
        return "@"+this.code.toString()+"@"+this.msg;
    }

    public static Map<String,Integer> getMap(){
        Map<String,Integer> map = new HashMap<>();
        for (MyErrorEnum item : MyErrorEnum.values()) {
            map.put(item.getMsg(),item.getCode());
        }
        return map;
    }
}
