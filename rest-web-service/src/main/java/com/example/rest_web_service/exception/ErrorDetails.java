package com.example.rest_web_service.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ErrorDetails {
    private String message;
    private LocalDateTime timestamp;
    private String details;
    public ErrorDetails(LocalDateTime timestamp,String message,String details) {
        // ? 为什么没有任何inheritance但是有super()
        // * 表示无参构造函数， 在有继承关系的时候，父类对象必须首先构造，如果父类只有无参构造的话子类构造函数会自动插入super(),如果父类存在有参构造则必须显式用super(arg）表示父类首先在子类中构建然后才是子类对象
        //下方的super()可以省略
        super();
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
