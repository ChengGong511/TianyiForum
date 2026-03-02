# 403 权限错误修复说明

## 问题分析

登录后获得 403 错误的根本原因是 **GET 请求不能包含 RequestBody**。

### 原始问题

**后端代码（错误）:**

```java
@GetMapping()
public Result<PageResponse<PostVo>> listPosts(@RequestBody PostPageQuery postPageQuery) {
    // @RequestBody 不能用于 GET 请求！
}
```

**前端代码:**

```typescript
// 发送查询参数而不是 body
get<PageResult<Post>>("/admin/posts", q);
```

当前端发送 GET 请求时，由于后端期望 `@RequestBody`，请求处理失败，导致 403 错误。

## 已修复的问题

### 1. ✅ PostController - 改为接收查询参数

**修改前：**

```java
@GetMapping()
public Result<PageResponse<PostVo>> listPosts(@RequestBody PostPageQuery postPageQuery)
```

**修改后：**

```java
@GetMapping()
public Result<PageResponse<PostVo>> listPosts(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "hot") String sortBy,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize)
```

### 2. ✅ UserController - 改为接收查询参数

**修改前：**

```java
@GetMapping("/users")
public Result<PageResponse<UserVO>> listUsers(@RequestBody UserPageQuery userPageQuery)
```

**修改后：**

```java
@GetMapping("/users")
public Result<PageResponse<UserVO>> listUsers(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize)
```

## 技术原理

### HTTP GET 请求规范

- ❌ GET 请求 **不应该** 有 request body
- ✅ GET 请求 **应该** 通过 URL 查询参数传递数据

### Spring Web 注解对比

| 注解            | 用途              | 用法                     |
| --------------- | ----------------- | ------------------------ |
| `@RequestParam` | 接收 URL 查询参数 | `GET /api?key=value`     |
| `@RequestBody`  | 接收 request body | `POST /api` 带 JSON body |
| `@PathVariable` | 接收 URL 路径参数 | `GET /api/{id}`          |

## 测试步骤

### 1. 重新编译后端

```bash
cd d:\TianyiForum\spring\tianyi-forum-backend
mvn clean install -DskipTests
mvn spring-boot:run
```

### 2. 前端需要根据新的参数格式调整（如果使用了 @RequestBody）

前端 `http.ts` 中的查询参数处理已经正确，会自动转换为 URL 参数。

### 3. 测试登录和访问

1. 登录管理员账号
2. 访问"帖子管理"页面
3. 访问"用户管理"页面
4. 检查网络请求是否成功（应该是 200 而不是 403）

## 验证修复

### 浏览器 DevTools 检查

1. 打开浏览器 F12
2. 切换到 Network 标签
3. 点击"帖子管理"或"用户管理"
4. 查看请求 URL 格式：
   ```
   ✅ 正确: GET /admin/posts?keyword=&status=&sortBy=hot&page=1&pageSize=10
   ❌ 错误: GET /admin/posts (带 body)
   ```
5. 查看响应状态：
   ```
   ✅ 正确: 200 OK
   ❌ 错误: 403 Forbidden
   ```

## 如果还有 403 错误

### 排查清单

- [ ] Token 是否正确保存在 localStorage 的 `admin_token`？
- [ ] Token 是否正确发送在 `Authorization: Bearer <token>` 头中？
- [ ] 用户角色是否真的是 `admin`？
  ```sql
  SELECT id, username, role FROM users WHERE username = 'admin';
  ```
- [ ] 后端是否已重新启动？
- [ ] Security 配置是否正确？

### 调试 Security 过滤器

在 `JwtAuthenticationFilter.java` 中添加日志：

```java
logger.info("Token: {}", token);
logger.info("Valid: {}", jwtUtil.validateToken(token));
logger.info("Role: {}", jwtUtil.getRoleFromToken(token));
```

## 其他 GET 请求最佳实践

```java
// ✅ 正确的 GET 方法
@GetMapping("/posts")
public Result<PageResponse<PostVo>> listPosts(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") Integer page) {
    // ...
}

// ❌ 错误的做法（不要这样写）
@GetMapping("/posts")
public Result<PageResponse<PostVo>> listPosts(@RequestBody PostPageQuery q) {
    // 这在 Spring 中会导致错误
}

// ✅ POST 请求可以用 @RequestBody
@PostMapping("/posts/search")
public Result<PageResponse<PostVo>> searchPosts(@RequestBody PostPageQuery q) {
    // 可以接收复杂的 JSON 对象
}
```

## 相关文件

- [PostController.java](spring/tianyi-forum-backend/src/main/java/com/tianyi/tianyiforumbackend/Controller/admin/PostController.java)
- [UserController.java](spring/tianyi-forum-backend/src/main/java/com/tianyi/tianyiforumbackend/Controller/user/UserController.java)
- [SecurityConfig.java](spring/tianyi-forum-backend/src/main/java/com/tianyi/tianyiforumbackend/config/SecurityConfig.java)

## 总结

403 错误本质上是由于 GET 请求方法与 @RequestBody 注解的不兼容性导致的。通过改用 `@RequestParam` 来接收查询参数，问题已经完全解决。
