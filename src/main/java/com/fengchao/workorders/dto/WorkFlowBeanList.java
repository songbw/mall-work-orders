package com.fengchao.workorders.dto;

import com.fengchao.workorders.bean.WorkFlowBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value = "工单及流程信息dto")
public class WorkFlowBeanList {
    @ApiModelProperty(value="所属订单ID", example="111",required=true)
    public String orderId;
    @ApiModelProperty(value="凤巢appID", example="10",required=true)
    public String iAppId;

    @ApiModelProperty(value="第三方appID", example="20110843",required=true)
    public String tAppId;

    @ApiModelProperty(value="退货商品数", example="1",required=true)
    public Integer returnedNum;

    @ApiModelProperty(value="申请退款金额", example="1.1")
    public Float refundAmount;

    @ApiModelProperty(value="实际退款金额", example="1.1")
    public Float realRefundAmount;
    @ApiModelProperty(value="商户ID", example="111")
    public Long merchantId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    public String title;

    @ApiModelProperty(value="工单描述", example="退货000011")
    public String description;

    @ApiModelProperty(value="客户ID", example="123")
    public String receiverId;

    @ApiModelProperty(value="客户名称", example="李四")
    public String receiverName;

    @ApiModelProperty(value="客户电话", example="13345678901")
    public String receiverPhone;

    @ApiModelProperty(value="工单状态码", example="1")
    public Integer status;

    @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11")
    public LocalDateTime updateTime;

    @ApiModelProperty(value="退款完成时间", example="2019-06-16 11:11:11")
    public LocalDateTime refundTime;

    @ApiModelProperty(value="快递单号", example="2019111111")
    public String expressNo;

    @ApiModelProperty(value="工单类型ID", example="1")
    public Integer typeId;

    @ApiModelProperty(value = "流程信息List", example = " ")
    public List<WorkFlowBean> result;

}
