package com.fengchao.workorders.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fengchao.workorders.constants.WorkOrderStatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 工单工作流实体类
 * @author Clark
 * */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("work_flow")
@ApiModel(value="WorkFlow", description="工单工作流")
public class WorkFlow extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    public final static String ID = "id";
    public final static String WORK_ORDER_ID = "work_order_id";
    public final static String STATUS = "status";
    public final static String COMMENTS = "comments";
    public final static String CREATED_BY = "created_by";
    public final static String UPDATED_BY = "updated_by";

    @ApiModelProperty(value = "主键")
    @TableId(value = ID, type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "工单ID")
    @TableField(WORK_ORDER_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workOrderId;

    @ApiModelProperty(value = "工作流处理之后的工单状态")
    @TableField(STATUS)
    private WorkOrderStatusType status;

    @ApiModelProperty(value = "注释")
    @TableField(COMMENTS)
    @JsonSerialize(using = ToStringSerializer.class)
    private String comments;

    @ApiModelProperty(value = "工作流创建者")
    @TableField(CREATED_BY)
    @JsonSerialize(using = ToStringSerializer.class)
    private String createdBy;

}
