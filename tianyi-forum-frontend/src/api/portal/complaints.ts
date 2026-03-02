import { post } from '../http'

export interface CreateComplaintPayload {
  targetType: 'post' | 'comment' | 'user'
  targetId: number
  reason: string
  evidence: string  // 后端DTO要求必填
}

export function createComplaint(payload: CreateComplaintPayload) {
  return post<void>('/portal/complaints', payload)
}
