# MicroService Notes During Study

## 1. Function of @RestController
### 1. Definition 
@RestController 是 Spring Framework 提供的一个强大的注解，它的主要作用是简化 RESTful Web 服务的开发。它是一个复合注解，等同于同时使用了 @Controller 和 @ResponseBody 这两个注解。

- @Controller: 这个注解将一个 Java 类标识为 Spring MVC 的控制器。它主要负责处理 HTTP 请求，并返回一个视图（View）或数据。
- @ResponseBody: 这个注解告诉 Spring，这个控制器中的所有方法的返回值都应该直接作为 HTTP 响应体（Response Body）发送给客户端，而不是被解析为视图名称。

将这两个注解合二为一，@RestController 的核心作用就是让你的类专注于处理请求并直接返回数据，通常是 JSON 或 XML 格式，这正是 RESTful API 所需要的。

### 2. Composition 
可以理解为`@ResponseBody`和`@Controller`结合的语法糖
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {
    // ...
}
```
从上面的代码可以看出，@RestController 的构成包括：
- @Controller: 赋予这个类作为 Spring MVC 控制器的能力，让 Spring 能够扫描并识别它。
- @ResponseBody: 确保该类中所有方法的返回值都会被自动序列化（Serialization）为 JSON 或 XML，然后直接写入 HTTP 响应体。

### 3. Workflow
当你使用 @RestController 时，Spring Boot 的自动配置会为你做以下几件事：

- HTTP 请求的接收与分发: Spring MVC 会接收到客户端的 HTTP 请求，并根据请求路径、方法（如 GET、POST 等）将请求分发给 RestController 类中相应的方法。
- 方法执行: 你的方法被执行，处理业务逻辑并返回一个 Java 对象（如 String, Map, List 或自定义对象）。
- 自动序列化: @ResponseBody 注解发挥作用，Spring 会使用内置的消息转换器（Message Converters，例如 Jackson 库）将你返回的 Java 对象自动转换为 JSON 字符串。
- 响应返回: 这个 JSON 字符串被作为 HTTP 响应体，连同 Content-Type: application/json 头信息一起返回给客户端。


## 2. DispatcherServlet

`DispatcherServlet` 是 Spring MVC 的核心，它扮演着整个请求处理流程中的“前端控制器”（Front Controller）角色，负责接收所有请求并将其分发到正确的处理器。

下面是一个请求从客户端到数据库，再返回给客户端的完整数据流转过程：


#### **客户端（Client）**

* **请求发起**：客户端发起一个 HTTP 请求（例如 `GET /users/123`）。

#### **DispatcherServlet**

* **请求拦截**：`DispatcherServlet` 拦截所有请求，它是所有请求的统一入口,和api gateway类似。
* **处理器映射（Handler Mapping）**：`DispatcherServlet` 查询**处理器映射器（Handler Mapping）**，根据请求的 URL 找到对应的控制器方法（例如 `UserController` 中的 `getUserById()` 方法）。

#### **控制器（Controller）**

* **接收请求**：请求被分发到 `UserController` 的 `getUserById()` 方法。
* **数据封装**：`@PathVariable` 或 `@RequestParam` 等注解将请求中的参数（如 `123`）自动封装到方法的参数中。
* **调用服务层**：控制器不处理业务逻辑，它将请求传递给**服务层（Service）**。

#### **服务层（Service）**

* **业务处理**：`UserService` 接收到控制器传递来的请求ID。它开始处理业务逻辑，例如验证ID的合法性、进行权限检查等。
* **调用数据访问层**：服务层将请求传递给**数据访问层（Repository）**，以执行数据库操作。

#### **数据访问层（Repository）**

* **数据查询**：`UserRepository` 接收到ID，它负责与数据库进行交互。
* **SQL执行**：它执行底层的数据库操作（例如 `SELECT * FROM users WHERE id = 123`）。

#### **数据库（DB）**

* **数据存储**：数据库根据 SQL 语句，从物理存储中查找并返回数据。

-----

**数据流转（返回路径）**

1.  **数据库 → Repository**

    * 数据库将查询到的原始数据（例如一行记录）返回给 `UserRepository`。

2.  **Repository → Service**

    * `UserRepository` 将原始数据封装成一个 Java 对象（例如 `User` 对象），返回给 `UserService`。

3.  **Service → Controller**

    * `UserService` 完成业务处理后，将 `User` 对象返回给 `UserController`。

4.  **Controller → DispatcherServlet**

    * `@RestController` 注解使得 `UserController` 将 `User` 对象直接返回给 `DispatcherServlet`。

5.  **DispatcherServlet → 客户端**

    * `DispatcherServlet` 利用消息转换器将 `User` 对象序列化成 JSON 格式的字符串。
    * 这个 JSON 字符串被作为 HTTP 响应体，返回给客户端。
    * 客户端接收到 JSON 响应，然后进行解析和展示。

**总结**
这个流程体现了 Spring MVC 经典的**三层架构**：

* **Controller 层**：负责处理请求和响应，是视图/API 的入口。
* **Service 层**：负责核心业务逻辑。
* **Repository 层**：负责数据持久化（数据库访问）。

`DispatcherServlet` 负责将请求从前端传递到后端，并将数据从后端传回到前端，是整个数据流转的调度中心。

## 3. Auto Configuration Error Mapping 
这个答案的意思是，Spring Boot 的“自动配置”（Auto Configuration）功能，正在为你配置错误映射。
- 自动配置：这是 Spring Boot 最强大的特性之一。它会根据你项目中引入的依赖（比如 spring-boot-starter-web），自动为你配置好大部分常见的功能，让你无需手动编写繁琐的配置代码。
- ErrorMvcAutoConfiguration：这是一个特定的自动配置类。当 Spring Boot 检测到你正在开发一个 Web 应用（通过 spring-webmvc 依赖），它就会自动启用 ErrorMvcAutoConfiguration。这个类会为你：
- 注册一个默认的错误页面：当发生错误时，它会显示一个标准的、简单的错误页面（通常是白色的，所以被称为 "Whitelabel Error Page"）。
- 提供一个默认的错误处理逻辑：它会把错误信息（比如状态码、错误消息、异常详情等）作为一个 JSON 响应返回给 API 客户端。

这个问答的意思是，你不需要自己去编写代码来处理基本的错误响应。Spring Boot 已经为你做好了。它通过一个叫做 ErrorMvcAutoConfiguration 的自动配置类，为你提供了默认的错误处理机制。

如果你想自定义错误页面或错误处理逻辑，你可以通过创建自己的 ErrorController 或者其他配置类来覆盖 Spring Boot 的默认行为。

## 4. PathVariable,PathParam
@PathVariable
- 作用： 从 URL 路径本身中提取参数值。这些参数通常是 URL 的一部分，用于唯一标识某个资源。
- 示例： GET /users/123

在这个 URL 中，123 是一个路径变量。

你的方法可以这样写：
```java
@GetMapping("/users/{id}") 
public User getUserById(@PathVariable("id") Long id);
```
@PathVariable 注解将 URL 中的 {id} 的值 123 绑定到方法的 id 参数上。 

用途： 标识符、唯一 ID 等。

@RequestParam
- 作用： 从 URL 的**查询参数（Query Parameter）**中提取值。查询参数位于 URL 的问号 ? 之后，以 key=value 的形式出现。

示例： GET /users?name=Alice
在这个 URL 中，name=Alice 是一个查询参数。
你的方法可以这样写：
```java
@GetMapping("/users") 
public List<User> getUsersByName(@RequestParam("name") String name);
```
@RequestParam 注解将 name 查询参数的值 Alice 绑定到方法的 name 参数上。

用途： 过滤条件、分页、排序等。

## 5. Why POJO use Wrapper Type for fields Design?
```java
public class User {
    private Integer id;
    private String name;
    private LocalDate birthdate;
}
```
那么为什么 User 类的字段多用包装类型（Wrapper Type）而不是基本类型（Primitive Type）

1.1 包装类型可以为 `null`

* **基本类型（primitive types）**：`int`, `double`, `boolean` 等都有默认值，不能为 `null`。
    * 比如 `int age;` 默认值是 `0`，你无法区分这个 `0` 是 **“用户真的年龄是 0”** 还是 **“用户还没设置年龄”**。
* **包装类型（wrapper types）**：`Integer`, `Double`, `Boolean` 可以为 `null`，能表达“值缺失”。
    * 在数据库映射（如 JPA、MyBatis）中，如果某列是 `NULL`，必须映射到包装类型。

1.2 框架兼容性与反射机制
* 很多框架（Spring, Hibernate, Jackson, JPA）依赖 **反射和序列化**，需要用包装类型。
* 比如 JSON 反序列化时：
    * 如果传入 `{"age": null}`，`int` 接受不了，会报错；
    * `Integer` 就可以接收 `null`，更安全。

1.3 泛型限制

* Java 的 **泛型** 不支持基本类型，比如 `List<int>` 是非法的。
* 只能写 `List<Integer>`。
* 所以为了统一代码风格，很多项目里都会把字段写成包装类型。

1.4 与 LocalDate / String 一致性

* `String`, `LocalDate` 本身就是对象类型，不存在 primitive 版本。
* 为了风格统一，Java 开发者更倾向于全用对象类型，而不是有的字段 primitive、有的字段 wrapper。


## 6. Popular Way to Build POJOs

1. 传统 POJO + Getter/Setter

最原始的写法：手写字段、构造函数、getter/setter。

```java
public class User {
    private Integer id;
    private String name;
    private LocalDate birthday;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    // 省略其他 getter/setter
}
```

缺点：样板代码（boilerplate）太多。

2. Lombok（非常流行）

用注解生成 getter/setter/构造器，减少样板代码。

```java
import lombok.Data;
import java.time.LocalDate;

