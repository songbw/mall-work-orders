package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel(value="客户端查询工单内容信息")
public class CustomerQueryWorkOrderBean {
    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="所属订单ID", example="111",required=true)
    private String orderId;

    @ApiModelProperty(value="凤巢appID", example="10",required=true)
    private String iAppId;

    @ApiModelProperty(value="第三方appID", example="20110843",required=true)
    private String tAppId;

    @ApiModelProperty(value="退货商品数", example="1",required=true)
    private Integer returnedNum;

    @ApiModelProperty(value="申请退款金额", example="1.1",required=false)
    private Float refundAmount;

    @ApiModelProperty(value="实际退款金额", example="1.1",required=false)
    private Float realRefundAmount;

    @ApiModelProperty(value="商户ID", example="111",required=false)
    private Long merchantId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011",required=false)
    private String description;

    @ApiModelProperty(value="客户ID", example="123",required=false)
    private String receiverId;

    @ApiModelProperty(value="客户名称", example="李四",required=false)
    private String receiverName;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Integer typeId;

    @ApiModelProperty(value="客户电话", example="13345678901",required=false)
    private String receiverPhone;

    @ApiModelProperty(value="工单状态码", example="1",required=false)
    private Integer status;

    @ApiModelProperty(value="提交时间", example="2019-06-16 11:11:11",required=false)
    private Date createTime;

    @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11",required=false)
    private Date updateTime;

    @ApiModelProperty(value="出售单价", example="11",required=false)
    private Float salePrice;

    @ApiModelProperty(value="单价", example="11",required=false)
    private Float unitPrice;

    @ApiModelProperty(value="商品名称", example="T-shirt",required=false)
    private String name;

    @ApiModelProperty(value="图片链接", example="http://imgservice2.suning.cn/uimg1/b2c/image/gV8WOSpc02rfK1vp-pN-UA==.jpg_400w_400h_4e",required=false)
    private String image;

    @ApiModelProperty(value="快递单号", example="2019111111",required=false)
    private String expressNo;
}
