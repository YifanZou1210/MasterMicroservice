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
