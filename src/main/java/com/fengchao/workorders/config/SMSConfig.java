package com.fengchao.workorders.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SMSConfig implements InitializingBean {

    @Value("${AppkeyTXAPP_ID}")
    public int appkeyTXAPP_ID;

    @Value("${AppSecretTXAPP_KEY}")
    public  String appSecretTXAPP_KEY;

    @Value("${ActiveTime}")
    public String activeTime;

    @Value("${TemplateID1}")
    public  int templateID1;

    @Value("${TemplateID2}")
    public  int templateID2;

    public static int TENT_AppkeyTXAPP_ID         ;
    public static String TENT_AppSecretTXAPP_KEY     ;
    public static String TENT_ActiveTime ;
    public static int TENT_TemplateID1     ;
    public static int TENT_TemplateID2    ;

    @Override
    public void afterPropertiesSet() throws Exception {
        TENT_AppkeyTXAPP_ID = appkeyTXAPP_ID;
        TENT_AppSecretTXAPP_KEY = appSecretTXAPP_KEY;
        TENT_ActiveTime = activeTime;
        TENT_TemplateID1 = templateID1;
        TENT_TemplateID2 = templateID2;
    }
}