@Data  // 自动生成 getter/setter/toString/hashCode/equals
public class User {
    private Integer id;
    private String name;
    private LocalDate birthday;
}
```

* 行业中 **95% Spring Boot 项目**会引入 Lombok。
* 优点：代码更简洁。
* 缺点：需要 IDE 插件，编译时才生效，对新手有点“魔法感”。


3.  Record（Java 14+ 引入）

如果你的类只是数据载体，可以用 `record`，它是 **不可变对象**（immutable）。

```java
public record User(Integer id, String name, LocalDate birthday) {}
```

* 自动生成构造函数、getter、`toString`、`equals`、`hashCode`。
* 常用于 **DTO（Data Transfer Object）**、API 返回值。
* 缺点：不能有 setter，不适合需要可变字段的实体。


4. Builder 模式

适合字段很多的类，避免复杂构造函数。

```java
public class User {
    private Integer id;
    private String name;
    private LocalDate birthday;

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.birthday = builder.birthday;
    }

    public static class Builder {
        private Integer id;
        private String name;
        private LocalDate birthday;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder birthday(LocalDate birthday) { this.birthday = birthday; return this; }
        public User build() { return new User(this); }
    }
}
```

使用：

```java
User user = new User.Builder()
                 .id(1)
                 .name("Alice")
                 .birthday(LocalDate.of(1995, 5, 20))
                 .build();
