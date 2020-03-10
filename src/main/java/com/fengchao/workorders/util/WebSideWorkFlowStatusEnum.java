package com.fengchao.workorders.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum WebSideWorkFlowStatusEnum {
    /*
    * const approve_request = 1
const agree_refund = 2
const reject_refund = 3
const reject_change = 4
const change_good = 5
const reopen_workorder = 6
const change_receiver = 7
const update_user_logistics = 8
const FlowOperations = [
  { value: approve_request, label: '通过申请' },
  { value: agree_refund, label: '同意退款' },
  { value: reject_refund, label: '拒绝退款' },
  { value: reject_change, label: '拒绝换货' },
  { value: change_good, label: '换货处理' },
  { value: reopen_workorder, label: '重置工单' },
  { value: change_receiver, label: '修改收货人信息' },
  { value: update_user_logistics, label: '更新退货物流' }
]

comments = { remark: "备注", operation: 1 }
    * */

    APPROVED(1, "通过申请"),
    AGREE_REFUND(2, "同意退款"),
    REJECT_REFUND(3, "拒绝退款"),
    REJECT_CHANGE(4, "拒绝换货"),
    CHANGE_GOODS(5, "换货处理"),
    RESET(6, "重置工单"),
    CHANGE_RECEIVER(7, "修改收货人信息"),
    UPDATE_LOGISTICS(8, "更新退货物流"),

    NOTIFY_PENDING(301, "等待怡亚通审核"),

    NOTIFY_REJECT(302, "怡亚通反馈：供应商审核不通过"),
    NOTIFY_APPROVED(303, "怡亚通反馈：供应商审核通过"),
    NOTIFY_GOODS_SENT(304, "怡亚通反馈：买家已发货"),
    NOTIFY_REFUNDING(306, "怡亚通反馈：退款处理中"),
    NOTIFY_REFUNDED(307, "怡亚通反馈：退款成功"),
    NOTIFY_REFUND_CLOSED(308, "怡亚通反馈：退款关闭"),
    NOTIFY_PLATFORM_REJECT(309, "怡亚通反馈：平台驳回"),
    NOTIFY_TIMEOUT(310, "怡亚通反馈：超时未修改自动关闭"),
    NOTIFY_REJECT_FOR_GOODS_SENT(311, "怡亚通反馈：已发货无法退款"),
    NOTIFY_FINANCE_REJECT(312, "怡亚通反馈：财务审核不通过"),
    NOTIFY_FINANCE_APPROVED(313, "怡亚通反馈：财务审核通过"),
    NOTIFY_RETURN_RECEIVED(314, "怡亚通反馈：供应商确认收货"),
    NOTIFY_GOODS_SENDING(330, "怡亚通反馈：供应商已发货待收货"),

    THIRD_SN_BLANK(401, "怡亚通订单缺失怡亚通下订单信息,需要与怡亚通确认下单信息"),

    UNKNOWN(888, "无法处理状态"),
    ;

    private Integer code;
    private String msg;

    WebSideWorkFlowStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static
    String buildComments(WebSideWorkFlowStatusEnum status){
       return buildComments(status,null);
    }

    public static
    String buildComments(WebSideWorkFlowStatusEnum status, String remark){

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"remark\":\"");
        sb.append(status.getMsg());
        if (null != remark){
            sb.append(" ");
            sb.append(remark);
        }
        sb.append("\",\"");
        sb.append("operation\":");
        sb.append(status.code);
        sb.append("}");

        return sb.toString();
    }

    public static Map<String,String> getMap(){
        Map<String,String> map = new HashMap<>();
        for (WebSideWorkFlowStatusEnum item : WebSideWorkFlowStatusEnum.values()) {
            map.put(item.getCode().toString(),item.getMsg());
        }
        return map;
    }
}
