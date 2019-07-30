package com.fengchao.workorders.feign.hystric;

import com.fengchao.workorders.bean.QueryOrderBodyBean;
import com.fengchao.workorders.util.OperaResult;
import com.fengchao.workorders.feign.OrderService;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class OrderServiceH implements OrderService {

    @Override
    public OperaResult getOrderList(QueryOrderBodyBean body, Map<String, Object> map) {
        return null;
    }
}

