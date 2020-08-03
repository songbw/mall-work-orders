package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.OperaResponse;
import com.fengchao.workorders.bean.ThirdWorkOrderBean;
import com.fengchao.workorders.service.IWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单列表
 */
@RestController
@RequestMapping(value = "/third")
@Slf4j
public class ThirdWorkOrderController {

    @Autowired
    private IWorkOrderService service;


    @PostMapping("workorder")
    public OperaResponse add(@RequestBody ThirdWorkOrderBean bean) {
        return service.syncAdd(bean);
    }


}
