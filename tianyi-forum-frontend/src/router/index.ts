import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layout/AdminLayout.vue'
import PortalLayout from '@/layout/PortalLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: PortalLayout,
      children: [
        {
          path: '',
          name: 'portal-home',
          meta: { title: '首页', requiresUser: true },
          component: () => import('@/views/portal/Home.vue'),
        },
        {
          path: 'posts/create',
          name: 'portal-create-post',
          meta: { title: '发布帖子', requiresUser: true },
          component: () => import('@/views/portal/CreatePost.vue'),
        },
        {
          path: 'posts/:id',
          name: 'portal-post-detail',
          meta: { title: '帖子详情', requiresUser: true },
          component: () => import('@/views/portal/PostDetail.vue'),
        },
        {
          path: 'login',
          name: 'portal-login',
          meta: { title: '登录' },
          component: () => import('@/views/portal/Login.vue'),
        },
        {
          path: 'register',
          name: 'portal-register',
          meta: { title: '注册' },
          component: () => import('@/views/portal/Register.vue'),
        },
        {
          path: 'profile',
          name: 'portal-profile',
          meta: { title: '我的', requiresUser: true },
          component: () => import('@/views/portal/Profile.vue'),
        },
      ],
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/dashboard' },
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          meta: { requiresAdmin: true, title: '数据看板' },
          component: () => import('@/views/admin/AdminDashboard.vue'),
        },
        {
          path: 'users',
          name: 'admin-users',
          meta: { requiresAdmin: true, title: '用户管理' },
          component: () => import('@/views/admin/UsersList.vue'),
        },
        {
          path: 'posts',
          name: 'admin-posts',
          meta: { requiresAdmin: true, title: '帖子管理' },
          component: () => import('@/views/admin/PostsList.vue'),
        },
        {
          path: 'complaints',
          name: 'admin-complaints',
          meta: { requiresAdmin: true, title: '投诉受理' },
          component: () => import('@/views/admin/Complaints.vue'),
        },
        {
          path: 'boards',
          name: 'admin-boards',
          meta: { requiresAdmin: true, title: '板块配置' },
          component: () => import('@/views/admin/BoardConfig.vue'),
        },
        {
          path: 'sensitive-words',
          name: 'admin-sensitive-words',
          meta: { requiresAdmin: true, title: '敏感词库' },
          component: () => import('@/views/admin/SensitiveWords.vue'),
        },
      ],
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      meta: { requiresAdmin: false, title: '管理员登录' },
      component: () => import('@/views/admin/AdminLogin.vue'),
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  const adminToken = localStorage.getItem('admin_token')
  const userToken = localStorage.getItem('user_token')

  // 如果访问管理端，清除用户token，防止混用
  if (to.path.startsWith('/admin') && to.name !== 'admin-login') {
    if (userToken && !adminToken) {
      localStorage.removeItem('user_token')
    }
    if (!adminToken) {
      return { name: 'admin-login', query: { redirect: to.fullPath } }
    }
  }

  // 如果访问用户端需要登录的页面，清除管理员token
  if (to.meta.requiresUser) {
    if (adminToken && !userToken) {
      localStorage.removeItem('admin_token')
    }
    if (!userToken) {
      return { name: 'portal-login', query: { redirect: to.fullPath } }
    }
  }

  // 管理员登录页面重定向
  if (to.name === 'admin-login' && adminToken) {
    return { path: '/admin' }
  }

  // 用户登录页面重定向
  if (to.name === 'portal-login' && userToken) {
    return { path: '/' }
  }

  const title = to.meta?.title as string | undefined
  document.title = title ? `天翼论坛 - ${title}` : '天翼论坛'
})

export default router
