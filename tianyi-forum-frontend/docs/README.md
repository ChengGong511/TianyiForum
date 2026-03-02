# 天翼论坛 - 接口文档使用指南

## 📋 文档说明

本项目采用 **前端先行** 开发策略，接口文档基于 OpenAPI 3.0 规范编写，可直接导入 Apifox 使用。

---

## 🚀 快速开始

### 1. 导入 Apifox

1. 打开 Apifox 客户端
2. 创建新项目：`天翼论坛管理端`
3. 点击 **导入** → **OpenAPI/Swagger** → **从文件导入**
4. 选择项目根目录下的 `docs/api-spec.yaml`
5. 导入完成后即可查看所有接口定义

### 2. 前端 Mock 开发

当前前端已内置 Mock 数据，无需后端即可运行：

```bash
npm install
npm run dev
```

访问 `http://localhost:5173/admin/login`，使用任意用户名登录即可体验完整功能。

### 3. 后端开发流程

#### Step 1: 查看接口文档

- 在 Apifox 中查看每个接口的请求参数、响应格式
- 注意 `required` 字段和枚举值限制

#### Step 2: 数据库建表

参考接口文档中的 **Schemas** 部分设计表结构，例如：

**用户表 (users)**

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100),
  password VARCHAR(255) NOT NULL,
  role ENUM('user', 'moderator', 'admin') DEFAULT 'user',
  status ENUM('active', 'banned') DEFAULT 'active',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  last_login_at DATETIME
);
```

**帖子表 (posts)**

```sql
CREATE TABLE posts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  author_id BIGINT NOT NULL,
  board_id BIGINT,
  status ENUM('normal', 'pending', 'archived') DEFAULT 'pending',
  is_top BOOLEAN DEFAULT FALSE,
  is_featured BOOLEAN DEFAULT FALSE,
  hot_score INT DEFAULT 0,
  violation_score INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (author_id) REFERENCES users(id),
  FOREIGN KEY (board_id) REFERENCES boards(id)
);
```

#### Step 3: 实现 RESTful API

按照文档中的接口路径、请求方法、参数格式实现后端接口。

**响应格式统一约定：**

```json
{
  "code": 0,          // 0 表示成功，非 0 表示错误
  "message": "success",
  "data": { ... }     // 实际数据
}
```

#### Step 4: 联调测试

- 前端修改 API 基础路径，从 Mock 切换到真实后端
- 在 `src/api/http.ts` 中配置后端地址
- 使用 Apifox 的 Mock 服务进行接口测试

---

## 📚 接口模块说明

### 认证模块

- `POST /api/admin/auth/login` - 管理员登录
- `GET /api/admin/auth/info` - 获取当前管理员信息

### 用户管理

- `GET /api/admin/users` - 获取用户列表（分页、搜索、筛选）
- `PUT /api/admin/users/{userId}/status` - 封禁/解封用户

### 内容管理

- `GET /api/admin/posts` - 获取帖子列表（支持多种排序）
- `POST /api/admin/posts/audit` - 审核帖子（置顶/加精/驳回/下架）

### 投诉受理

- `GET /api/admin/complaints` - 获取投诉列表
- `POST /api/admin/complaints/{id}/handle` - 处理投诉

### 板块配置

- `GET /api/admin/boards` - 获取板块列表
- `POST /api/admin/boards` - 新增板块
- `PUT /api/admin/boards/{id}` - 更新板块
- `DELETE /api/admin/boards/{id}` - 删除板块

### 系统配置

- `GET /api/admin/sensitive-words` - 获取敏感词列表
- `POST /api/admin/sensitive-words` - 新增敏感词
- `DELETE /api/admin/sensitive-words/{id}` - 删除敏感词

---

## 🔐 认证机制

所有管理端接口（除登录接口外）均需在 HTTP Header 中携带 JWT Token：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token 获取流程：**

1. 调用 `/api/admin/auth/login` 登录
2. 后端返回 `token` 和 `expiresIn`
3. 前端存储 Token 到 `localStorage`
4. 后续请求自动在 Header 中携带

---

## 📊 数据库设计建议

### 核心表结构

1. **users** - 用户表
2. **posts** - 帖子表
3. **comments** - 评论表（可选）
4. **boards** - 板块表
5. **complaints** - 投诉表
6. **sensitive_words** - 敏感词表
7. **admin_logs** - 管理员操作日志表（建议）

### 索引建议

- users: `idx_username`, `idx_email`, `idx_status`
- posts: `idx_author_id`, `idx_board_id`, `idx_status`, `idx_hot_score`
- complaints: `idx_status`, `idx_target_type_id`

---

## 🛠️ 开发工具推荐

- **Apifox**: 接口文档 + Mock + 测试
- **Navicat/DBeaver**: 数据库管理
- **Postman**: 备用接口测试工具
- **VSCode REST Client**: 轻量级接口调试

---

## 📝 注意事项

1. **分页参数**: 所有列表接口必须支持 `page` 和 `pageSize` 参数
2. **时间格式**: 统一使用 `YYYY-MM-DD HH:mm:ss` 格式
3. **错误码**: 建议定义统一的错误码表（如 1001-参数错误, 1002-权限不足）
4. **日志记录**: 敏感操作（封禁、删除）必须记录操作人和原因
5. **SQL 注入防护**: 所有查询参数必须使用预编译语句

---

## 🔄 Mock 切换到真实后端

修改 `src/api/http.ts`：

```typescript
// 开发环境使用 Mock
const BASE_URL = import.meta.env.DEV
  ? ''
  : 'http://localhost:8080/api'

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const fullUrl = BASE_URL + url + (params ? '?' + new URLSearchParams(...) : '')
  // ...
}
```

或者直接在各个 API 文件中替换 Mock 函数为真实的 HTTP 请求。

---

## 📞 联系方式

如有接口定义问题或需要调整，请联系前端团队进行同步更新。
