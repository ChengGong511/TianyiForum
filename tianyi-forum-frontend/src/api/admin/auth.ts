import { get, post } from '@/api/http'

export type AdminInfo = {
  id: number
  username: string
  roles: string[]
}

export async function login(username: string, password: string): Promise<string> {
  const res = await post<{ token: string; expiresIn: number }>('/admin/auth/login', {
    username,
    password,
  })
  return res.token
}

export async function info(): Promise<AdminInfo> {
  return get<AdminInfo>('/admin/auth/info')
}
