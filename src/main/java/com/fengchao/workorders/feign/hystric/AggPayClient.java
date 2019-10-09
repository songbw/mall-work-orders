package com.fengchao.workorders.feign.hystric;

import com.fengchao.workorders.bean.AggPayRefundBean;
import com.fengchao.workorders.feign.IAggPayClient;
import com.fengchao.workorders.util.ResultObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AggPayClient implements IAggPayClient {
    @Override
    public ResultObject<String> postAggPayRefund(@RequestBody AggPayRefundBean body){
        return null;
    }
}
