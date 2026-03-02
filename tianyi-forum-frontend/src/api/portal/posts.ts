import { get, post } from '../http'
import type { PageResult } from '../http'

export interface PostSummary {
  id: number
  title: string
  boardId: number
  boardName?: string
  author: string
  authorId: number
  hotScore?: number
  isTop?: boolean
  isFeatured?: boolean
  createdAt: string
}

export interface PostDetail extends PostSummary {
  content: string
  violationScore?: number
  status?: string
  updatedAt?: string
}

export interface CreatePostPayload {
  title: string
  content: string
  boardId: number
}

export interface PostQuery {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string  // normal/pending/archived
  sortBy?: 'hot' | 'violation'  // 后端支持: hot（热度）| violation（违规分）
}

export function listPosts(params: PostQuery) {
  return post<PageResult<PostSummary>>('/portal/posts/query', params)
}

export function getPostDetail(id: number) {
  return get<PostDetail>(`/portal/posts/${id}`)
}

export function createPost(payload: CreatePostPayload) {
  return post<void>('/portal/posts', payload)
}
