package com.microservice.limits_service.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("limits-service")
//springboot扫描后在本地config中找对应前缀limits-service映射到class field中,通过autowired注入+getter获取绑定值
//Configuration的setter,getter用于注入参数，和limits中不同
@Component
public class Configuration {
    private int minimum;
    private int maximum;
    public Configuration() {
        super();
    }
    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }
}
