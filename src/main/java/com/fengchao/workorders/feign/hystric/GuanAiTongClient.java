package com.fengchao.workorders.feign.hystric;

import com.fengchao.workorders.bean.GuanAiTongRefundBean;
import com.fengchao.workorders.feign.IGuanAiTongClient;
import com.fengchao.workorders.util.ResultObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class GuanAiTongClient implements IGuanAiTongClient {

    @Override
    public ResultObject<String> postRefund(@RequestBody GuanAiTongRefundBean body) {

        return null;
    }
}
