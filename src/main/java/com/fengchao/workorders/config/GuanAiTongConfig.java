package com.fengchao.workorders.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GuanAiTongConfig implements InitializingBean {

    @Value("${GAT_NOTIFY_URL}")
    public String GAT_NOTIFY_URL;

    @Value("${GAT_I_APP_ID}")
    public String GAT_I_APP_ID;

    private static String CONFIG_GAT_NOTIFY_URL;
    private static String CONFIG_GAT_I_APP_ID;

    @Override
    public void afterPropertiesSet() {
        CONFIG_GAT_NOTIFY_URL = GAT_NOTIFY_URL;
        CONFIG_GAT_I_APP_ID = GAT_I_APP_ID;
        log.info("==== get guan_ai_tong config notify_url={}, iappId={}",
                CONFIG_GAT_NOTIFY_URL, CONFIG_GAT_I_APP_ID);
    }

    public static String getConfigGatNotifyUrl() {
        return CONFIG_GAT_NOTIFY_URL;
    }

    public static String getConfigGatIAppId() {
        return CONFIG_GAT_I_APP_ID;
    }

}

