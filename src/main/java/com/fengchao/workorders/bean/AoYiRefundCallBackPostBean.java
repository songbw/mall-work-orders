package com.fengchao.workorders.bean;

import com.fengchao.workorders.util.WebSideWorkFlowStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value="怡亚通退款申请回调POST body")
public class AoYiRefundCallBackPostBean {

    @ApiModelProperty(value="orderSn,星链子订单sn(主订单thirdOrderSn字段)", example="1234",required=true)
    private String orderSn;

    @ApiModelProperty(value="serviceSn,退款订单编号,工单创建时已经写入refundNo字段", example="4321",required=true)
    private String serviceSn;

    @ApiModelProperty(value="outOrderNo 第三方订单号（主订单tradeNo后8位）", example="12345678",required=true)
    private String outOrderNo;

    @ApiModelProperty(value="oldStatus 原订单状态", example="20",required=true)
    private String oldStatus;

    @ApiModelProperty(value="oldStatusName 原订单状态名称", example="供应商发货")
    private String oldStatusName;

    @ApiModelProperty(value="newStatus 更新后的状态", example="3",required=true)
    private String newStatus;

    @ApiModelProperty(value="newStatusName 更新后的状态名称", example="供应商审核通过")
    private String newStatusName;

    @ApiModelProperty(value="statusUpdateTime 状态更新时间 如：2019-01-01 18:10:01", example="2019-01-01 18:10:01",required=false)
    private String statusUpdateTime;

    @ApiModelProperty(value="updateType 状态变更类型 1.订单状态变更 2.退货退款状态变更", example="1",required=true)
    private String updateType;


    /*
    *
变更提醒类型	oldStatus	oldStatusName   newStatus	newStatusName	updateType
供应商审核不通过  1	    供应商审核中   	    10	   退货/退款关闭	        2
退货供应商审核通过	1	 供应商审核中(待买家发货)   3     供应商审核通过      	2
买家发货	        3	供应商审核通过（待买家发货） 4     买家发货供应商待收货   	2
供应商拒绝收货	4	 待供应商确认收货(待平台退款)3	   供应商审核(拒绝收货),待买家发货   2
供应商确认收货	4	    供应商审核通过	     5	   确认收货，待平台退款	2

财务审核通过	    3	  商审核通过,待平台退款     7	   退款成功   	        2
财务审核通过(单据确认) 5 商审核通过,待平台退款	 7	   退货成功            	2
//below items has been removed
财务审核不通过	3	  商审核通过,待平台退款	12	   财务审核不通过	        2
财务审核不通过	5	供应商确认收货,待平台退款	12	   财务审核不通过     	2
    * */

    public static boolean
    isReturnGoodsStatus(String newStatus){
        return ("5".equals(newStatus));
    }

    public static boolean
    isPassedStatus(String status){

        return ("3".equals(status) || "5".equals(status));
    }
    public static boolean
    isRejectedStatus(String status){

        return ("12".equals(status) || "10".equals(status));
    }

    public static
    WebSideWorkFlowStatusEnum convert2workflowCommentsCode(String newStatus,String oldStatus){

        switch(newStatus){
            case "4":
                return WebSideWorkFlowStatusEnum.NOTIFY_WAITING_RETURN_RECEIVED;
            case "3":
                if ("4".equals(oldStatus)){
                    return WebSideWorkFlowStatusEnum.NOTIFY_NEED_RETURN;
                }else {
                    return WebSideWorkFlowStatusEnum.NOTIFY_APPROVED;
                }
            case "5":
                return WebSideWorkFlowStatusEnum.NOTIFY_RETURN_RECEIVED;
            case "7":
                if("5".equals(oldStatus)){
                    return WebSideWorkFlowStatusEnum.NOTIFY_REFUNDED_RETURN;
                }else {
                    return WebSideWorkFlowStatusEnum.NOTIFY_REFUNDED;
                }
            case "10":
                return WebSideWorkFlowStatusEnum.NOTIFY_TIMEOUT;

            /*
            case "12":
                return WebSideWorkFlowStatusEnum.NOTIFY_FINANCE_REJECT;
            case "13":
                return WebSideWorkFlowStatusEnum.NOTIFY_FINANCE_APPROVED;
            case "30":
                return WebSideWorkFlowStatusEnum.NOTIFY_GOODS_SENDING;
                */
            default:
                return WebSideWorkFlowStatusEnum.UNKNOWN;
        }
    }
}
