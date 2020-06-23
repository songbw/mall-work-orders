package com.fengchao.workorders.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 租户配置实体类
 * @author Clark
 * */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("renter")
@ApiModel(value="Renter", description="租户配置")
public class Renter extends AbstractEntity{
    private static final long serialVersionUID = 1L;

    public final static String ID = "id";
    @ApiModelProperty(value = "主键")
    @TableId(value = ID, type = IdType.INPUT)
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    public final static String RENTER_NAME = "renter_name";
    @ApiModelProperty(value = "租户名称")
    @TableField(RENTER_NAME)
    @JsonSerialize(using = ToStringSerializer.class)
    private String renterName;
}
