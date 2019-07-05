package com.fengchao.workorders.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value="附件记录信息")
public class AttachmentProfileBean {
    @ApiModelProperty(value="附件所属工单ID", example="123",required=true)
    private Long workOrderId;

    @ApiModelProperty(value="附件名称", example="商品图",required=true)
    private String name;

    @ApiModelProperty(value="附件链接", example="http://cdn.qos.com/company_business_license.jpg",required=true)
    private String path;

    @ApiModelProperty(value="附件提交者", example="张赞",required=true)
    private String submitter;

}
