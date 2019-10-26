package com.fengchao.workorders.feign.hystric;

import com.fengchao.workorders.bean.AggPayRefundBean;
import com.fengchao.workorders.bean.AggPayRefundQueryBean;
import com.fengchao.workorders.feign.IAggPayClient;
import com.fengchao.workorders.util.ResultObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
public class AggPayClient implements IAggPayClient {
    @Override
    public ResultObject<String> postAggPayRefund(@RequestBody AggPayRefundBean body){
        return null;
    }

    @Override
    @RequestMapping(value = "/wspay/query/refund", method = RequestMethod.GET)
    public ResultObject<List<AggPayRefundQueryBean>> getAggPayRefund(@RequestParam String tradeNo){return null;}
}
