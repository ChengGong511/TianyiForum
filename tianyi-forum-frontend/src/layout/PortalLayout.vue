<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="brand" @click="goHome">💬 天翼论坛</div>
      <!-- 只要有 token 就显示导航栏 -->
      <nav class="nav" v-if="isLoggedIn">
        <RouterLink to="/" class="nav-item">
          <span class="nav-icon">🏠</span>
          <span>帖子广场</span>
        </RouterLink>
        <RouterLink to="/profile" class="nav-item">
          <span class="nav-icon">👤</span>
          <span>个人主页</span>
        </RouterLink>
      </nav>
      <div class="auth-box">
        <template v-if="isLoggedIn">
          <div class="user-info">
            <span class="user-avatar">👤</span>
            <span class="user-name">{{ user?.username || '用户' }}</span>
          </div>
          <button class="logout-btn" @click="logout">退出</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="login-link">登录</RouterLink>
        </template>
      </div>
    </header>
    <main class="container">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter, useRoute, RouterLink, RouterView } from 'vue-router'
import { getProfile, type UserProfile } from '@/api/portal/profile'
import { clearAuthStorage } from '@/utils/auth'

const user = ref<UserProfile | null>(null)
const isLoggedIn = ref(false)
const router = useRouter()
const route = useRoute()

// 检查登录状态
function checkLoginStatus() {
  isLoggedIn.value = !!localStorage.getItem('user_token')
  console.log('[PortalLayout] Login status:', isLoggedIn.value)
}

// 监听路由变化，刷新用户状态
watch(
  () => route.path,
  () => {
    console.log('[PortalLayout] Route changed to:', route.path)
    checkLoginStatus()
    void refreshProfile()
  }
)

function goHome() {
  if (isLoggedIn.value) {
    router.push('/')
  }
}

async function refreshProfile() {
  const token = localStorage.getItem('user_token')
  console.log('[PortalLayout] refreshProfile called, token exists:', !!token)

  if (!token) {
    user.value = null
    localStorage.removeItem('user_info')
    return
  }

  // 优先从 localStorage 读取用户信息（立即显示导航）
  const cachedUser = localStorage.getItem('user_info')
  if (cachedUser) {
    try {
      const parsed = JSON.parse(cachedUser) as UserProfile
      user.value = parsed
      console.log('[PortalLayout] Loaded user from cache:', parsed.username)
    } catch {
      console.log('[PortalLayout] Failed to parse cached user')
    }
  }

  // 后台刷新用户信息
  try {
    const profile = await getProfile()
    user.value = profile
    localStorage.setItem('user_info', JSON.stringify(profile))
    console.log('[PortalLayout] Updated user from API:', profile.username)
  } catch (err) {
    console.error('[PortalLayout] Failed to fetch profile:', err)
    // 只有在没有缓存用户时才清除
    if (!user.value) {
      localStorage.removeItem('user_info')
    }
  }
}

function logout() {
  clearAuthStorage('user')
  localStorage.removeItem('user_info')
  user.value = null
  isLoggedIn.value = false
  router.replace({ name: 'portal-login', query: { redirect: '/' } })
}

const storageHandler = (event: StorageEvent) => {
  if (event.key === 'user_token') {
    checkLoginStatus()
    void refreshProfile()
  }
}

const loginHandler = () => {
  console.log('[PortalLayout] user-login event received')
  checkLoginStatus()
  void refreshProfile()
}

onMounted(() => {
  console.log('[PortalLayout] Component mounted')
  checkLoginStatus()
  void refreshProfile()
  window.addEventListener('storage', storageHandler)
  window.addEventListener('user-login', loginHandler)
})

onBeforeUnmount(() => {
  window.removeEventListener('storage', storageHandler)
  window.removeEventListener('user-login', loginHandler)
})
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  background: radial-gradient(120% 120% at 20% 20%, #eef2ff 0%, #f8fafc 40%, #f9fafb 100%);
  color: #222;
}

.portal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  font-weight: 700;
  font-size: 18px;
  cursor: pointer;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: transform 0.2s;
}

.brand:hover {
  transform: scale(1.02);
}

.nav {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nav-item {
  color: #4b5563;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 10px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.nav-icon {
  font-size: 16px;
}

.nav-item:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.nav-item.router-link-active {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

.container {
  padding: 20px 16px 32px;
  max-width: 1100px;
  margin: 0 auto;
}

.auth-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.user-avatar {
  font-size: 18px;
}

.user-name {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.logout-btn {
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #dc2626;
  cursor: pointer;
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  font-weight: 500;
}

.logout-btn:hover {
  background: #fef2f2;
  border-color: #fecaca;
  color: #991b1b;
}

.login-link {
  color: #2563eb;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;
}

.login-link:hover {
  background: #eff6ff;
}
</style>
