import { get } from '../http'

export interface Board {
  id: number
  name: string
  description?: string
  moderator?: string
  moderatorId?: number
  postCount?: number
  sort?: number
  createdAt?: string
}

export function listBoards() {
  return get<Board[]>('/portal/boards')
}
