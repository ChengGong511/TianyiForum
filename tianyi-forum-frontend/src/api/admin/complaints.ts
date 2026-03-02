import { get, post } from '@/api/http'
import type { PageResult } from '@/api/http'

export type Complaint = {
  id: number
  targetType: 'post' | 'comment' | 'user'
  targetId: number
  reason: string
  evidence?: string
  reporter: string
  reporterId?: number
  status: 'pending' | 'resolved'
  handleNote?: string
  createdAt: string
  handledAt?: string
}

export type ComplaintQuery = {
  page: number
  pageSize: number
  status?: 'pending' | 'resolved'
}

export type ComplaintHandleDTO = {
  action: 'dismiss' | 'takedown' | 'reject_post'
  handleNote?: string
  handlerId?: number
}

export async function listComplaints(q: ComplaintQuery): Promise<PageResult<Complaint>> {
  return get<PageResult<Complaint>>('/admin/complaints', q)
}

export async function getComplaint(id: number): Promise<Complaint> {
  return get<Complaint>(`/admin/complaints/${id}`)
}

export async function resolveComplaint(id: number, payload: ComplaintHandleDTO): Promise<void> {
  await post(`/admin/complaints/${id}/resolve`, payload)
}
