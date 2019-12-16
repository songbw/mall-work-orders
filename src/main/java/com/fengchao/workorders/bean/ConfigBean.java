package com.fengchao.workorders.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigBean {

    private String iAppId;
    private String apiType;//10：关爱通 11:聚合支付 99:假支付
}
