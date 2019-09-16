package com.fengchao.workorders.config;

import com.fengchao.workorders.filter.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,jsr250Enabled = true,prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
/*
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService detailsService() {
        return new UserDetailsServiceImpl();
    }

*/
   // @Autowired
  //  private BCryptPasswordEncoder bCryptPasswordEncoder;
    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(detailsService()) // 用户认证
        //        .passwordEncoder(passwordEncoder()); // 使用加密验证
        auth.authenticationProvider(new CustomAuthenticationProvider(userDetailsService, new BCryptPasswordEncoder()));
    }
*/
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/config/**", "/css/**", "/fonts/**", "/img/**", "/js/**","/v2/api-docs","/webjars/**","/swagger-resources/**","/swagger-ui.html","/workorders/**","/actuator/**");
    }
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().disable()
                .and().authorizeRequests()
                .antMatchers("/workorders/registry").permitAll() // 注册请求不需要验证
                .antMatchers("/workorders/sign_up").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/workorders/sign_in")
                .loginProcessingUrl("/workorders/login").defaultSuccessUrl("/workorders/personal_center",true)
                .failureUrl("/workorders/sign_in?error").permitAll()
                .and().logout().logoutSuccessUrl("/workorders/sign_in").permitAll()
                .and().csrf().disable();
    }
*/
/* it seems that springboot 2.1.4 need not it anymore
    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler(){
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }
*/
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String[] methods = "POST,PUT,GET,DELETE,OPTIONS".split(",");
        String[] headers = "Origin,X-Requested-With,Content-Type,Accept,Accept-Encoding,Accept-Language,Host,Referer,Connection,User-Agent,Authorization".split(",");

        List<String> allowedOrigins = new ArrayList<>();
        List<String> allowedMethods = new ArrayList<>();
        List<String> allowedHeaders = new ArrayList<>();

        allowedOrigins.add("*");

        for(String s: methods) {
            allowedMethods.add(s);
        }
        for (String ss: headers) {
            allowedHeaders.add(ss);
        }

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(allowedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/swagger-ui**").permitAll()
                    .antMatchers("/v2/api-docs").permitAll()
                    .antMatchers("/actuator").permitAll()
                    .antMatchers(HttpMethod.OPTIONS).permitAll()
                    .anyRequest().authenticated()
                    .anyRequest().permitAll()
                .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .sessionManagement().disable()
                //    .cors()
                //.and()
                   // .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                   // new Header("Access-control-Allow-Origin","*"),
                   // new Header("Cache-Control", "no-store"),
                   // new Header("Access-Control-Expose-Headers","Authorization"))))
                //.and()
                //    .formLogin().loginPage("/workorders/login");
                    //.logout().logoutUrl("/securityLogout")
                    //.addLogoutHandler(tokenClearLogoutHandler())
                    //.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                    //.logoutSuccessHandler(new CustomLogoutSuccessHandler())
                //.and()
                    .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class);
                //验证token
                    //.addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    //@Bean
    //protected TokenClearLogoutHandler tokenClearLogoutHandler() {
     //   return new TokenClearLogoutHandler();
    //}

}


