package com.example.rest_web_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//* 通常内部开发逻辑问题在自定义异常时都extends RuntimeException,外部问题extends Exception(checked)

// ResponseStatus用于异常class在抛出该异常时自动返回指定status
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        /*
        userNotFoundException -> Runtime -> Exception -> Throwable
        super()是为了继承 detailMessage field和getMessage()方法
        使用如下：
        new userNotFoundException("saved user is not founded")传入的就是message
         */
        super(message);
    }
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);//cause->throwable,可选exception, runtimeException,error
    }
}
