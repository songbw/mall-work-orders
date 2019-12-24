package com.fengchao.workorders.feign;

import com.fengchao.workorders.bean.GuanAiTongRefundBean;
import com.fengchao.workorders.feign.hystric.GuanAiTongClient;
//import com.fengchao.workorders.util.OperaResult;
import com.fengchao.workorders.util.ResultObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "guanaitong-client", fallback = GuanAiTongClient.class)
public interface IGuanAiTongClient {

    @RequestMapping(value = "seller/pay/syncRefund", method = RequestMethod.POST)
    ResultObject<String> postRefund(@RequestBody GuanAiTongRefundBean body);

}
