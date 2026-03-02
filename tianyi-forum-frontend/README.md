# 天翼论坛 - 管理端前端

基于 Vue 3 + TypeScript + Element Plus 构建的现代化管理后台系统。

---

## 🚀 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:5173/admin/login

**默认登录：** 用户名任意，密码任意（当前使用 Mock 数据）

---

## 📋 项目特性

- ✅ **前端先行开发**：使用 Mock 数据，无需后端即可运行
- ✅ **完整接口文档**：提供 OpenAPI 3.0 规范的 API 文档，可直接导入 Apifox
- ✅ **TypeScript 类型安全**：全面的类型定义，提升开发体验
- ✅ **Element Plus UI**：基于 Vue 3 的企业级组件库
- ✅ **路由守卫**：JWT Token 认证拦截
- ✅ **模块化设计**：按业务模块拆分页面和 API

---

## 📁 项目结构

```
tianyi-forum-frontend/
├── docs/                          # 📚 项目文档
│   ├── api-spec.yaml             # Apifox 接口文档（OpenAPI 3.0）
│   ├── database-schema.sql       # 数据库建表脚本
│   ├── README.md                 # 接口文档使用指南
│   └── PROJECT.md                # 项目开发总览
├── src/
│   ├── api/                      # API 请求封装
│   │   ├── http.ts              # 基础 HTTP 工具
│   │   └── admin/               # 管理端 API
│   │       ├── auth.ts          # 认证接口（Mock）
│   │       ├── users.ts         # 用户管理接口（Mock）
│   │       └── posts.ts         # 帖子管理接口（Mock）
│   ├── layout/                   # 布局组件
│   │   └── AdminLayout.vue      # 管理端主布局
│   ├── views/                    # 页面组件
│   │   └── admin/               # 管理端页面
│   │       ├── AdminLogin.vue         # 登录页
│   │       ├── AdminDashboard.vue     # 数据看板
│   │       ├── UsersList.vue          # 用户管理
│   │       ├── PostsList.vue          # 帖子管理
│   │       ├── Complaints.vue         # 投诉受理
│   │       ├── BoardConfig.vue        # 板块配置
│   │       └── SensitiveWords.vue     # 敏感词库
│   ├── router/                   # 路由配置
│   │   └── index.ts             # 路由定义 + 守卫
│   ├── stores/                   # Pinia 状态管理
│   └── main.ts                   # 应用入口
└── package.json
```

---

## 📄 接口文档

### 导入到 Apifox

1. 打开 Apifox 客户端
2. 创建新项目：`天翼论坛管理端`
3. 点击 **导入** → **OpenAPI/Swagger**
4. 选择 `docs/api-spec.yaml` 文件
5. 导入完成后即可查看所有接口定义

### 接口模块

| 模块     | 接口数量 | 说明                |
| -------- | -------- | ------------------- |
| 认证模块 | 2        | 登录、获取用户信息  |
| 用户管理 | 2        | 用户列表、封禁/解封 |
| 内容管理 | 2        | 帖子列表、审核操作  |
| 投诉受理 | 2        | 投诉列表、处理投诉  |
| 板块配置 | 4        | 增删改查板块        |
| 系统配置 | 3        | 敏感词管理          |

---

## 🗄️ 数据库设计

后端开发可直接使用 `docs/database-schema.sql` 建表脚本，包含：

- ✅ 用户表 (users)
- ✅ 帖子表 (posts)
- ✅ 评论表 (comments)
- ✅ 板块表 (boards)
- ✅ 投诉表 (complaints)
- ✅ 敏感词表 (sensitive_words)
- ✅ 管理员日志表 (admin_logs)

---

## 🛠️ 开发指南

### 前端 Mock 开发（当前阶段）

所有 API 文件（`src/api/admin/*.ts`）目前返回 Mock 数据：

```typescript
// src/api/admin/users.ts
export async function listUsers(q: UserQuery): Promise<PageResult<User>> {
  await delay(200) // 模拟网络延迟
  return { items: mockUsers(), total: 137 }
}
```

### 切换到真实后端

修改 `src/api/http.ts`：

```typescript
const BASE_URL = 'http://localhost:8080/api'

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const token = localStorage.getItem('admin_token')
  const headers: HeadersInit = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch(BASE_URL + url + '?...', { headers })
  const json = await res.json()

  if (json.code !== 0) throw new Error(json.message)
  return json.data as T
}
```

然后删除 `src/api/admin/*.ts` 中的 Mock 函数，替换为真实的 HTTP 请求。

---

## 🎯 功能模块

### 1. 数据看板

- 总用户数、今日新帖、待审投诉、访问量统计
- 趋势图表占位（可接入 ECharts）

### 2. 用户管理

- 分页列表、关键词搜索、状态筛选
- 封禁/解封用户操作

### 3. 帖子管理

- 多条件搜索（标题、作者、状态）
- 按热度/违规权重排序
- 置顶、加精、驳回、下架操作

### 4. 投诉受理

- 投诉列表展示
- 处理投诉功能

### 5. 板块配置

- 板块增删改查
- 版主委派、排序调整

### 6. 敏感词库

- 敏感词增删查
- 支持批量导入导出（UI 占位）

---

## 📦 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - JavaScript 的超集，提供类型安全
- **Element Plus** - 基于 Vue 3 的组件库
- **Vue Router** - 官方路由管理器
- **Pinia** - 新一代状态管理库
- **Vite** - 下一代前端构建工具

---

## 📜 脚本命令

### 开发

```bash
npm run dev          # 启动开发服务器
npm run build        # 构建生产版本
npm run preview      # 预览生产构建
```

### 代码质量

```bash
npm run type-check   # TypeScript 类型检查
npm run lint         # ESLint 代码检查
npm run format       # Prettier 代码格式化
```

---

## 🔐 认证机制

- **登录接口**：`POST /api/admin/auth/login`
- **Token 存储**：`localStorage.admin_token`
- **路由守卫**：自动拦截未登录请求，跳转到登录页
- **Token 携带**：所有请求在 Header 中携带 `Authorization: Bearer {token}`

---

## 📞 联系方式

- **接口文档问题**：查看 `docs/README.md`
- **数据库设计**：查看 `docs/database-schema.sql`
- **开发流程**：查看 `docs/PROJECT.md`

---

## 📝 更新日志

### v1.0.0 (2025-12-29)

- ✅ 完成管理端所有页面开发
- ✅ 生成完整的 Apifox 接口文档
- ✅ 提供数据库建表脚本
- ✅ 内置 Mock 数据支持独立运行

---

## 📄 License

MIT

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```
