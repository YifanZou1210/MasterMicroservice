package com.example.rest_web_service.helloworld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//rest api
@RestController
public class HelloWorld {
// http request映射到controller的处理方法,method=..只处理get request,path匹配路径
// 只要client存在http get请求访问/hello-word,就会派发该方法返回hello-world
    @RequestMapping(method = RequestMethod.GET, path = "/hello-world")
//    @GetMapping(path = "hello-world")
    public String helloWorld(){
        return "Hello World!";
    }

    @GetMapping("/hello-world-bean")
    public String helloWorldBean(){
        return new HelloWorldBean("hello-world").toString();
    }
}
