<template>
  <el-container class="admin-layout">
    <el-aside :width="collapsed ? '64px' : '200px'" class="sidebar">
      <div class="logo">天翼论坛</div>
      <el-menu router :default-active="route.path" :collapse="collapsed" class="menu">
        <el-menu-item index="/admin/dashboard">
          <el-icon>
            <DataAnalysis />
          </el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon>
            <User />
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/posts">
          <el-icon>
            <Document />
          </el-icon>
          <span>帖子管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/complaints">
          <el-icon>
            <Warning />
          </el-icon>
          <span>投诉受理</span>
        </el-menu-item>
        <el-menu-item index="/admin/boards">
          <el-icon>
            <Menu />
          </el-icon>
          <span>板块配置</span>
        </el-menu-item>
        <el-menu-item index="/admin/sensitive-words">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <span>敏感词库</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <el-button link @click="collapsed = !collapsed">
          <el-icon>
            <Expand v-if="collapsed" />
            <Fold v-else />
          </el-icon>
        </el-button>
        <div class="spacer" />
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item>管理端</el-breadcrumb-item>
          <el-breadcrumb-item v-for="c in crumbs" :key="c.path">{{ c.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <el-dropdown>
          <span class="el-dropdown-link">
            管理员
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <div class="page-container">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowDown,
  Menu,
  User,
  Document,
  Warning,
  ChatDotRound,
  DataAnalysis,
  Expand,
  Fold,
} from '@element-plus/icons-vue'
import { clearAuthStorage } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)
const crumbs = computed(() =>
  route.matched
    .filter((r) => r.meta && (r.meta as any).title)
    .map((r) => ({ path: r.path, title: (r.meta as any).title as string })),
)

function logout() {
  clearAuthStorage('admin')
  router.replace({ name: 'admin-login' })
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  border-right: 1px solid var(--el-border-color);
  background: #001529;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu {
  border-right: none;
  background: #001529;
}

.header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--el-border-color);
  background: #fff;
  padding: 0 20px;
}

.spacer {
  flex: 1;
}

.breadcrumb {
  margin-right: 16px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}
</style>
