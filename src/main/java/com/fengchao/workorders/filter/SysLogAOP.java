package com.fengchao.workorders.filter;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
//@Component
//@Aspect
//@Order(1)
public class SysLogAOP {
    //private static final Logger log = LoggerFactory.getLogger(SysLogAOP.class);

    @Around("@within(org.springframework.web.bind.annotation.RequestMapping)")
    public Object recordLog(ProceedingJoinPoint p) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Object httpData;

        long t1 = System.currentTimeMillis();
        try {
            httpData = p.proceed();
        } catch (Exception e) {
                throw new Exception(e);
        }

        long t2 = System.currentTimeMillis();
        if (request.getRequestURL().toString().contains("swagger")) {
            return httpData;
        }
        /*
        if (null != httpData) {
            if (httpData.toString().length() < 5000) {
                sysLog.setResult(httpData.toString());
            } else {
                sysLog.setResult("data is too long");
            }
        }


        if (null != request.getUserPrincipal() && null != request.getUserPrincipal().getName()) {
            sysLog.setUser(request.getUserPrincipal().getName());
        }
        sysLog.setDuration((t2 - t1));
        sysLog.setMethod(request.getMethod()+":"+p.getTarget().getClass().getName() + "." + p.getSignature().getName());
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : request.getParameterMap().keySet()) {
            if (stringBuilder.length() > 1) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append(s);
            stringBuilder.append(" = ");
            stringBuilder.append(request.getParameterMap().get(s)[0]);
        }
        sysLog.setParam(stringBuilder.toString());
        sysLog.setIp(request.getRemoteAddr());
        sysLog.setUrl(request.getRequestURL().toString());
        sysLog.setUserAgent(request.getHeader("user-agent"));
        //systemService.insertSysControllerLog(sysLog);
*/
        //log.info("request contentType:{}", request.getHeader("Accept"));
        //log.info("request param : {}", sysLog.getParam());
        log.info("request method : {}", request.getMethod());
        log.info("request url : {}", request.getRequestURL().toString());
        log.info("request IP : {}",request.getRemoteAddr());
        log.info("user-agent : {}",request.getHeader("user-agent"));
        log.info("request duration : {}ms",(t2-t1));
        //log.info("request result : {}",sysLog.getResult());
        return httpData;
    }

}
