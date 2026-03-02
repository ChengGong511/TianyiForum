export type PageResult<T> = { items: T[]; total: number }
export type ApiResult<T> = { code: number; msg?: string; data: T }

const API_BASE = import.meta.env.VITE_API_BASE ?? ''

// 根据路径区分管理员与普通用户的 token，避免混用
function pickToken(path: string) {
  const isAdmin = path.startsWith('/admin')
  return isAdmin ? localStorage.getItem('admin_token') : localStorage.getItem('user_token')
}

function authHeader(path: string) {
  const token = pickToken(path)
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request<T>(path: string, init: RequestInit = {}, params?: Record<string, unknown>) {
  const query = params ? '?' + new URLSearchParams(params as Record<string, string>).toString() : ''
  const res = await fetch(`${API_BASE}${path}${query}`, {
    credentials: 'include',
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...authHeader(path),
      ...(init.headers || {}),
    },
  })

  if (!res.ok) {
    let message = res.statusText
    try {
      const data = (await res.json()) as { message?: string }
      if (data?.message) message = data.message
    } catch (e) {
      void e
    }
    throw new Error(message)
  }
  // 统一 Result<T> 解包
  const payload = (await res.json()) as ApiResult<T>
  if (payload.code !== 1) {
    throw new Error(payload.msg || '请求失败')
  }
  return payload.data
}

export async function get<T>(path: string, params?: Record<string, unknown>): Promise<T> {
  return request<T>(path, { method: 'GET' }, params)
}

export async function post<T>(path: string, body?: unknown): Promise<T> {
  return request<T>(path, { method: 'POST', body: body ? JSON.stringify(body) : undefined })
}

export async function put<T>(path: string, body?: unknown): Promise<T> {
  return request<T>(path, { method: 'PUT', body: body ? JSON.stringify(body) : undefined })
}

export async function del<T>(path: string, params?: Record<string, unknown>): Promise<T> {
  return request<T>(path, { method: 'DELETE' }, params)
}
