package com.microservice.currency_conversion_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "currency-exchange", url = "localhost:8000")
// name表示在currency-conversion中我们希望调用的服务名，该定义在currency-exchange-service中
// url表示target service url
public interface CurrencyExchangeProxy {
    @GetMapping("currency-exchange/from/{from}/to/{to}")
    public CurrencyConversion retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);
    //直接调用本地bean CurrencyConversion,直接映射exchange to conversion 
}
