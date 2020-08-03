package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.OperaResponse;
import com.fengchao.workorders.service.impl.WorkOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单列表
 */
@RestController
@RequestMapping(value = "/third", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Slf4j
public class ThirdWorkOrderController {

    @Autowired
    private WorkOrderServiceImpl service;

    @GetMapping("workorder")
    public OperaResponse getAll(@RequestParam("orderId") Integer orderId) {
        OperaResponse response = new OperaResponse() ;
        response.setData(service.selectWorkOrderByOrderId(orderId));
        return response;
    }


}