```

* 常用于 **大型企业项目**，特别是需要保证对象构造安全时。
* Lombok 也支持 `@Builder` 简化。

## 7. Restful API 最佳实践
### 7.1 POST自定义Response+新建资源URI

以下这行代码是 Spring Boot **在 REST 风格 API 中返回资源 URI 的典型写法**。我给你分解一下：
```java
@PostMapping("/users")
    public User addUser(@RequestBody User user) {
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
        /*
        1. 为什么需要创建一个URI在这里而不是直接返回savedUser?
        2. uri在这里的作用是什么？对client是什么功能？ 
        */
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();

    }
```
---

#### 1. 背景

REST API 中有个最佳实践：

* 当你用 `POST /users` 创建一个新用户时，**除了返回用户对象本身，还应该返回新建资源的 URI**（告诉客户端“你可以通过这个 URL 访问刚创建的资源”）。
* 这个 URI 一般放在响应的 `Location` header 里。

Spring 提供了 `ServletUriComponentsBuilder` 来简化这个过程。

---

#### 2. 代码分解

```java
URI uri = ServletUriComponentsBuilder
              .fromCurrentRequest()       // ① 获取当前请求的 URL即输入browser中的request url，例如: http://localhost:8080/users
              .path("/{id}")              // ② 在 URL 后追加 "/{id}" 占位符
              .buildAndExpand(savedUser.getId())  // ③ 用实际的 user id 替换占位符
              .toUri();                   // ④ 转换成 java.net.URI 对象
```

##### 2.1 `fromCurrentRequest()`

获取当前请求的 URL。
如果当前请求是 `POST /users`，那么基准 URL = `http://localhost:8080/users`

---

##### 2.2 `path("/{id}")`

在基准 URL 后追加路径占位符，变成：
`http://localhost:8080/users/{id}`

---

##### 2.3 `buildAndExpand(savedUser.getId())`

把占位符 `{id}` 替换成新创建用户的 ID。
假设 `savedUser.getId() = 5`，结果就是：
`http://localhost:8080/users/5`
意思是这个uri得到的是这个新创建的单个user的json页面

---

##### 2.4 `toUri()`

把字符串 URL 转成 `java.net.URI` 对象，便于 `ResponseEntity` 或 `Location` header 使用。

---

#### 3. 应用场景

通常配合 `ResponseEntity.created(uri)` 使用：

