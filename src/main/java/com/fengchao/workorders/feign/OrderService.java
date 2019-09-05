package com.fengchao.workorders.feign;

import com.fengchao.workorders.bean.QueryOrderBodyBean;
import com.fengchao.workorders.feign.hystric.OrderServiceH;
import com.fengchao.workorders.util.OperaResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "order", fallback = OrderServiceH.class)
public interface OrderService {

    @RequestMapping(value = "/order/searchOrder", method = RequestMethod.POST)
    OperaResult getOrderList(@RequestBody QueryOrderBodyBean body, @RequestHeader Map<String, Object> headers);


}

