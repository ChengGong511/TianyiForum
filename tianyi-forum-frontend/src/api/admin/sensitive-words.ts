import { del, get, post, put } from '@/api/http'
import type { PageResult } from '@/api/http'

export type SensitiveWord = {
  id: number
  word: string
  level: '高' | '中' | '低'
  createdAt: string
}

export type SensitiveWordQuery = {
  page: number
  pageSize: number
  keyword: string
}

export async function listSensitiveWords(
  q: SensitiveWordQuery,
): Promise<PageResult<SensitiveWord>> {
  return get<PageResult<SensitiveWord>>('/admin/sensitive-words', q)
}

export async function createSensitiveWord(payload: {
  word: string
  level: '高' | '中' | '低'
}): Promise<void> {
  await post('/admin/sensitive-words', payload)
}

export async function updateSensitiveWord(
  id: number,
  payload: {
    word: string
    level: '高' | '中' | '低'
  },
): Promise<void> {
  await put(`/admin/sensitive-words/${id}`, payload)
}

export async function deleteSensitiveWord(id: number): Promise<void> {
  await del(`/admin/sensitive-words/${id}`)
}
