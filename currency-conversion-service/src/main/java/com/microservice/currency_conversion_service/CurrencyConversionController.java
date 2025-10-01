package com.microservice.currency_conversion_service;

import com.microservice.currency_exchange_service.CurrencyExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {
    @Autowired
    private CurrencyExchangeProxy CurrencyExchangeProxy;
//    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
//    public CurrencyConversion calculateConversion(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
//        HashMap<String, String> uriVariables = new HashMap<>();
//        uriVariables.put("from", from);
//        uriVariables.put("to", to);
//        ResponseEntity<CurrencyExchange> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyExchange.class, uriVariables);
//        //! 注意：获取响应实体的body时，必须用getBody()方法远程调用，而不是直接通过module之间share objects
//        CurrencyExchange currencyExchange = responseEntity.getBody();
//        // 添加空值检查
//        if (currencyExchange == null) {
//            throw new RuntimeException("Failed to retrieve currency conversion data");
//        }
//        return new CurrencyConversion(
//                currencyExchange.getId(),
//                from,
//                to,
//                currencyExchange.getExchangeRate(),
//                quantity,
//                quantity.multiply(currencyExchange.getExchangeRate()),
//                currencyExchange.getEnvironment());
//    }
    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateConversionFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
        CurrencyConversion currencyConversion = CurrencyExchangeProxy.retrieveExchangeValue(from, to);
        
        return new CurrencyConversion(
                currencyConversion.getId(),
                from,
                to,
                currencyConversion.getExchangeRate(),
                quantity,
                quantity.multiply(currencyConversion.getExchangeRate()),
                currencyConversion.getEnvironment()+" feign");
    }
}