```java
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    User savedUser = userDaoService.save(user);

    URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

    // 返回 201 Created，并在 Location header 放入新建资源的 URI
    return ResponseEntity.created(uri).body(savedUser);
}
```

##### 返回结果：

* **HTTP Status**: 201 Created
* **Header**: `Location: http://localhost:8080/users/5`
* **Body**: 新建用户对象 JSON

---

#### 4. 延伸知识

1. **为什么要这样做？**

    * 遵循 RESTful API 标准，让客户端知道新建资源的确切位置。
    * 对 API 使用者很友好，不用自己拼接 URL。

2. **其他用法**：

    * `fromCurrentContextPath()` → 只取上下文路径（不带 `/users`），更灵活。
    * `queryParam("sort", "desc")` → 添加查询参数。

3. **实践经验**：
   在企业项目里，这几乎是 Spring REST Controller 中 `POST` 的标准套路。

## 8. Exception
### 8.1 默认super(父函数)目的

#### 1. 总结

在 Java 中，自定义异常类一般会调用 `super(message)`，是因为：

* **继承机制**：你的异常类继承了 `RuntimeException`，而 `RuntimeException` 又继承自 `Exception` → 最终继承自 `Throwable`。
* **核心作用**：`Throwable` 中维护了一个 **异常消息字段**（`detailMessage`），它存储异常的具体说明，便于后续调试、日志记录、错误展示,通过有效的`getMessage()`可以获取否则得到位`null`。
* **如果不调用 `super(message)`**：则 `detailMessage` 会是 `null`，抛出异常时 `getMessage()` 返回 `null`，日志和栈信息缺乏有用描述。

---

#### 2. 详细解释

##### 2.1 异常类的继承链

```text
Throwable
   └── Exception
          └── RuntimeException
                 └── userNotFoundException
```

* 在 `Throwable` 中有一个构造方法：

  ```java
  public Throwable(String message) {
      this.detailMessage = message;
  }
  ```
* 所以当你写 `super(message)` 时，就是把你传进来的字符串交给父类的构造函数，存到 `detailMessage` 里。

---

##### 2.2 为什么要 `super(message)`？

1. **异常信息传递**：
   你抛出异常时：

   ```java
   throw new userNotFoundException("User with id 10 not found");
   ```

   如果调用了 `super(message)`，那么 `e.getMessage()` 就能拿到 `"User with id 10 not found"`。

   **例子**：

   ```java
   try {
       throw new userNotFoundException("User with id 10 not found");
   } catch (userNotFoundException e) {
       System.out.println(e.getMessage());  // 输出：User with id 10 not found
   }
   ```

   如果不写 `super(message)`，输出就是 `null`。

---

2. **日志与调试**：
   日志框架（如 SLF4J, Log4j）或者 Spring Boot 的异常处理机制，都会自动调用 `getMessage()` 打印异常信息。
   没有 `message`，就只会打印栈信息，不利于排错。

---

3. **与父类兼容**：
   Java 中大多数异常类都有类似的构造器。比如：

   ```java
   public RuntimeException(String message) {
       super(message);
   }
   ```

   你的自定义异常继承这种模式，可以让别人使用时更符合习惯。

---

#### 3. 举例对比

##### 不使用 `super(message)`：

```java
public class userNotFoundException extends RuntimeException {
    public userNotFoundException(String message) {
        // 没有super(message)
    }
}

try {
    throw new userNotFoundException("User not found");
} catch (userNotFoundException e) {
    System.out.println(e.getMessage());  // null
}
```

##### 使用 `super(message)`：

```java
public class userNotFoundException extends RuntimeException {
    public userNotFoundException(String message) {
        super(message);
    }
}

try {
    throw new userNotFoundException("User not found");
} catch (userNotFoundException e) {
    System.out.println(e.getMessage());  // User not found
}
```

---

#### 4. 延伸知识

##### 4.1 Checked vs Unchecked Exception

* `Exception`（受检异常，checked exception）：必须在方法签名 `throws` 或者 `try-catch` 中显式处理。例如：`IOException`。
* `RuntimeException`（非受检异常，unchecked exception）：可以不写在方法签名里，常用于程序逻辑错误。例如：`NullPointerException`, `IllegalArgumentException`。

你的 `userNotFoundException` 继承 `RuntimeException`，属于 **业务逻辑错误**，表示查询时没有找到对象。
它是典型的 **domain-specific exception（领域自定义异常）**。

---

##### 4.2 异常的设计思路

* **分层异常**：
  在企业应用里，异常经常会分层次，比如：

    * DAO 层异常（数据访问）
    * Service 层异常（业务逻辑）
    * Controller 层异常（请求/响应）

  自定义异常让每层清晰表达自己的问题。

