package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value="怡亚通退款申请Response bean")
public class AoYiRefundResponseBean {

    @ApiModelProperty(value="serviceStatus,退款退货单状态", example="1",required=true)
    private String serviceStatus;

    @ApiModelProperty(value="serviceStatusName,退款退货单状态名称", example="申请中",required=true)
    private String serviceStatusName;

    @ApiModelProperty(value="orderSn,星链子订单sn(主订单thirdOrderSn字段)", example="1234",required=true)
    private String orderSn;

    @ApiModelProperty(value="serviceSn,退款订单编号,存入工单refundNo字段", example="4321",required=true)
    private String serviceSn;

    public static boolean isPassedStatus(String status){
        return ("13".equals(status) || "3".equals(status));
    }

    /*
    *
    * -- 退款返回状态  及状态名称
(1,"申请中"),
(2,"审核不通过"),
(3,"待平台退款"),
(6,"退款处理中"),
(7,"退款成功"),
(8,"退款关闭"),     -- 买家发起退货退款，又取消了退货退款   确认收货后，7天内，还可以再发起退款
(9,"平台驳回"),
(10,"超时未修改自动关闭"),  --买家发起退货，商家审核通过，3天内买家未发货，则是此状态
(11,"已发货无法退款"),
(12,"财务审核不通过"),
(13,"财务审核通过");
--退货退款状态  及状态名称
(1,"申请中"),
(2,"审核不通过"),
(3,"供应商审核通过"),
(4,"买家已发货"),
(5,"待平台退款"),       --账期用户不涉及
(6,"退款处理中"),       --账期用户不涉及
(7,"退款成功"),     -- 退货退款成功，结束
(8,"退款关闭"),     -- 买家发起退货退款，又取消了退货退款   确认收货后，7天内，还可以再发起退款
(9,"平台驳回"),
(10,"超时未修改自动关闭"),  --买家发起退货，商家审核通过，3天内买家未发货，则是此状态
(12,"财务审核不通过"),
(13,"财务审核通过");
    * */
}
