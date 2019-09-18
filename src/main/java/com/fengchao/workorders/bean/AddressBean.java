package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@ApiModel(value="地址信息")
public class AddressBean {
    @ApiModelProperty(value="ID", example="111",required=false)
    private Long id;

    @ApiModelProperty(value="是否为缺省地址", example="true",required=true)
    private Boolean isDefault;

    @ApiModelProperty(value="地址", example="北京市朝阳区建外街道88号",required=true)
    private String content;

}
