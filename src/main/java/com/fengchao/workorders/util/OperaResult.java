package com.fengchao.workorders.util;
import com.fengchao.workorders.constants.MyErrorEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString
public class OperaResult implements Serializable {

	// 结果码
	private Integer code = 200;

	// 结果描述(消息框的内容)
	private String msg = "Success";

	//封装的对象
	private Map<String,Object> data = new HashMap<String,Object>();

	public OperaResult(Integer code,String msg,Map map){
		this.code = code;
		this.msg = msg;
		this.data = map;
	}

	public OperaResult(MyErrorEnum errorEnum){
		this.code = errorEnum.getCode();
		this.msg = errorEnum.getMsg();
		this.data = null;
	}
}
