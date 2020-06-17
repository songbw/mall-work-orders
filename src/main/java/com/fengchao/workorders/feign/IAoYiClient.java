package com.fengchao.workorders.feign;

import com.fengchao.workorders.bean.AoYiRefundOnlyPostBean;
import com.fengchao.workorders.bean.AoYiRefundReturnPostBean;
import com.fengchao.workorders.bean.YiYaTongReturnGoodsBean;
import com.fengchao.workorders.feign.hystric.AoYiClientH;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "aoyi-client", fallback = AoYiClientH.class)
public interface IAoYiClient {

    /**
     * 向怡亚通发仅退款申请
     * */
    @RequestMapping(value = "/star/orders/apply/refund", method = RequestMethod.POST)
    String postRefundOnly(@RequestBody AoYiRefundOnlyPostBean body);

    /**
     * 向怡亚通发退货退款申请
     * */
    @RequestMapping(value = "/star/orders/apply/refund/goods", method = RequestMethod.POST)
    String postRefundReturn(@RequestBody AoYiRefundReturnPostBean body);

    /**
     * 向怡亚通发退货物流信息
     * */
    @RequestMapping(value = "/star/orders/return/goods", method = RequestMethod.POST)
    String postReturnGoods(@RequestBody YiYaTongReturnGoodsBean body);

}
