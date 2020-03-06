package com.fengchao.workorders.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fengchao.workorders.bean.AoYiRefundResponseBean;
import com.fengchao.workorders.bean.YiYaTongReturnGoodsBean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Clark
 * */

@Slf4j
public class YiYaTong {

    public static final int MERCHANT_ID = 4;

    public static YiYaTongReturnGoodsBean
    parseReturnGoodsComments(String comments){

        JSONObject jsonComments = JSON.parseObject(comments);
        JSONObject json = jsonComments.getJSONObject("logisticsInfo");
        if (null == json) {
            log.error("comments内容错误,找不到 logisticsInfo");
            return null;
        }
        String com = json.getString("com");
        String order = json.getString("order");
        String comCode = json.getString("comCode");
        if (null == com || null == order || null == comCode) {
            log.error("comments内容错误,logisticsInfo中缺失 com,order或comCode");
            return null;
        }
        YiYaTongReturnGoodsBean bean = new YiYaTongReturnGoodsBean();
        bean.setDeliveryCorpName(com);
        bean.setDeliveryCorpSn(comCode);
        bean.setDeliveryCorpSn(comCode);

        return bean;
    }

    public static AoYiRefundResponseBean
    parseRefundReturnResponse(String response){
        if (null == response){
            throw new RuntimeException("420009:怡亚通请求 无回应");
        }
        JSONObject responseJson = JSONObject.parseObject(response);
        /*
 成功返回
{
    "code":0,
    "message":"",
    "data":{
        "serviceStatus": "",        //退款退货单状态
        "serviceStatusName": "",    //退款退货单状态名称
        "orderSn": "1",             //星链子订单sn
        "serviceSn": "1"            //退款订单编号
    }
}

错误返回
{
"code": 50012,
"msg": "服务器已知错误",
"data": null
}
        * */
        Integer code = responseJson.getInteger("code");
        if (null == code){
            throw new RuntimeException("420009:怡亚通请求 失败,code缺失");
        }

        if (200 != code){
            throw new RuntimeException("420009:怡亚通请求 返回错误 "+responseJson.getString("msg"));
        }

        JSONObject respData = responseJson.getJSONObject("data");
        if (null != respData) {
            String serviceSn = respData.getString("serviceSn");
            String orderSn = respData.getString("orderSn");
            String serviceStatus = respData.getString("serviceStatus");
            String serviceStatusName = respData.getString("serviceStatusName");

            AoYiRefundResponseBean bean = new AoYiRefundResponseBean();
            bean.setOrderSn(orderSn);
            bean.setServiceSn(serviceSn);
            bean.setServiceStatus(serviceStatus);
            bean.setServiceStatusName(serviceStatusName);
            return bean;
        }else {
            log.error("怡亚通返回缺失 data");
            return null;
        }

    }
}