* **统一异常处理**：
  在 Spring Boot 中，你可能会配合 `@ControllerAdvice` + `@ExceptionHandler` 来集中处理 `userNotFoundException`，返回一个标准化 JSON 响应给前端。

  **例子**：

  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(userNotFoundException.class)
      public ResponseEntity<String> handleUserNotFound(userNotFoundException e) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
      }
  }
  ```

  这样前端拿到的就是：

  ```json
  {
    "error": "User with id 10 not found"
  }
  ```

---

##### 4.3 应用场景

1. **REST API 开发**：表示资源不存在 → HTTP 404。
2. **数据校验**：表示参数不合规 → HTTP 400。
3. **权限控制**：表示没有权限 → HTTP 403。

每个异常类用来描述一种特定错误，配合 `super(message)`，能让日志和前端响应更直观。

### 8.2 MVC统一Exception Design

#### 1. 总结

在典型的 **Spring Boot MVC 架构**中：

1. **异常分层设计**：不同层定义不同语义的异常（DAO、Service、Controller）。
2. **自定义异常类**：比如 `UserNotFoundException`、`InvalidRequestException`。
3. **统一异常处理机制**：利用 `@ControllerAdvice` + `@ExceptionHandler` 进行集中处理。
4. **返回标准化响应**：通常返回一个 JSON，包含 `code`, `message`, `timestamp` 等字段，便于前后端对接。

---

#### 2. 详细解释

##### 2.1 异常的分层思想

* **DAO 层（Repository 层）**：

    * 遇到数据库异常 → 一般直接抛出 Spring Data JPA 或 JDBC 的异常。
* **Service 层**：

    * 捕获 DAO 层异常，转换成业务语义的异常（比如 `UserNotFoundException`）。
* **Controller 层**：

    * 一般不直接写 `try-catch`，而是通过 **全局异常处理器**（`@ControllerAdvice`）来拦截。

---

##### 2.2 示例代码

###### 2.2.1 自定义异常

```java
// 用户未找到异常
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message); // 调用父类构造方法，存储 message
    }
}
```

###### 2.2.2 Service 层抛出异常

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }
}
```

###### 2.2.3 Controller 层调用

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
```

###### 2.2.4 全局异常处理
通常全局Exception通过`@ControllerAdvice`处理，和自定义异常隔离开放在Exception folder中。 并且抛异常 ≠ 返回给前端。

`throw new UserNotFoundException("...")` 只是把异常抛出去了。 如果没有全局处理器，Spring 默认会返回一个 HTML 错误页（Whitelabel Error Page），这对前端/客户端没用。

全局异常处理器的作用：

- 捕获异常（来自所有 controller）。

- 转换成标准化 JSON 响应。

- 映射成合适的 HTTP 状态码（404、400、500 …）。

- 避免将内部堆栈直接暴露给用户（安全风险）。

所以，全局异常处理器是 桥梁：把 Java 内部异常转成前端能消费的统一格式。

```java
@ControllerAdvice  // 表示全局异常处理器
public class GlobalExceptionHandler {

    // 处理自定义异常
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 处理其他未捕获异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
注意这里未捕获的含义：

- 代码中没有 try-catch

- 也没有匹配的 @ExceptionHandler

- 异常一路冒泡到 Spring 框架的最顶层。

- 任何没有被 try-catch 捕获、也没有被方法声明 throws 向上交给调用方处理的异常。

- 无论它是 checked 还是 unchecked，只要一路冒泡到最顶层（JVM 或 Spring DispatcherServlet）都算“未捕获异常”。
     
比如
```java
@GetMapping("/divide")
public int divide() {
    return 10 / 0;  // 会抛 ArithmeticException，但没捕获
}
```
如果没有全局异常处理器 → Spring 会直接返回 500 错误页。
如果有全局处理器 → 就能优雅地包装成 JSON：
```json
{
  "timestamp": "2025-09-26T14:30:10",
  "status": 500,
  "error": "Internal Server Error",
  "message": "/ by zero"
}
```
处理未捕获异常的目的主要是：
- 稳定性：保证 API 永远返回 JSON，而不是一会 JSON、一会 HTML 错误页。

- 可观测性：日志系统可以收集所有异常，便于运维排查。

- 用户体验：前端拿到统一格式，才能做出合理提示（比如弹出“系统繁忙，请稍后再试”）。

##### 500 Internal Error 设计目的
500 = 服务端错误（Server Error）。 当异常不是我们自定义的、没有映射到具体 HTTP 状态（如 404, 400）时，Spring 默认认为是 内部错误。 

比如：空指针 (NullPointerException)、数据库连接失败 (SQLException)。

这些错误 不应该暴露细节，因为：

- 对前端无意义（前端不会处理 NullPointerException）。

- 对安全有风险（暴露堆栈可能给攻击者可乘之机）。

所以，统一把它归类为 Internal Server Error (500)，然后返回一个模糊但标准的信息。

**效果**：
当访问一个不存在的用户 `/users/99` 时，返回的 JSON：

```json
{
  "timestamp": "2025-09-26T11:45:30.123",
  "status": 404,
  "error": "Not Found",
  "message": "User with id 99 not found"
}
```

---

#### 3. 延伸知识

###### 3.1 为什么要统一异常处理？

在 MVC 架构里，异常是跨层的：

* DAO 抛的异常可能是技术性的（SQL 错误、连接超时）。
* Service 需要把这些异常转换成业务语义（比如 "用户不存在"）。
* Controller 负责对接前端，如果每个 Controller 都写一堆 `try-catch`，会很乱。

所以统一异常处理能让 **代码更简洁**、**返回更规范**、**日志更清晰**。

###### 3.2 关联知识点

* **ResponseEntity**：可以灵活设置返回体和 HTTP 状态码。

* **@ResponseStatus**：可以直接在异常类上加注解，自动返回对应状态码。例如：

