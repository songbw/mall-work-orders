package com.fengchao.workorders.util;

import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.fengchao.workorders.config.SMSConfig;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
@Component
public class SMSUtil {

    /**
     * 自定义短信内容发送
     * @param msg 短信内容
     * @param number 用户手机号
     * @return OK 成功  null 失败
     */
    public static String sendMess(String msg , String number){
        try {
            SmsSingleSender ssender = new SmsSingleSender(SMSConfig.TENT_AppkeyTXAPP_ID, SMSConfig.TENT_AppSecretTXAPP_KEY);
            SmsSingleSenderResult result = ssender.send(0, "86", number,
                    msg, "", "");
            return result.errMsg;
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定模板ＩＤ发送短信
     * @param number 用户手机号
     * @return OK 成功  null 失败
     */
    public static String sendMesModel(String[] params, String number, Integer template){
        try {
            SmsSingleSender ssender = new SmsSingleSender(SMSConfig.TENT_AppkeyTXAPP_ID, SMSConfig.TENT_AppSecretTXAPP_KEY);
            SmsSingleSenderResult result = ssender.sendWithParam("86", number,
                    template, params, null, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            return result.errMsg;
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 群发自定义短信
     * @param msg 短信内容
     * @param numbers 用户手机号数组
     * @return OK 成功 null 失败
     */
    public static String sendMesModel(String msg , String[] numbers){
        try {
            SmsMultiSender msender = new SmsMultiSender(SMSConfig.TENT_AppkeyTXAPP_ID, SMSConfig.TENT_AppSecretTXAPP_KEY);
            SmsMultiSenderResult result =  msender.send(0, "86", numbers,
                    msg, "", "");
            System.out.print(result);
            return result.errMsg;
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定模板ID群发
     * @param numbers 用户手机号数组
     * @return OK 成功 null 失败
     */
    public static String sendMesModel(String[] numbers){
        try {
            String[] params = {"hello" , "1" };
            SmsMultiSender msender = new SmsMultiSender(SMSConfig.TENT_AppkeyTXAPP_ID, SMSConfig.TENT_AppSecretTXAPP_KEY);
            SmsMultiSenderResult result =  msender.sendWithParam("86", numbers,
                    SMSConfig.TENT_TemplateID1, params, "我的钱包", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.print(result);
            return result.errMsg;
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return null;
    }

    public static String getRandom() {
        Random random = new Random();
        String fourRandom = random.nextInt(1000000) + "";
        int randLength = fourRandom.length();
        if (randLength < 6) {
            for (int i = 1; i <= 6 - randLength; i++)
                fourRandom = "0" + fourRandom;
        }
        return fourRandom;
    }


}
