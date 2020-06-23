package com.fengchao.workorders.exception;

import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.util.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常统一处理
 * */
@Slf4j
@ControllerAdvice
@ResponseBody
public class MyExceptionHandler {

    /**
     * 业务异常统一封装成400，返回code和message
     *
     * @param e
     * @return Map
     */
    @ResponseBody
    @ExceptionHandler(value = MyException.class)
    public Map<String, Object> errorHandle(Exception e, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
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
            }/*else {
                if (message.contains(":")) {
                    String[] errInfo = message.split(":");
                    errMessage = errInfo[errInfo.length - 1].trim();
                }
            }*/

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

        if (null != e.getCause()) {
            map.put(ResultObject.DATA,e.getCause().getMessage());
        }
        map.put(ResultObject.MESSAGE,errMessage);
        map.put(ResultObject.CODE,errCode);

        response.setStatus(MyErrorEnum.RESPONSE_FUNCTION_ERROR.getCode());
        return map;
    }

    /**
     * 参数格式错误，统一回复400
     *
     * @param request
     * @param e
     * @return
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResultObject<String> handleParseException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("{},请求参数错误:", request.getRequestURL(), e);
        return new ResultObject<>(400,e.getLocalizedMessage(),null);
    }

    /**
     * 非业务异常统一在此处理，统一回复500
     *
     * @param request
     * @param e
     * @return
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultObject<String> handleException(Exception e, HttpServletRequest request) {
        log.error("{},系统异常详细信息:", request.getRequestURL(), e);
        return new ResultObject<>(500,"内部错误",null);
    }
}