  ```java
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public class UserNotFoundException extends RuntimeException {
      public UserNotFoundException(String message) {
          super(message);
      }
  }
  ```

  这样就不用写 `@ControllerAdvice` 了，但灵活性比全局处理器差。

* **全局异常 vs 局部异常处理**：

    * `@ExceptionHandler` 写在 `@ControllerAdvice` 里 → 全局处理。
    * `@ExceptionHandler` 写在某个 Controller 里 → 只处理该 Controller 的异常。

* **REST API 设计规范**：
  大型项目里通常会有统一的错误返回格式（Error Response Object），例如：

  ```json
  {
    "code": "USER_NOT_FOUND",
    "message": "User with id 99 not found",
    "timestamp": "2025-09-26T11:45:30.123",
    "path": "/users/99"
  }
  ```

  这样前端能统一处理错误，而不用根据不同接口解析不同格式。

###### 3.3 应用场景

1. **微服务架构**：异常设计必须保证 API 错误返回标准化。
2. **企业级项目**：全局异常处理可以与日志系统（ELK, Splunk）对接。
3. **安全防御**：避免将底层异常（SQL 错误、堆栈信息）直接暴露给前端，降低攻击风险。

### 8.3 microservice处理异常

##### 1. 总结

在微服务架构中：

1. **Client**（浏览器/APP）：只关心统一的 API 错误格式（例如 JSON 格式的错误对象），不会知道底层服务的细节。
2. **API Gateway**：

    * 统一入口，负责聚合服务响应。
    * 异常处理层面，通常负责拦截并转换为统一的错误响应（Error Response Standardization）。
3. **后端分布式服务**（多个微服务）：

    * 每个服务内部有自己的异常体系，但向外暴露时必须转换成通用格式（避免泄露内部堆栈/敏感信息）。
    * 微服务之间调用（RPC/HTTP/gRPC）时，也要定义标准的错误码（Error Codes / gRPC Status）。
4. **统一的 Error Handling Strategy**：

    * 每层都可能出错，但最终必须在 API Gateway 或服务返回给前端时保持 **一致的错误模型**。

---

##### 2. 各层角色

###### 2.1 Client 层（前端 / 移动端）

* **目标**：拿到统一格式的错误响应，方便处理（如弹 Toast、跳转登录页）。
* **做法**：

    * 例如 REST API 返回：

      ```json
      {
        "timestamp": "2025-09-26T11:45:30.123",
        "status": 404,
        "code": "USER_NOT_FOUND",
        "message": "User with id 99 not found",
        "path": "/users/99"
      }
      ```

---

###### 2.2 API Gateway 层（如 Spring Cloud Gateway / Nginx / Kong）

* **职责**：

    1. 拦截后端错误（包括 HTTP 4xx、5xx）。
    2. 转换成统一的 JSON 错误格式。
    3. 可以做全局熔断（fallback），例如当某个服务不可用时返回一个预设错误。

* **例子**（Spring Cloud Gateway 的全局异常处理器）：

