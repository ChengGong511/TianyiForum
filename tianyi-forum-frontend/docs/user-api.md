# 用户端 API 文档 v2.2.0

> 更新日期: 2026-01-05  
> 后端服务: http://localhost:9090

## 通用说明

- 认证统一返回：`{ code: 1, data: { token, expiresIn }, msg? }`
- 业务成功返回：`{ code: 1, data: ..., msg? }`
- 失败返回：`{ code: 0, msg }`
- 需鉴权的接口使用 `Authorization: Bearer <token>`（用户 token）。
- 用户 token 存储在 `localStorage.user_token`
- 管理员 token 存储在 `localStorage.admin_token`（不要混用）

### ⚠️ 重要提示：数据类型约定

- **后端 Java Long 类型**：前端使用 TypeScript 的 `number` 类型对应
- **查询接口**：部分列表查询接口使用 **POST** 方法而非 GET，以便传递复杂的查询条件
- **帖子查询接口**：`/portal/posts/query` 使用 POST 方法，body 参数必须与后端 `PostPageQuery` DTO 保持一致
- **评论查询接口**：`/portal/posts/{id}/comments/query` 使用 POST 方法，参数需对应 `CommentPageQuery` DTO

## 接口实现状态

| 接口                                   | 状态      | 备注                     |
| -------------------------------------- | --------- | ------------------------ |
| POST /user/auth/register               | ✅ 已实现 | 注册成功返回token        |
| POST /user/auth/login                  | ✅ 已实现 | 登录成功返回token        |
| GET /user/profile                      | ✅ 已实现 | 获取当前登录用户信息     |
| GET /portal/boards                     | ✅ 已实现 | 获取板块列表（公开）     |
| POST /portal/posts/query               | ✅ 已实现 | 获取帖子列表（POST方法） |
| GET /portal/posts/{id}                 | ✅ 已实现 | 获取帖子详情（公开）     |
| POST /portal/posts                     | ⏳ 待实现 | 发布帖子（需登录）       |
| POST /portal/posts/{id}/comments/query | ⏳ 待实现 | 获取评论列表（POST方法） |
| POST /portal/posts/{postId}/comments   | ⏳ 待实现 | 发表评论（需登录）       |
| POST /portal/complaints                | ⏳ 待实现 | 提交投诉（需登录）       |

## 认证

### 用户注册

- **POST** `/user/auth/register`
- Body: `{ username, email, password }`
- Resp: `{ token, expiresIn }`
- 示例:
  ```json
  // Request
  { "username": "testuser", "email": "test@example.com", "password": "password123" }
  // Response
  { "code": 1, "data": { "token": "eyJ...", "expiresIn": 604800 } }
  ```

### 用户登录

- **POST** `/user/auth/login`
- Body: `{ username, password }`
- Resp: `{ token, expiresIn }`

## 用户信息

### 获取个人资料

- **GET** `/user/profile`（需登录）
- Headers: `Authorization: Bearer <user_token>`
- Resp: `{ id, username, email, avatarUrl?, createdAt?, lastLoginAt? }`
- 示例:
  ```json
  {
    "code": 1,
    "data": {
      "id": 4,
      "username": "testuser",
      "email": "test@example.com",
      "avatarUrl": null,
      "createdAt": "2026-01-04T21:36:30",
      "lastLoginAt": null
    }
  }
  ```

## 板块

### 获取板块列表

- **GET** `/portal/boards`（公开）
- Resp: `[{ id, name, description, moderator, moderatorId, postCount, sort, createdAt }]`

## 帖子

### 获取帖子列表

- **POST** `/portal/posts/query`（公开）✅ 已实现
- Body（PostPageQuery）:
  ```typescript
  {
    page?: number          // 当前页码，默认1
    pageSize?: number      // 每页数量，默认10
    keyword?: string       // 搜索关键词（标题或内容）
    status?: string        // 帖子状态（normal/pending/archived），用户端默认normal
    sortBy?: string        // 排序方式：hot（热度）| violation（违规分）
  }
  ```
- Resp: `{ items: PostSummary[], total }`
- PostSummary: `{ id, title, boardId, boardName?, author, authorId, hotScore?, isTop?, isFeatured?, createdAt }`
- 示例:
  ```json
  // Request
  {
    "page": 1,
    "pageSize": 10,
    "keyword": "测试",
    "sortBy": "hot"
  }
  // Response
  {
    "code": 1,
    "data": {
      "items": [
        {
          "id": 1,
          "title": "测试帖子",
          "boardId": 1,
          "boardName": "技术讨论",
          "author": "张三",
          "authorId": 1,
          "hotScore": 100,
          "isTop": false,
          "isFeatured": false,
          "createdAt": "2026-01-05T10:00:00"
        }
      ],
      "total": 1
    }
  }
  ```

### 获取帖子详情

- **GET** `/portal/posts/{id}`（公开）✅ 已实现
- Resp: PostDetail = PostSummary + `{ content, violationScore?, status?, updatedAt }`

### 发布帖子

- **POST** `/portal/posts`（需登录）⏳ 待实现
- Body（CreatePostRequestDTO）:
  ```typescript
  {
    title: string // 帖子标题
    content: string // 帖子内容
    boardId: number // 板块ID (Long类型)
  }
  ```
- Resp: 空（成功 code=1）

## 评论

### 获取评论列表

- **POST** `/portal/posts/{id}/comments/query` ⏳ 待实现
- Body（CommentPageQuery）:
  ```typescript
  {
    page?: number      // 当前页码
    pageSize?: number  // 每页数量
    postId: number     // 帖子ID (通过URL和Body都会传)
  }
  ```
- Resp: `{ items: CommentVO[], total }`
- CommentVO: `{ id, postId, parentId?, content, authorId, author, createdAt }`

### 发表评论

- **POST** `/portal/posts/{postId}/comments`（需登录）⏳ 待实现
- Body（CreateCommentRequestDTO）:
  ```typescript
  {
    postId: number      // 帖子ID (Long类型)
    parentId?: number   // 父评论ID (Long类型，可选)
    content: string     // 评论内容
  }
  ```
- Resp: 空

## 投诉

### 提交投诉

- **POST** `/portal/complaints`（需登录）⏳ 待实现
- Body（CreateComplaintRequestDTO）:
  ```typescript
  {
    targetType: string // 投诉目标类型：post | comment | user
    targetId: number // 目标ID (Long类型)
    reason: string // 投诉原因
    evidence: string // 投诉证据
  }
  ```
- Resp: 空
