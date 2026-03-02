import { get } from '../http'

export interface UserProfile {
  id: number
  username: string
  email: string
  avatarUrl?: string
  createdAt?: string
}

export function getProfile() {
  return get<UserProfile>('/user/profile')
}
