package com.fengchao.workorders.bean;

import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.exception.MyException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Clark
 * */
@Getter
@Setter
@ToString
@ApiModel(value="工单流程内容信息")
public class WorkFlowBodyBean {
    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="工单目标状态码", example="1",required=true)
    private Integer status;

    @ApiModelProperty(value="流程处理意见", example="移交")
    private String comments;

    @ApiModelProperty(value="流程处理人名称", example="somebody")
    private String operator;

    @ApiModelProperty(value="实际退费金额（元）", example="9.9")
    private Float refund;

    @ApiModelProperty(value="是否处理运费, 0 : 不处理运费", example="0")
    private Integer handleFare;

    @ApiModelProperty(value="快递单号", example="2019111111")
    private String expressNo;

    @ApiModelProperty(value="工单类型ID", example="1")
    private Integer typeId;

    public void checkParameters(){
        if (null == workOrderId || 0 == workOrderId) {
            throw new MyException(MyErrorEnum.PARAM_WORK_ORDER_ID_BLANK);
        }
        if (null == operator || operator.isEmpty()) {
            throw new MyException(MyErrorEnum.PARAM_OPERATOR_BLANK);
        }

    }
}