  ```java
  @Component
  public class GlobalErrorHandler implements ErrorWebExceptionHandler {

      @Override
      public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
          Map<String, Object> errorAttributes = new HashMap<>();
          errorAttributes.put("timestamp", LocalDateTime.now().toString());
          errorAttributes.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
          errorAttributes.put("message", ex.getMessage());
          errorAttributes.put("path", exchange.getRequest().getPath().value());

          byte[] bytes = new ObjectMapper().writeValueAsBytes(errorAttributes);
          exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
          DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
          return exchange.getResponse().writeWith(Mono.just(buffer));
      }
  }
  ```

---

###### 2.3 后端微服务层

* **内部**：

    * 每个微服务自己维护一套 **业务异常体系**（例如 `UserNotFoundException`, `OrderTimeoutException`）。
    * 使用 `@ControllerAdvice` 或 `@RestControllerAdvice` 进行统一处理。

* **对外**：

    * **不要直接暴露内部堆栈**。
    * 必须转换成标准化错误格式（Error Response Object）。
    * 使用统一的 **错误码（error code）体系**，方便 API Gateway/Client 判断。

* **示例**（后端服务返回统一错误）：

  ```java
  @RestControllerAdvice
  public class GlobalExceptionHandler {

      @ExceptionHandler(UserNotFoundException.class)
      public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
          Map<String, Object> body = new HashMap<>();
          body.put("timestamp", LocalDateTime.now());
          body.put("status", HttpStatus.NOT_FOUND.value());
          body.put("code", "USER_NOT_FOUND");
          body.put("message", ex.getMessage());
          body.put("path", ((ServletWebRequest)request).getRequest().getRequestURI());
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
      }
  }
  ```

---

###### 3. 跨服务调用（Service A → Service B）

* 当 A 调用 B 出错时：

    * B 必须返回标准化错误响应（HTTP + JSON）。
    * A 捕获到后，不直接把 B 的原始错误丢给 Client，而是做二次封装：

        * 比如 A 是 `OrderService`，它调用 B 的 `UserService`。如果用户不存在，B 返回 `"USER_NOT_FOUND"`，A 可以翻译为 `"ORDER_FAILED_USER_NOT_EXIST"` 再传给 Client。

* 在 gRPC 微服务里：

    * 使用 `Status` 和 `StatusRuntimeException` 作为标准错误格式。
    * 错误信息通过 metadata（trailer）传递。

###### 4. 扩展知识

###### 4.1 为什么微服务必须有统一错误标准？

* **单体应用**里，异常只需要在 Controller 层统一处理就行。
* **微服务架构**下，异常可能跨越：Client → Gateway → ServiceA → ServiceB → Database。
* 如果没有统一的错误格式：

    * Gateway 不知道如何解析后端错误。
    * Client 也要写一堆解析逻辑，增加耦合。

###### 4.2 常见设计模式

1. **统一错误码体系**：

    * 错误码一般分层级，如：

        * `USER_NOT_FOUND` (业务错误)
        * `AUTH_UNAUTHORIZED` (权限错误)
        * `SYS_INTERNAL_ERROR` (系统错误)
    * 企业里经常会有一个 `ErrorCode` 枚举类，全服务共享。

2. **网关层错误封装**：

    * 避免后端服务直接把 `SQLException` 暴露给前端。
    * 统一替换成标准 JSON。

3. **链路追踪（Distributed Tracing）**：

    * 错误返回时加上 `traceId`（如 Sleuth/Zipkin），便于日志追踪整个调用链路。

4. **熔断 & 降级**：

    * 如果某个服务宕机，Gateway 可以返回 `"SERVICE_UNAVAILABLE"`，同时走 fallback（例如返回缓存数据）。

###### 4.3 应用场景

* **金融系统**：跨多个服务，必须保证错误可追踪（审计要求）。
* **电商平台**：下单调用库存、支付、物流 → 任何一环出错必须有标准错误返回，否则前端无法处理。
* **B2B API**：对外暴露的 API，如果没有统一错误模型，合作方集成会非常痛苦。

### 8.4 遇到Exception思想流程
#### 1. 我的部分疑惑问题：
细化如何思考遇到异常的时候的处理思想流程 以及有部分问题我还是非常疑惑
1. 我需要考虑到时候使用异常处理时候用try catch,throws还是注解吗，如何trade off?

    1. try-catch：适合能就地解决的异常，避免上抛。 比如配置文件不存在 → 加载默认配置。 
   
    2. throws：适合当前方法无法决定如何处理时，把责任交给调用方。比如DAO 抛 SQLException，由 Service 决定是否转为 UserNotFoundException。

