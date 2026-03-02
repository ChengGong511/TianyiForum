export type AuthRole = 'user' | 'admin' | undefined

function clearAllCookies() {
  document.cookie.split(';').forEach((cookie) => {
    const eqIndex = cookie.indexOf('=')
    const name = (eqIndex > -1 ? cookie.slice(0, eqIndex) : cookie).trim()
    if (name) {
      document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`
    }
  })
}

export function clearAuthStorage(role?: AuthRole) {
  if (!role || role === 'user') {
    localStorage.removeItem('user_token')
  }
  if (!role || role === 'admin') {
    localStorage.removeItem('admin_token')
  }
  clearAllCookies()
}
