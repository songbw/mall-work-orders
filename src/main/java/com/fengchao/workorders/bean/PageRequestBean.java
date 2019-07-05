package com.fengchao.workorders.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
//import lombok.AllArgsConstructor;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Component(value="PageRequestBean")
@ApiModel(value="分页请求信息")
public class PageRequestBean<T> {

    @ApiModelProperty(value="页大小", example="1",required=true)
    private int pageSize;

    @ApiModelProperty(value="页码", example="1",required=true)
    private int pageIndex;

    private T data;

}