    3. 注解（@ExceptionHandler / @ControllerAdvice）：如果try-catch, throws都无法解决，则用注解全局兜底给全局异常Controller, 统一对外暴露格式。比如所有 RuntimeException → HTTP 500 + 错误 JSON。

2. 我需要考虑是checked, unchecked, customized吗，然后对他们进行分类？
    对于如何决定当前layer是哪一种异常的设计，我在下面详细列出设计的解释和做法
3. super class subclass of exception之间内部逻辑如何设计，为什么这样设计
   1. 扩展性：super class定义统一接口和fields保证基类异常可用，subclass在基础上扩展细节方法
   
   2. 复用性：父子类之间重复出现的异常可以直接继承

4. 如果有没设计到的异常难道每次都要逐步添加吗，不麻烦吗，还是说需要预判？
   这是现实中的常见痛点。思路：

   1. 预判常见业务异常：

      - 用户模块：用户不存在、用户已冻结、权限不足。

      - 订单模块：库存不足、订单过期、支付失败。

      - 这些要提前在设计阶段规划。

   2. 兜底方案：

      - 未预判的异常，最终会落到全局异常处理器，返回 500 Internal Server Error。

      - 日志中完整记录，后续再评估是否需要引入新的自定义异常。

   3. 动态演进：

      - 不可能一次性设计出“完美异常系统”，一般是迭代中不断完善。

      - 好的设计是“允许兜底 + 容易扩展”，而不是“一次性写死”。

---

#### 2. 设计中checked unchecked异常判断
一般来说设计准则是：
- 开发阶段：你要清楚异常类型的来源（checked or unchecked）。

- 设计阶段：你主要关心“异常语义”，因此更倾向于用 unchecked + 自定义异常。

- 原因：业务逻辑异常如果全是 checked（强制写 try-catch），会让代码充满样板；而 unchecked 更灵活，可以依赖全局处理器统一兜底。

所以实际项目里常见做法：

- 底层（IO、网络） → Checked → 向上抛出。

- 中间层（Service） → 包装成 Unchecked + 自定义异常（如 UserNotFoundException）。

- 控制器层 → 全局异常处理器统一捕获。

#### 1. 为什么底层用 **checked 向上抛出**

1. **底层面对外部世界**：文件、网络、数据库，错误是常见的（文件不存在、网络断开、数据库超时）。
2. **底层没能力决定业务语义**：DAO 碰到 `SQLException`，它不知道是要提示“订单不存在”还是“系统维护中”。
3. **所以只能 throws**：DAO 只能 `throws SQLException`，让上层业务来解释。
4. checked其实是要求开发可以提前预判风险，显式预处理外部异常，要么try catch在本层出现外部联系的位置处理，要么throws给调用方处理，要么包装成业务逻辑错误。如果抛出的话，调用者的处理方法是：如果本层可以处理-try catch,无法处理则继续throws,checked exception extends 某个具体的exception的时候，这个类型调用者会在try catch的时候使用到，如果可以匹配到捕获类型则继续catch
5. unchecked异常通常是内部逻辑异常也就是我们开发中大部分的异常，通常不try catch否则会导致大量赘余代码，最佳实践是用全局controller兜底，重要异常用自定义处理器`extends RuntimeException`

```java
// DAO 层
public User findUserById(Long id) throws SQLException {
    // JDBC 操作
}
```

---

#### 2. 为什么 Service 层要 **包装成 unchecked + 自定义**

1. **语义转换**：DAO 抛的是技术细节（SQLException），业务要抛的是“用户不存在”这种业务语义。
2. **为什么用 unchecked**：

    * 如果用 checked，会强制 Controller 也写 try-catch，代码臃肿。
    * 用 unchecked，可以直接交给全局异常处理器统一处理，Controller 不需要关心。

```java
// Service 层
public User getUser(Long id) {
    try {
        return userDao.findUserById(id);
    } catch (SQLException e) {
        throw new UserNotFoundException("用户不存在: " + id);
    }
}
```

---

#### 3. 什么时候 try-catch，什么时候 throws？

* **能在当前层修复/降级 → try-catch**

    * 配置文件读取失败 → 用默认配置。
    * 远程调用失败 → 用缓存兜底。
* **自己不能修复，但上层能处理 → throws**

    * DAO 抛 SQLException → Service 决定转成业务异常。
* **没人能修复 → unchecked，交给全局异常处理器**

    * Service 抛 UserNotFoundException → Controller 不管，全局异常处理器转成 JSON。

---

#### 4. 总结口诀

* **能修复 → try-catch**
* **不能修复，上层能决策 → throws**
* **没人能修复 → RuntimeException（交给全局异常处理器）**
