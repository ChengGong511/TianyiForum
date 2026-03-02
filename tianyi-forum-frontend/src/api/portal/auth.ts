import { post } from '../http'

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  expiresIn: number
}

export function login(payload: LoginPayload) {
  return post<LoginResponse>('/user/auth/login', payload)
}

export function register(payload: RegisterPayload) {
  return post<LoginResponse>('/user/auth/register', payload)
}
