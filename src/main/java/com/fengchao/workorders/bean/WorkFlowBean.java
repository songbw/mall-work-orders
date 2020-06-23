package com.fengchao.workorders.bean;

import com.fengchao.workorders.entity.WorkFlow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ApiModel(value="工单流程查询信息")
public class WorkFlowBean {

    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="工单状态码", example="1",required=false)
    private Integer status;

    @ApiModelProperty(value="流程处理意见", example="移交",required=true)
    private String comments;

    @ApiModelProperty(value="提交时间", example="2019-06-16 11:11:11",required=false)
    private LocalDateTime createTime;

    @ApiModelProperty(value="更新时间", example="2019-06-16 11:11:11",required=false)
    private LocalDateTime updateTime;

    @ApiModelProperty(value="流程处理人名称", example="tom",required=true)
    private String operator;

    public static WorkFlowBean
    convert(WorkFlow workFlow){
        WorkFlowBean bean = new WorkFlowBean();
        BeanUtils.copyProperties(workFlow, bean);
        bean.setOperator(workFlow.getCreatedBy());
        bean.setStatus(workFlow.getStatus().getCode());
        return bean;
    }
}
