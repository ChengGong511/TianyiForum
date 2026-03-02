import { post, put } from '@/api/http'
import type { PageResult } from '@/api/http'

export type User = {
  id: number
  username: string
  role: 'user' | 'moderator' | 'admin'
  status: 'active' | 'banned'
  createdAt: string
  email?: string
  lastLoginAt?: string
}

// 与后端 UserPageQuery 对齐
export type UserQuery = {
  page: number
  pageSize: number
  keyword?: string
  status?: 'active' | 'banned'
}

export async function listUsers(
  q: Partial<UserQuery> & { page: number; pageSize: number },
): Promise<PageResult<User>> {
  return post<PageResult<User>>('/admin/users', q)
}

export async function updateUserStatus(
  userId: number,
  status: 'active' | 'banned',
  reason: string = '',
): Promise<void> {
  await put(`/admin/users/${userId}/status`, { status, reason })
}
