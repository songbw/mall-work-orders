package com.fengchao.workorders.security;

import com.fengchao.workorders.util.StringUtil;
//import com.fengchao.workorders.service.TokenAuthenticationService;
//import com.fengchao.workorders.service.impl.UserDetailsServiceImpl;

//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.fengchao.workorders.util.JSONResult;
//import com.fengchao.workorders.util.RedisUtil;
//import org.apache.http.HttpException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

//import java.io.IOException;


public class TokenClearLogoutHandler implements LogoutHandler {

    //@Autowired
    //private UserDetailsServiceImpl userDetailsService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication)
             {
        System.out.println("LogoutHandler: logout, token: " + authentication);

        //SecurityContextHolder.clearContext();

        if (null == authentication || null == authentication.getPrincipal()) {
            System.out.println("LogoutHandler: no token");
            return;
        }
        String username = authentication.getPrincipal().toString();
        if (null == username || username.isEmpty()) {
            System.out.println("LogoutHandler: wrong token");
            return;
        }
        System.out.println("LogoutHandler: logout user: " + username);
        StringUtil.deleteToken(username);

        //UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, authorities);

        //authenticate(authenticationToken);

    }

}

