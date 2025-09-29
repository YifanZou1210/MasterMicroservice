package com.microservice.currency_exchange_service;

import java.math.BigDecimal;

public class CurrencyExchange {
    private long id;
    private String from;
    private String tod;
    private BigDecimal exchangeRate;
    private String environment;

    public CurrencyExchange(long id, String from, String tod, BigDecimal exchangeRate) {
        this.id = id;
        this.from = from;
        this.tod = tod;
        this.exchangeRate = exchangeRate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTod() {
        return tod;
    }

    public void setTod(String tod) {
        this.tod = tod;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
