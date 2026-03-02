import { del, get, post, put } from '@/api/http'

export type Board = {
  id: number
  name: string
  description?: string
  moderator?: string
  moderatorId?: number
  postCount: number
  sort: number
  createdAt?: string
}

export async function listBoards(): Promise<Board[]> {
  // 后端接口返回 Result<Board[]>，http.ts 会统一解包 data
  return get<Board[]>('/admin/boards')
}

export type BoardPayload = {
  name: string
  description?: string
  moderatorId?: number
  sort: number
}

export async function createBoard(payload: BoardPayload): Promise<void> {
  await post('/admin/boards', payload)
}

export async function updateBoard(boardId: number, payload: Partial<BoardPayload>): Promise<void> {
  await put(`/admin/boards/${boardId}`, payload)
}

export async function assignModerator(boardId: number, moderatorId: number): Promise<void> {
  await put(`/admin/boards/${boardId}/moderator`, { moderatorId })
}

export async function getBoardPostCount(boardId: number): Promise<number> {
  return get<number>(`/admin/boards/${boardId}/post-count`)
}

export async function deleteBoard(boardId: number): Promise<void> {
  await del(`/admin/boards/${boardId}`)
}
