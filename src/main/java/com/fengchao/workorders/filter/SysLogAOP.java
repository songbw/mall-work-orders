package com.fengchao.workorders.filter;

import com.fengchao.workorders.model.SysLog;
import com.fengchao.workorders.service.impl.SystemServiceImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Order(1)
public class SysLogAOP {
    private static final Logger logger = LoggerFactory.getLogger(SysLogAOP.class);

    private SystemServiceImpl systemService;

    @Autowired
    SysLogAOP(SystemServiceImpl systemService) {
        this.systemService = systemService;
    }

    @Around("@within(org.springframework.web.bind.annotation.RequestMapping)")
    public Object recordLog(ProceedingJoinPoint p) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Object httpData;
        SysLog log = new SysLog();

        long t1 = System.currentTimeMillis();
        try {
            httpData = p.proceed();
        } catch (Exception e) {//这里建议将异常向上层抛让异常处理器来进行捕捉
                throw new Exception(e);
        }

        long t2 = System.currentTimeMillis();

        if (null != httpData) {
            if (httpData.toString().length() < 5000) {
                log.setResult(httpData.toString());
            } else {
                log.setResult("data is too long");
            }
        }
        if (request.getRequestURL().toString().contains("swagger")) {
            return httpData;
        }

        if (null != request.getUserPrincipal() && null != request.getUserPrincipal().getName()) {
            log.setUser(request.getUserPrincipal().getName());
        }
        log.setDuration((t2 - t1));
        log.setMethod(request.getMethod()+":"+p.getTarget().getClass().getName() + "." + p.getSignature().getName());
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : request.getParameterMap().keySet()) {
            if (stringBuilder.length() > 1) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append(s);
            stringBuilder.append(" = ");
            stringBuilder.append(request.getParameterMap().get(s)[0]);
        }
        log.setParam(stringBuilder.toString());
        log.setIp(request.getRemoteAddr());
        log.setUrl(request.getRequestURL().toString());
        log.setUserAgent(request.getHeader("user-agent"));
        //systemService.insertSysControllerLog(log);

        logger.info("request contentType:{}", request.getHeader("Accept"));
        logger.info("request param : {}", log.getParam());
        logger.info("reuest method : {}", request.getMethod());
        logger.info("request url : {}", log.getUrl());
        logger.info("request IP : {}",log.getIp());
        logger.info("user-agent : {}",log.getUserAgent());
        logger.info("request duration : {}ms",log.getDuration());
        logger.info("request result : {}",log.getResult());
        return httpData;
    }

}
