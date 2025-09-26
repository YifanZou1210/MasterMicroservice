package com.example.rest_web_service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
public class userResource {
    private final userDaoService userDaoService;

    public userResource(userDaoService userDaoService) {
        this.userDaoService = userDaoService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userDaoService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) {
        return userDaoService.findUserById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        /**
         * 前端传入数据user JSON如下
         * {
         *     "name":"Alice",
         *     "birthDate":"2000-12-10"
         * }
         * 会反序列化为java对象后自动通过dao存入db
         */
        // 一般最好return response status of created而不是void
//        return userDaoService.saveUser(user);
        User savedUser = userDaoService.saveUser(user);
        // 构建new user自己JSON的url，比如localhost:8080/users/4等等
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        // created()返回ResponseEntity.BodyBuilder对象，在response headers中自动加入Location: uri和HTTP/1.1 201 created
        // body()在bodyBuilder上调用，把savedUser序列化为JSON放入response body中
        // 一般返回的是一个ResponseEntity<T>包含headers,status, body
        return ResponseEntity.created(uri).body(savedUser);
    }
}
