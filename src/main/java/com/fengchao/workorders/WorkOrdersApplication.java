package com.fengchao.workorders;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan({"com.fengchao.workorders.mapper"})
public class WorkOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkOrdersApplication.class, args);
    }

    @LoadBalanced //使用负载均衡机制
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
