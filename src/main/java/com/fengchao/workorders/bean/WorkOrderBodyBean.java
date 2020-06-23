package com.fengchao.workorders.bean;

import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.constants.WorkOrderType;
import com.fengchao.workorders.exception.MyException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Clark
 * */

@ApiModel(value="工单更新信息Bean")
@Getter
@Setter
public class WorkOrderBodyBean {
    @ApiModelProperty(value="所属订单ID", example="111")
    private String orderId;

    @ApiModelProperty(value="工单标题", example="退货000011",required=true)
    private String title;

    @ApiModelProperty(value="工单描述", example="退货000011")
    private String description;

    @ApiModelProperty(value="客户姓名", example="张三")
    private String receiverName;

    @ApiModelProperty(value="客户ID", example="1111")
    private String receiverId;

    @ApiModelProperty(value="工单类型ID", example="123",required=true)
    private Integer typeId;

    @ApiModelProperty(value="客户电话", example="12345678901")
    private String receiverPhone;

    @ApiModelProperty(value="凤巢appID", example="10",required=true)
    private String iAppId;

    @ApiModelProperty(value="第三方appID", example="20110843",required=true)
    private String tAppId;

    @ApiModelProperty(value="供货商ID", example="11")
    private Long merchantId;

    @ApiModelProperty(value="退货数量", example="1",required=true)
    private Integer num;

    public void checkFields(){
        if (null == orderId || orderId.isEmpty()) {
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_ID_BLANK);
        }
        if (null == iAppId || iAppId.isEmpty()) {
            throw new MyException(MyErrorEnum.PARAM_I_APP_ID_BLANK);
        }

        if (null == merchantId) {
            throw new MyException(MyErrorEnum.PARAM_MERCHANT_ID_BLANK);
        }

        if (null == title || title.isEmpty()) {
            throw new MyException(MyErrorEnum.PARAM_TITLE_BLANK);
        }

        if (null == typeId) {
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_TYPE_BLANK);
        }else{
            if(null == WorkOrderType.checkByCode(typeId)){
                throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_TYPE_INVALID);
            }
        }
    }

}
