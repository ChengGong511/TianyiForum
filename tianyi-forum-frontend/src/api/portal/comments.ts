import { get, post } from '../http'
import type { PageResult } from '../http'

export interface CommentItem {
  id: number
  postId: number
  parentId?: number
  content: string
  authorId: number
  author: string
  createdAt: string
}

export interface CommentQuery {
  page?: number
  pageSize?: number
  postId?: number
}

export interface CreateCommentPayload {
  postId: number       // 必须包含，与后端DTO保持一致
  content: string
  parentId?: number
}

export function listComments(postId: number, params: CommentQuery) {
  return post<PageResult<CommentItem>>(`/portal/posts/${postId}/comments/query`, {
    ...params,
    postId
  })
}

export function createComment(postId: number, payload: CreateCommentPayload) {
  // 确保payload包含postId字段
  return post<void>(`/portal/posts/${postId}/comments`, {
    ...payload,
    postId  // 后端CreateCommentRequestDTO需要postId字段
  })
}
