package com.fengchao.workorders.config;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.bean.ConfigBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConfigurationProperties("refund")
@Component
@Slf4j
@Getter
@Setter
public class RefundConfig {

    List<ConfigBean> ids;

    @PostConstruct//在servlet初始化的时候加载，并且只加载一次，和构造代码块的作用类似
    private void init() throws Exception{
        String _func = "初始化退款配置项";
        log.info(_func);

        if (null == ids || 0 == ids.size()){
            String msg = "configuration not found";
            log.error("{} {}",_func,msg);
            throw new Exception(msg);
        }

        log.info("{} {}", _func,JSON.toJSONString(ids));

    }


    public ConfigBean getConfig(String iAppId) {
        String _func = "获取配置项: ";
        if (null == iAppId || iAppId.isEmpty()){
            log.error("{} iAppId 缺失",_func);
            return null;
        }


        if (null == this.ids || 0 == this.ids.size()){
            log.error("{} 没有发现任何配置项",_func);
            return null;
        }

        for (ConfigBean b: ids){
            if (b.getIAppId().equals(iAppId)){
                log.info("{} {}",_func,JSON.toJSONString(b));
                return b;
            }
        }

        log.error("{} 没有发现iAppId={} 的配置项",_func,iAppId);
        return null;
    }


}
