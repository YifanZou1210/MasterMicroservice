package com.microservice.limits_service;

import com.microservice.limits_service.bean.Limits;
import com.microservice.limits_service.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LimitsController {
    @Autowired
    private Configuration configuration;


    @GetMapping("/limits")
    public Limits getAllLimits() {
        // 动态获得本地配置的参数
        return new Limits(configuration.getMinimum(), configuration.getMaximum());
    }
}
