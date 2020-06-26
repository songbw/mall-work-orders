package com.fengchao.workorders.feign.hystric;

import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.AoYiRefundOnlyPostBean;
import com.fengchao.workorders.bean.AoYiRefundReturnPostBean;
import com.fengchao.workorders.bean.YiYaTongReturnGoodsBean;
import com.fengchao.workorders.feign.IAoYiClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class AoYiClientH implements IAoYiClient {

    @Override
    public String postRefundOnly(@RequestBody AoYiRefundOnlyPostBean body){
        return null;
    }

    @Override
    public String postRefundReturn(@RequestBody AoYiRefundReturnPostBean body){
        return null;
    }

    @Override
    public String postReturnGoods(@RequestBody YiYaTongReturnGoodsBean body){
        return null;
    }

    @Override
    public JSONObject getReturnStatus(@RequestParam String serviceSn){
        return null;
    }
}
