import { get, post } from '@/api/http'
import type { PageResult } from '@/api/http'

export type Post = {
  id: number
  title: string
  content?: string
  author: string
  authorId?: number
  boardId?: number
  status: 'normal' | 'pending' | 'archived'
  createdAt: string
  hotScore: number
  violationScore: number
  viewCount?: number
  commentCount?: number
  isTop?: boolean
  isFeatured?: boolean
}

// 与后端 PostPageQuery 对齐
export type PostQuery = {
  page: number
  pageSize: number
  keyword?: string
  status?: 'normal' | 'pending' | 'archived'
  sortBy?: 'hot' | 'violation'
}

export async function listPosts(
  q: Partial<PostQuery> & { page: number; pageSize: number },
): Promise<PageResult<Post>> {
  const toNullable = (value: unknown) => {
    if (value === undefined || value === null) return null
    if (typeof value === 'string' && value.trim() === '') return null
    return value
  }
  const payload = {
    page: q.page,
    pageSize: q.pageSize,
    keyword: toNullable(q.keyword),
    status: toNullable(q.status),
    sortBy: toNullable(q.sortBy),
  }
  return post<PageResult<Post>>('/admin/posts', payload)
}

export type PostReviewDTO = {
  action: 'approve' | 'top' | 'featured' | 'reject' | 'ban'
  reason?: string
}

export async function getPostDetail(postId: number): Promise<Post> {
  return get<Post>(`/admin/posts/${postId}`)
}

export async function reviewPost(postId: number, payload: PostReviewDTO): Promise<void> {
  await post(`/admin/posts/${postId}/review`, payload)
}
