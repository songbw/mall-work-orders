package com.fengchao.workorders.exception;

import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.util.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 异常统一处理
 * @author clark
 * */
@Slf4j
@ControllerAdvice
@ResponseBody
public class MyExceptionHandler {

    /**
     * 业务异常统一封装成200，返回code和message
     *
     * @param e Exception
     * @return Map
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public ResultObject<String> errorHandle(MyException e) {
        if (log.isDebugEnabled()) {
            log.debug("raw message: " + e.getMessage());
        }
        String message = e.getMessage();
        if (null == message){
            message = e.getCause().getMessage();
            if (log.isDebugEnabled()) {
                log.error("new message: {}", message);
            }
        }

        String errMessage = (null == message ?"exception": message.trim());
        log.error(errMessage);

        int errCode = MyErrorEnum.RESPONSE_FUNCTION_ERROR.getCode();
        if (null != message){
            String defMsg = "default message";
            int msgIndex = message.lastIndexOf(defMsg);
            if (0 < msgIndex){
                errMessage = message.substring(msgIndex+defMsg.length()).trim();
                message = errMessage;
            }

            int codeIndexBegin = message.indexOf("@");
            int codeIndexEnd = message.indexOf("@",codeIndexBegin+1);
            //log.info("==errMessage= {} , codeBegin={} codeEnd={}",message,codeIndexBegin,codeIndexEnd);
            if (0 <= codeIndexBegin && 1 < codeIndexEnd){
                String codeStr = message.substring(codeIndexBegin+1,codeIndexEnd);
                Integer code;
                try {
                    code = Integer.valueOf(codeStr);
                }catch (Exception ex){
                    log.error("wrong error code {}",codeStr,ex);
                    code = 0;
                }
                if (0 < code){
                    errMessage = message.substring(codeIndexEnd+1);
                    errCode = code;
                }
            }
        }

        return new ResultObject<>(errCode,errMessage, null);
    }


    /**
     * 参数格式错误，统一回复400
     *
     * @param request httpRequest
     * @param e Exception
     * @return result
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResultObject<String> handleParseException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("{},请求参数错误 {}:", request.getRequestURL(), e.getMessage());
        return new ResultObject<>(400,e.getMessage(),null);
    }

    /**
     * 参数校验错误，统一回复400
     *
     * @param request httpRequest
     * @param e Exception
     * @return result
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultObject<String> handleVerifyException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("{},请求参数错误: {}", request.getRequestURL(), e.getMessage() );
        String errorMessage = e.getMessage();
        BindingResult bindingResult = e.getBindingResult();
        if(null != bindingResult.getFieldError()){
            FieldError fieldError = bindingResult.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            if(null != defaultMessage && !defaultMessage.isEmpty()){
                errorMessage = defaultMessage;
            }

        }

        return new ResultObject<>(MyErrorEnum.PARAM_VERIFY_ERROR.getCode(),MyErrorEnum.PARAM_VERIFY_ERROR.getMsg()+ errorMessage,null);
    }


    /**
     * 参数验证异常统一在此处理，统一回复400
     *
     * @param request httpRequest
     * @param e Exception
     * @return result
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResultObject<String> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("{},系统异常详细信息:{} - {}", request.getRequestURL(),e.getMessage(),e.getConstraintViolations().toString());
        String errorMessage = e.getMessage();
        if (null != e.getConstraintViolations()){
            Iterator iterator = e.getConstraintViolations().iterator();
            while (iterator.hasNext()) {
                ConstraintViolation item=(ConstraintViolation)iterator.next();
                if(null != item && null != item.getMessage()){
                    errorMessage = item.getMessage();
                }
            }

        }

        return new ResultObject<>(400,errorMessage,null);
    }

    /**
     * 非业务异常统一在此处理，统一回复500
     *
     * @param request httpRequest
     * @param e Exception
     * @return result
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultObject<String> handleException(Exception e, HttpServletRequest request) {
        log.error("{},系统异常详细信息:", request.getRequestURL(), e);
        return new ResultObject<>(500,"内部错误",null);
    }
}
