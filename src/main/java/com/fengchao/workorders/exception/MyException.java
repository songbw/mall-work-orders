package com.fengchao.workorders.exception;

import com.fengchao.workorders.constants.MyErrorEnum;

/**
 * @author clark
 */
public class MyException extends RuntimeException {

    public MyException(MyErrorEnum myErrorEnum){

        super(myErrorEnum.buildCodeMsg());
    }

    public MyException(MyErrorEnum myErrorEnum,String msg){

        super(myErrorEnum.buildCodeMsg(msg));
    }
}
