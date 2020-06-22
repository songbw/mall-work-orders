package com.fengchao.workorders.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fengchao.workorders.bean.AddressBean;
import com.fengchao.workorders.constants.AsDefaultEnum;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.exception.MyException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 默认退货地址实体类
 * @author Clark
 * */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("default_address")
@ApiModel(value="DefaultAddress", description="默认退货地址")
public class DefaultAddress extends AbstractEntity{

    private static final long serialVersionUID = 1L;

    public final static String ID = "id";
    public final static String AS_DEFAULT = "as_default";
    public final static String CONTENT = "content";


    @ApiModelProperty(value = "主键")
    @TableId(value = ID,type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "是否作为默认地址, 0:否， 1:是")
    @TableField(AS_DEFAULT)
    private AsDefaultEnum asDefault;

    @ApiModelProperty(value = "地址")
    @TableField(CONTENT)
    @JsonSerialize(using = ToStringSerializer.class)
    private String content;

    public static DefaultAddress
    convert(AddressBean bean){
        if(null == bean.getContent()){
            throw new MyException(MyErrorEnum.PARAM_ADDRESS_BLANK);
        }

        DefaultAddress address = new DefaultAddress();
        address.setContent(bean.getContent());
        if (null == bean.getIsDefault() || !bean.getIsDefault()){
            address.setAsDefault(AsDefaultEnum.NO);
        }else{
            address.setAsDefault(AsDefaultEnum.YES);
        }

        return address;
    }

    public static DefaultAddress
    updateConvert(AddressBean bean){
        String addressContent = bean.getContent();
        Boolean isDefault = bean.getIsDefault();

        DefaultAddress address = new DefaultAddress();
        if(null == addressContent && null == isDefault){
            return address;
        }

        if(null != addressContent) {
            address.setContent(addressContent);
        }

        if (null != isDefault) {
            if (isDefault) {
                address.setAsDefault(AsDefaultEnum.YES);
            } else {
                address.setAsDefault(AsDefaultEnum.NO);
            }
        }

        return address;
    }
}
