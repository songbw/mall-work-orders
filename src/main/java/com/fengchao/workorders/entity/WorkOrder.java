package com.fengchao.workorders.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import com.fengchao.workorders.constants.WorkOrderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 工单实体类
 * @author Clark
 * */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("work_order")
@ApiModel(value="WorkOrder", description="工单")
public class WorkOrder extends AbstractEntity{

    private static final long serialVersionUID = 1L;


    public final static String ID = "id";
    @ApiModelProperty(value = "主键")
    @TableId(value = ID, type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    public final static String MERCHANT_ID = "merchant_id";
    @ApiModelProperty(value = "厂商ID")
    @TableField(MERCHANT_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;

    public final static String PARENT_ORDER_ID = "parent_order_id";
    @ApiModelProperty(value = "主订单ID")
    @TableField(PARENT_ORDER_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer parentOrderId;

    public final static String ORDER_ID = "order_id";
    @ApiModelProperty(value = "子订单ID")
    @TableField(ORDER_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private String orderId;

    public final static String ORDER_GOODS_NUM ="order_goods_num";
    @ApiModelProperty(value = "子订单商品数")
    @TableField(ORDER_GOODS_NUM)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer orderGoodsNum;

    public final static String TRADE_NO = "trade_no";
    @ApiModelProperty(value = "子订单交易号")
    @TableField(TRADE_NO)
    @JsonSerialize(using = ToStringSerializer.class)
    private String tradeNo;

    public final static String RETURNED_NUM = "returned_num";
    @ApiModelProperty(value = "退货商品数")
    @TableField(RETURNED_NUM)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer returnedNum;

    public final static String FARE = "fare";
    @ApiModelProperty(value = "子订单运费")
    @TableField(FARE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Float fare;

    public final static String PAYMENT_AMOUNT = "payment_amount";
    @ApiModelProperty(value = "子订单金额（分）")
    @TableField(PAYMENT_AMOUNT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer paymentAmount;

    public final static String SALE_PRICE = "sale_price";
    @ApiModelProperty(value = "子订单商品单价（分）")
    @TableField(SALE_PRICE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Float salePrice;

    public final static String REFUND_NO = "refund_no";
    @ApiModelProperty(value = "退款码")
    @TableField(REFUND_NO)
    @JsonSerialize(using = ToStringSerializer.class)
    private String refundNo;

    public final static String REFUND_AMOUNT = "refund_amount";
    @ApiModelProperty(value = "退款金额（分）")
    @TableField(REFUND_AMOUNT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Float refundAmount;

    public final static String GUANAITONG_REFUND_AMOUNT = "guanaitong_refund_amount";
    @ApiModelProperty(value = "支付渠道实际退款金额（分）")
    @TableField(GUANAITONG_REFUND_AMOUNT)
    @JsonSerialize(using = ToStringSerializer.class)
    private Float guanaitongRefundAmount;

    public final static String GUANAITONG_TRADE_NO = "guanaitong_trade_no";
    @ApiModelProperty(value = "支付渠道交易码")
    @TableField(GUANAITONG_TRADE_NO)
    @JsonSerialize(using = ToStringSerializer.class)
    private String guanaitongTradeNo;

    public final static String I_APP_ID = "i_app_id";
    @ApiModelProperty(value = "多端编码（客户端编码）")
    @TableField(I_APP_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private String iAppId;

    public final static String T_APP_ID = "t_app_id";
    @ApiModelProperty(value = "三方客户端编码")
    @TableField(T_APP_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private String tAppId;

    public final static String TITLE = "title";
    @ApiModelProperty(value = "工单标题")
    @TableField(TITLE)
    @JsonSerialize(using = ToStringSerializer.class)
    private String title;

    public final static String DESCRIPTION = "description";
    @ApiModelProperty(value = "工单描述")
    @TableField(DESCRIPTION)
    @JsonSerialize(using = ToStringSerializer.class)
    private String description;

    public final static String RECEIVER_ID = "receiver_id";
    @ApiModelProperty(value = "收货人ID")
    @TableField(RECEIVER_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private String receiverId;

    public final static String RECEIVER_NAME = "receiver_name";
    @ApiModelProperty(value = "收货人姓名")
    @TableField(RECEIVER_NAME)
    @JsonSerialize(using = ToStringSerializer.class)
    private String receiverName;

    public final static String RECEIVER_PHONE = "receiver_phone";
    @ApiModelProperty(value = "收货人电话")
    @TableField(RECEIVER_PHONE)
    @JsonSerialize(using = ToStringSerializer.class)
    private String receiverPhone;

    public final static String TYPE_ID = "type_id";
    @ApiModelProperty(value = "工单类型")
    @TableField(TYPE_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private WorkOrderType typeId;

    public final static String STATUS = "status";
    @ApiModelProperty(value = "工单状态")
    @TableField(TRADE_NO)
    @JsonSerialize(using = ToStringSerializer.class)
    private WorkOrderStatusType status;

    public final static String REFUND_TIME = "refund_time";
    @ApiModelProperty(value = "支付渠道退款完成时间")
    @TableField(REFUND_TIME)
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime refundTime;

    public final static String EXPRESS_NO = "express_no";
    @ApiModelProperty(value = "退货快递单号")
    @TableField(EXPRESS_NO)
    @JsonSerialize(using = ToStringSerializer.class)
    private String expressNo;

    public final static String COMMENTS = "comments";
    @ApiModelProperty(value = "工单注释")
    @TableField(COMMENTS)
    @JsonSerialize(using = ToStringSerializer.class)
    private String comments;
}
