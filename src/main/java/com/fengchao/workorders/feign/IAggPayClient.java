package com.fengchao.workorders.feign;

import com.fengchao.workorders.bean.AggPayRefundBean;
import com.fengchao.workorders.bean.AggPayRefundQueryBean;
import com.fengchao.workorders.feign.hystric.AggPayClient;
import com.fengchao.workorders.util.ResultObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "aggpay", fallback = AggPayClient.class)
public interface IAggPayClient {
    @RequestMapping(value = "/wspay/refund", method = RequestMethod.POST)
    ResultObject<String> postAggPayRefund(@RequestBody AggPayRefundBean body);

    @RequestMapping(value = "/wspay/query/refund", method = RequestMethod.GET)
    ResultObject<List<AggPayRefundQueryBean>> getAggPayRefund(@RequestParam String tradeNo);
}
