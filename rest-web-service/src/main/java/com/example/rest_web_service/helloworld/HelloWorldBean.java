package com.example.rest_web_service.helloworld;

public class HelloWorldBean {
    private final String message;
    public HelloWorldBean(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "HelloWorldBean{" +
                "message='" + message + '\'' +
                '}';
    }

}
