package com.example.rest_web_service.user;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class userDaoService {
    // static将user List作为共享数据源
    private final static List<User> users = new ArrayList<>();
    // static block是第一次加载到jvm时执行的代码，只执行一次，对static field初始化
    // 这里是用static list模拟数据库表
    private static int usercnt = 0;
    static {
        users.add(new User(++usercnt, "alice", LocalDate.now().minusYears(1)));
        users.add(new User(++usercnt, "alice1", LocalDate.now().minusYears(2)));
        users.add(new User(++usercnt, "alice2", LocalDate.now().minusYears(3)));
    }
    public List<User> findAllUsers() {
        return users;
    }
    public User findUserById(Integer id) {
//  forloop搜索对应id的user
//        for (User user : users) {
//            if (Objects.equals(user.getId(), id)) {
//                return user;
//            }
//        }
//        return null;
        return users.stream().filter(i->i.getId().equals(id)).findFirst().orElse(null);
        // 1. users.stream() 将List<User> -> Stream<User>,并不立即执行，只是一个可以遍历user元素的流水线
        // 2. .filer(i->i.getId().equals(id)) 只保留ID等于id的Stream<User>否则是空Stream,i是每个User元素,返回true的User会保留下来
        // 3. findFirst() 找到第一个匹配的Stream<User>的元素，返回的是Optional<User>,作为terminal method,结束stream的遍历，如果没有匹配项则返回Optional.empty()
        // 4. orElse(null) 如果Optional为空返回null否则返回找到的User,Optional API
    }
    public User saveUser(User user){
        // saveUser肯定会传入new User,设定自增index后放入users list
        user.setId((++usercnt));//这里是共享id，无论从哪个service或者url加入新的user都不会打乱index
        users.add(user);
        return user;
    }
}
