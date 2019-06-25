package com.fengchao.workorders.filter;

import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.util.JSONResult;
import com.fengchao.workorders.util.MyErrorMap;
import com.fengchao.workorders.util.StringUtil;
import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.JwtParser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }


    /**
     * 在此方法中检验客户端请求头中的token,
     * 如果存在并合法,就把token中的信息封装到 Authentication 类型的对象中,
     * 最后使用  SecurityContextHolder.getContext().setAuthentication(authentication); 改变或删除当前已经验证的 pricipal
     *
     * @param request http request
     * @param response http response
     * @param chain filter chain
     * @throws IOException exception
     * @throws ServletException exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //System.out.println("doFilterInternal");
        if (request.getRequestURI().contains("workorders")) {//since gateway will check security
            System.out.println("doFilterInternal: access all");
            chain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().contains("/api/swagger-ui.html")) {
            //System.out.println("doFilterInternal: swagger");
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = null;
        try {
            //System.out.println("doFilterInternal: check token");
            authentication = TokenAuthenticationService.getAuthentication(request, response);
            if (null == authentication) {
                //System.out.println("doFilterInternal: token Exp");
                response.setStatus(MyErrorMap.e400.getCode());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(JSONResult.fillErrorString( 400001, "token expired"));
                return;
            }
        } catch (JwtException ex) {
            //System.out.println("doFilterInternal: token Exp");
            response.setStatus(MyErrorMap.e400.getCode());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(JSONResult.fillResultString(400001, "failed", ex.getMessage()));
            return;
        }

        if (null == authentication) {
            System.out.println("doFilterInternal: no token");
            response.setStatus(MyErrorMap.e400.getCode());
            response.setContentType("application/json;charset=UTF-8");
            chain.doFilter(request, response);
            return;
        }
/*
* Token :org.springframework.security.authentication.UsernamePasswordAuthenticationToken@fa79fc8b: Principal: admin; Credentials: [PROTECTED]; Authenticated: true; Details: null; Not granted any authorities
* */

        //System.out.println("Token :" + authentication);
        String userName;
        if (null == authentication.getPrincipal()) {
            System.out.println("doFilterInternal: token no username");
            response.getWriter().println(JSONResult.fillResultString(5000, "failed", "not username"));
            return;
        }
        userName = authentication.getPrincipal().toString();

        System.out.println("AutherFilter: got username :"+userName);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //System.out.println("doFilterInternal: gotContext and do filter");
        chain.doFilter(request, response);

    }
}

