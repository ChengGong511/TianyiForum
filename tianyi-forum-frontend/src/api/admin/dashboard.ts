import { get } from '@/api/http'

export type DashboardStats = {
  totalUsers: number
  totalPosts: number
  todayPosts: number
  pendingComplaints: number
  totalViews: number
}

export async function getDashboardStats(): Promise<DashboardStats> {
  return get<DashboardStats>('/admin/dashboard/stats')
}
