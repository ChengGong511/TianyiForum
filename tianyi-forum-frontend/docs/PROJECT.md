# 天翼论坛 - 项目开发文档总览

## 📁 文档目录

- [`api-spec.yaml`](./api-spec.yaml) - **Apifox 接口文档**（OpenAPI 3.0）
- [`database-schema.sql`](./database-schema.sql) - **数据库建表脚本**
- [`README.md`](./README.md) - **开发指南**

---

## 🎯 开发流程

### 阶段一：前端先行（已完成 ✅）

1. ✅ 完成管理端所有页面 UI 开发
2. ✅ 使用 Mock 数据实现前端逻辑
3. ✅ 生成完整的 API 接口文档

**当前状态：** 前端可独立运行，无需后端支持

---

### 阶段二：接口文档确认（进行中 🚧）

**任务清单：**

- [ ] 将 `api-spec.yaml` 导入 Apifox
- [ ] 前后端团队评审接口定义
- [ ] 确认字段命名、数据类型、枚举值
- [ ] 讨论是否需要新增接口或调整参数

**注意事项：**

- 接口路径遵循 RESTful 规范
- 统一响应格式：`{ code, message, data }`
- 所有时间字段使用 `YYYY-MM-DD HH:mm:ss` 格式
- 分页参数统一为 `page` 和 `pageSize`

---

### 阶段三：数据库设计（待开始 ⏳）

**任务清单：**

- [ ] 评审 `database-schema.sql` 建表脚本
- [ ] 确认表结构、字段类型、索引设计
- [ ] 执行建表脚本创建数据库
- [ ] 插入测试数据

**核心表：**

1. `users` - 用户表
2. `posts` - 帖子表
3. `comments` - 评论表
4. `boards` - 板块表
5. `complaints` - 投诉表
6. `sensitive_words` - 敏感词表
7. `admin_logs` - 管理员操作日志表

---

### 阶段四：后端开发（待开始 ⏳）

**技术栈建议：**

- Spring Boot 3.x
- MyBatis-Plus
- MySQL 8.0
- Redis（缓存）
- JWT（认证）

**开发顺序：**

1. 搭建项目框架（Spring Boot + MyBatis）
2. 配置 JWT 认证拦截器
3. 实现用户管理模块接口
4. 实现内容管理模块接口
5. 实现投诉受理模块接口
6. 实现板块配置模块接口
7. 实现系统配置模块接口

**后端开发规范：**

- 所有接口必须返回统一格式：`{ code, message, data }`
- 使用 `@RestController` 和 `@RequestMapping` 注解
- 参数校验使用 `@Valid` + JSR-303
- 异常统一通过全局异常处理器返回
- 敏感操作必须记录到 `admin_logs` 表

---

### 阶段五：前后端联调（待开始 ⏳）

**任务清单：**

- [ ] 前端修改 API 基础路径指向后端地址
- [ ] 替换 Mock 数据为真实 HTTP 请求
- [ ] 使用 Apifox 测试所有接口
- [ ] 修复前后端数据格式差异
- [ ] 验证分页、搜索、筛选功能

**联调配置：**

修改 `src/api/http.ts`：

```typescript
const BASE_URL = 'http://localhost:8080/api'

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const token = localStorage.getItem('admin_token')
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const q = params ? '?' + new URLSearchParams(params as Record<string, string>) : ''
  const res = await fetch(BASE_URL + url + q, { headers })

  if (!res.ok) throw new Error(res.statusText)
  const json = await res.json()

  // 后端统一返回 { code, message, data }
  if (json.code !== 0) throw new Error(json.message)
  return json.data as T
}
```

---

## 📊 项目进度看板

| 模块     | 前端开发 | 接口文档 | 数据库 | 后端开发 | 联调测试 |
| -------- | -------- | -------- | ------ | -------- | -------- |
| 认证模块 | ✅       | ✅       | ⏳     | ⏳       | ⏳       |
| 用户管理 | ✅       | ✅       | ⏳     | ⏳       | ⏳       |
| 内容管理 | ✅       | ✅       | ⏳     | ⏳       | ⏳       |
| 投诉受理 | ✅       | ✅       | ⏳     | ⏳       | ⏳       |
| 板块配置 | ✅       | ✅       | ⏳     | ⏳       | ⏳       |
| 系统配置 | ✅       | ✅       | ⏳     | ⏳       | ⏳       |

---

## 🔗 相关资源

- **Apifox 官网**: https://apifox.com
- **OpenAPI 规范**: https://swagger.io/specification/
- **Element Plus 文档**: https://element-plus.org
- **Vue 3 文档**: https://cn.vuejs.org

---

## 👥 团队协作

### 前端团队

- 维护接口文档 `api-spec.yaml`
- 更新 Mock 数据逻辑
- 提供 UI/UX 反馈

### 后端团队

- 参考接口文档实现 API
- 提供数据库优化建议
- 反馈接口设计不合理之处

### 产品团队

- 确认业务逻辑和数据流转
- 提供测试用例和验收标准

---

## 📝 变更记录

| 日期       | 版本   | 变更内容               | 负责人   |
| ---------- | ------ | ---------------------- | -------- |
| 2025-12-29 | v1.0.0 | 完成前端开发和接口文档 | 前端团队 |

---

## 🆘 常见问题

**Q: 前端如何切换 Mock 到真实后端？**  
A: 修改 `src/api/http.ts` 中的 `BASE_URL` 即可。

**Q: 接口返回格式和文档不一致怎么办？**  
A: 联系前端团队更新 `api-spec.yaml`，双方保持同步。

**Q: 数据库表需要调整怎么办？**  
A: 修改 `database-schema.sql`，并同步更新接口文档中的 Schema 定义。

**Q: 新增功能需要新接口怎么办？**  
A: 在 `api-spec.yaml` 中添加接口定义，前后端评审后实现。
