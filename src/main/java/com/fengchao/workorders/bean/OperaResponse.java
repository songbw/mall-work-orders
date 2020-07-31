package com.fengchao.workorders.bean;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@SuppressWarnings("serial")
@Setter
@Getter
public class OperaResponse<T> implements Serializable {
	
	// 结果码
	private Integer code = 200;
	
	// 结果描述(消息框的内容)
	private String msg = "Success";

	private String message = "success";
	
	//封装的对象
	private T data;
	
}
