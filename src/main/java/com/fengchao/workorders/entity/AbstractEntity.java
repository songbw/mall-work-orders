package com.fengchao.workorders.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 所有实体类公有字段
 *
 * @author Clark
 * @since 2020-06-18
 */
@Data
public class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String  CREATE_TIME="create_time";
    public static final String  UPDATE_TIME="update_time";


    /**CURRENT_TIMESTAMP*/
    @ApiModelProperty(value = "创建日期", example = "2020-06-18 12:00:00")
    @TableField(CREATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP*/
    @ApiModelProperty(value = "更新日期", example = "2020-06-18 12:00:00")
    @TableField(UPDATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}
