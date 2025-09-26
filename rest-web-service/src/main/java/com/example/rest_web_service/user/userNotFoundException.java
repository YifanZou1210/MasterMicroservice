package com.example.rest_web_service.user;

public class userNotFoundException extends RuntimeException {
    public userNotFoundException(String message) {
        /*
        userNotFoundException -> Runtime -> Exception -> Throwable
        super()是为了继承 detailMessage field和getMessage()方法
        使用如下：
        new userNotFoundException("saved user is not founded")传入的就是message
         */
        super(message);
    }
}
