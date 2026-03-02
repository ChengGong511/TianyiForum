<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户管理</span>
      </div>
    </template>
    <el-form :inline="true" :model="query" class="mb16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="用户名/邮箱" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="正常" value="active" />
          <el-option label="封禁" value="banned" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border style="width: 100%" size="small">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" min-width="180" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : row.role === 'moderator' ? 'warning' : 'info'">
            {{ row.role === 'admin' ? '管理员' : row.role === 'moderator' ? '版主' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? '正常' : '封禁' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'active'" type="danger" size="small" text @click="toggleBan(row)">
            封禁
          </el-button>
          <el-button v-else type="success" size="small" text @click="toggleBan(row)">解封</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && tableData.length === 0" description="暂无数据" />

    <div class="mt16" style="text-align: right">
      <el-pagination background layout="total, sizes, prev, pager, next" :total="total" :page-size="pageSize"
        :page-sizes="[10, 20, 50]" :current-page="page" @current-change="onPageChange" @size-change="onSizeChange" />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { User } from '@/api/admin/users'
import { listUsers, updateUserStatus } from '@/api/admin/users'

const query = reactive<{ keyword?: string; status?: 'active' | 'banned' | '' }>({
  keyword: '',
  status: '',
})
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<User[]>([])
const loading = ref(false)

const params = computed(() => ({
  keyword: query.keyword || undefined,
  status: (query.status || undefined) as 'active' | 'banned' | undefined,
  page: page.value,
  pageSize: pageSize.value,
}))

async function fetchList() {
  loading.value = true
  try {
    const res = await listUsers(params.value)
    tableData.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}
watch(params, fetchList, { immediate: true })

function onSearch() {
  page.value = 1
  fetchList()
}
function onReset() {
  query.keyword = ''
  query.status = ''
  page.value = 1
  fetchList()
}
function onPageChange(p: number) {
  page.value = p
}
function onSizeChange(s: number) {
  pageSize.value = s
  page.value = 1
}

async function toggleBan(row: User) {
  const next = row.status === 'active' ? 'banned' : 'active'
  try {
    await updateUserStatus(row.id, next)
    ElMessage.success(next === 'banned' ? '已封禁用户' : '已解封用户')
    fetchList()
  } catch (e) {
    ElMessage.error('操作失败：' + (e as Error).message)
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.mb16 {
  margin-bottom: 16px;
}

.mt16 {
  margin-top: 16px;
}
</style>
