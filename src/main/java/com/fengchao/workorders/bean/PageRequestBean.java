package com.fengchao.workorders.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;
//import lombok.AllArgsConstructor;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Component(value="PageRequestBean")
@ApiModel(value="分页请求信息")
public class PageRequestBean<T> {

    @ApiModelProperty(value="页大小", example="1",required=true)
    private int pageSize;

    @ApiModelProperty(value="页码", example="1",required=true)
    private int pageIndex;

    private T data;


    public int getPageSize() {
        return this.pageSize;
    }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getPageIndex() {
        return this.pageIndex;
    }
    public void setPageIndex(int pageIndex) { this.pageIndex = pageIndex; }

    public T getData() {
        return this.data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
