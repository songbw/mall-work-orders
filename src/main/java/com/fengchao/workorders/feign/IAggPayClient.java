package com.fengchao.workorders.feign;

import com.fengchao.workorders.bean.AggPayRefundBean;
import com.fengchao.workorders.bean.AggPayRefundQueryBean;
import com.fengchao.workorders.feign.hystric.AggPayClient;
import com.fengchao.workorders.util.ResultMessage;
import com.fengchao.workorders.util.ResultObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "aggpay", fallback = AggPayClient.class)
public interface IAggPayClient {
    @RequestMapping(value = "/wspay/refund", method = RequestMethod.POST)
    ResultMessage<String> postAggPayRefund(@RequestBody AggPayRefundBean body);

    @RequestMapping(value = "/wspay/query/refund", method = RequestMethod.GET)
    ResultMessage<List<AggPayRefundQueryBean>> getAggPayRefund(@RequestParam String outRefundNo);

    @RequestMapping(value = "/wspay/batch/query/refund", method = RequestMethod.GET)
    ResultMessage<Map<String,List<AggPayRefundQueryBean>>> getBatchAggPayRefund(@RequestParam String outRefundNos);

}
