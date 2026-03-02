# JWT 认证系统使用说明

## 已完成的功能

### 1. JWT 工具类 (`JwtUtil.java`)

- ✅ 生成 JWT Token
- ✅ 解析和验证 Token
- ✅ 提取用户信息（userId, username, role）
- ✅ Token 有效期：7天

### 2. JWT 过滤器 (`JwtAuthenticationFilter.java`)

- ✅ 拦截所有请求
- ✅ 从 Header 中提取 Token（Authorization: Bearer <token>）
- ✅ 验证 Token 有效性
- ✅ 将用户信息存入 SecurityContext

### 3. Security 配置 (`SecurityConfig.java`)

- ✅ 配置无状态会话（JWT 不需要 Session）
- ✅ 配置 CORS 跨域支持
- ✅ 配置接口权限：
  - `/admin/auth/login` - 允许匿名访问
  - `/admin/**` - 需要 ADMIN 角色
  - 其他接口 - 需要认证
- ✅ BCrypt 密码加密

### 4. 登录逻辑 (`AdminServiceImpl.java`)

- ✅ 验证用户名密码
- ✅ 验证管理员角色
- ✅ 验证账号状态
- ✅ 生成并返回 JWT Token

## API 使用示例

### 1. 管理员登录

**请求:**

```http
POST http://localhost:9090/admin/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应（成功）:**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 604800
  }
}
```

**响应（失败）:**

```json
{
  "code": 0,
  "msg": "用户名或密码错误",
  "data": null
}
```

### 2. 访问需要认证的接口

**请求:**

```http
GET http://localhost:9090/admin/users
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 前端集成指南

### 1. 登录后保存 Token

```javascript
// 登录成功后
const response = await fetch("/admin/auth/login", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ username, password }),
});

const result = await response.json();
if (result.code === 1) {
  // 保存到 localStorage
  localStorage.setItem("token", result.data.token);
}
```

### 2. 请求时携带 Token

```javascript
// 方式1: 手动添加 Header
fetch("/admin/users", {
  headers: {
    Authorization: `Bearer ${localStorage.getItem("token")}`,
  },
});

// 方式2: 使用 axios 拦截器（推荐）
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 3. 处理 Token 过期

```javascript
// 响应拦截器
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token 无效或过期，跳转到登录页
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);
```

## 后端控制器中获取当前用户

```java
import com.tianyi.tianyiforumbackend.security.UserContext;

@RestController
@RequestMapping("/admin/posts")
public class PostController {

    @GetMapping("/my")
    public Result getMyPosts() {
        // 获取当前登录用户的 ID
        Long userId = UserContext.getCurrentUserId();

        // 使用 userId 查询数据...
        return Result.success(posts);
    }
}
```

## 数据库准备

### 1. 确保 users 表存在

```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'user',
    status VARCHAR(20) DEFAULT 'active',
    avatar_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_at DATETIME,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. 创建测试管理员账号

```sql
-- 密码是 BCrypt 加密的 "admin123"
INSERT INTO users (username, email, password, role, status) VALUES
('admin', 'admin@tianyi.com', '$2a$10$YourBCryptHashHere', 'admin', 'active');
```

或者使用 Java 代码生成密码：

```java
@Autowired
private PasswordEncoder passwordEncoder;

String encodedPassword = passwordEncoder.encode("admin123");
System.out.println("BCrypt 密码: " + encodedPassword);
```

## 安全建议

1. **生产环境配置：**
   - 将 JWT 密钥移到环境变量或配置中心
   - 使用更长、更复杂的密钥
   - 启用 HTTPS

2. **Token 刷新：**
   - 考虑实现 Refresh Token 机制
   - Token 快过期时自动刷新

3. **密码策略：**
   - 强制复杂密码
   - 定期修改密码
   - 删除默认管理员账号

4. **日志审计：**
   - 记录所有登录尝试
   - 记录管理员操作

## 常见问题

### Q1: 提示 "403 Forbidden"？

**A:** 检查：

1. Token 是否正确携带在 Header 中
2. Token 格式是否为 `Bearer <token>`
3. 用户角色是否匹配接口权限

### Q2: Token 解析失败？

**A:** 检查：

1. Token 是否过期
2. JWT 密钥是否一致
3. Token 是否被篡改

### Q3: 跨域问题？

**A:** SecurityConfig 已配置 CORS，检查前端地址是否在允许列表中。

## 文件清单

```
src/main/java/com/tianyi/tianyiforumbackend/
├── security/
│   ├── JwtUtil.java                    # JWT 工具类
│   ├── JwtAuthenticationFilter.java    # JWT 过滤器
│   └── UserContext.java                # 用户上下文工具
├── config/
│   ├── SecurityConfig.java             # Security 配置
│   └── DataInitializer.java            # 数据初始化器
├── Service/
│   ├── AdminSerivce.java               # 管理员服务接口
│   └── Impl/
│       └── AdminServiceImpl.java       # 登录逻辑实现
├── Controller/login/
│   └── loginController.java            # 登录控制器
└── Mapper/
    └── UserMapper.java                 # 用户数据访问
```

## 下一步

1. 在数据库中创建管理员账号
2. 启动应用测试登录接口
3. 前端集成 Token 认证
4. 为其他控制器添加认证保护
5. 实现用户登录接口（参考管理员登录）
