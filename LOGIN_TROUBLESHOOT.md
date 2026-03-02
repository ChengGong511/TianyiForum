# 登录失败诊断和修复指南

## 已修复的问题

### 1. ✅ 前端 API 基础路径错误

**问题**: 前端使用 `/api` 前缀，导致实际请求变成 `/api/admin/auth/login`

**修复**: 已将 `http.ts` 中的 `API_BASE` 改为正确的路径

```typescript
// 修改前
const API_BASE = "http://localhost:9090/api";

// 修改后
const API_BASE = "http://localhost:9090";
```

## 登录失败的常见原因

### 原因1: 数据库中没有管理员账号 ⚠️

**检查方法:**

```sql
-- 在数据库中执行
SELECT * FROM users WHERE role = 'admin';
```

**如果结果为空，需要创建管理员账号:**

#### 方式一: 直接使用预生成的密码（推荐）

使用这个加密密码：`$2a$10$aLZHCH8jDrCLLmEEYVWdeOxWqrqRPATwMU8VfKAZmzMhVDkFkSKFy`（对应密码: admin123）

```sql
INSERT INTO users (username, email, password, role, status, created_at, updated_at)
VALUES ('admin', 'admin@tianyi.com', '$2a$10$aLZHCH8jDrCLLmEEYVWdeOxWqrqRPATwMU8VfKAZmzMhVDkFkSKFy', 'admin', 'active', NOW(), NOW());
```

#### 方式二: 运行后端的密码生成工具

1. **启动后端应用（确保已编译）**

   ```bash
   cd d:\TianyiForum\spring\tianyi-forum-backend
   mvn clean package -DskipTests
   mvn spring-boot:run
   ```

2. **在项目中运行 PasswordGenerator**
   - 如果使用 IntelliJ IDEA：右键点击 `PasswordGenerator.java` → Run
   - 如果使用 VS Code：打开终端运行

   ```bash
   mvn exec:java -Dexec.mainClass="com.tianyi.tianyiforumbackend.util.PasswordGenerator"
   ```

3. **复制输出的加密密码，然后执行 SQL**
   ```sql
   INSERT INTO users (username, email, password, role, status, created_at, updated_at)
   VALUES ('admin', 'admin@tianyi.com', '[复制的加密密码]', 'admin', 'active', NOW(), NOW());
   ```

### 原因2: 后端应用未启动

**检查:**

- 后端应该在 `http://localhost:9090` 运行
- 可以在浏览器中测试：访问 `http://localhost:9090/admin/auth/login`
- 应该看到 POST 方法不允许（因为是 GET 请求），这表示应用正在运行

**启动后端:**

```bash
cd d:\TianyiForum\spring\tianyi-forum-backend
mvn spring-boot:run
```

### 原因3: 用户名或密码错误

**默认测试账号:**

- 用户名: `admin`
- 密码: `admin123`

### 原因4: 数据库连接问题

**检查 application.yml 中的数据库配置:**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/tianyidatabase
    username: root
    password: 123456
```

**验证:**

1. MySQL 服务是否启动
2. 数据库名称是否正确（应该是 `tianyidatabase`）
3. 用户名和密码是否正确

### 原因5: CORS 跨域问题

后端已配置允许来自 `http://localhost:5173` 的请求。如果前端端口不同，需要修改 SecurityConfig：

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",  // 你的前端地址
    "http://localhost:3000"
));
```

## 完整的登录测试流程

### 1. 准备环境

```bash
# 1. 确保 MySQL 运行
# 2. 确保数据库存在
CREATE DATABASE IF NOT EXISTS tianyidatabase;
USE tianyidatabase;

# 3. 执行数据库初始化脚本（如果有）
# 4. 创建管理员账号
INSERT INTO users (username, email, password, role, status, created_at, updated_at)
VALUES ('admin', 'admin@tianyi.com', '$2a$10$aLZHCH8jDrCLLmEEYVWdeOxWqrqRPATwMU8VfKAZmzMhVDkFkSKFy', 'admin', 'active', NOW(), NOW());
```

### 2. 启动后端

```bash
cd d:\TianyiForum\spring\tianyi-forum-backend
mvn spring-boot:run
```

**检查输出，应该看到:**

```
Started TianyiForumBackendApplication in X seconds
```

### 3. 启动前端

```bash
cd d:\TianyiForum\tianyi-forum-frontend
npm run dev
```

### 4. 测试登录

1. 打开浏览器访问 `http://localhost:5173/admin/login`
2. 输入用户名: `admin`
3. 输入密码: `admin123`
4. 点击登录

## 调试技巧

### 查看浏览器网络请求

1. 打开浏览器 DevTools（F12）
2. 切换到 Network 标签
3. 输入用户名密码点击登录
4. 查看请求：应该是 POST `http://localhost:9090/admin/auth/login`
5. 查看响应：
   - 成功: `{"code": 1, "data": {"token": "...", "expiresIn": 604800}}`
   - 失败: `{"code": 0, "msg": "用户名或密码错误"}`

### 查看后端日志

启动后端时，终端会打印日志，可以看到：

- SQL 执行语句
- 密码验证过程
- Token 生成过程

## SQL 快速测试

```sql
-- 检查是否有管理员账号
SELECT * FROM users WHERE role = 'admin' LIMIT 1;

-- 查看所有用户
SELECT id, username, email, role, status FROM users;

-- 删除测试账号（如果需要重新创建）
DELETE FROM users WHERE username = 'admin';

-- 创建管理员账号（使用标准密码）
INSERT INTO users (username, email, password, role, status, created_at, updated_at)
VALUES ('admin', 'admin@tianyi.com', '$2a$10$aLZHCH8jDrCLLmEEYVWdeOxWqrqRPATwMU8VfKAZmzMhVDkFkSKFy', 'admin', 'active', NOW(), NOW());

-- 验证插入成功
SELECT * FROM users WHERE username = 'admin';
```

## 如果仍然登录失败

**请提供以下信息:**

1. 浏览器控制台错误信息（F12 → Console）
2. 网络请求的响应内容（F12 → Network）
3. 后端运行日志输出
4. 数据库中用户表的内容：`SELECT * FROM users;`

## 登录成功后

登录成功后，会：

1. 将 Token 保存到 `localStorage` 的 `admin_token` 字段
2. 重定向到 `/admin` 页面
3. 后续所有请求会自动在 Header 中携带 Token：`Authorization: Bearer <token>`
