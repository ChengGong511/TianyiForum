<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>帖子管理</span>
      </div>
    </template>
    <el-form :inline="true" :model="query" class="mb16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="标题/作者" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
          <el-option label="正常" value="normal" />
          <el-option label="待审" value="pending" />
          <el-option label="下架" value="archived" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序">
        <el-select v-model="query.sortBy" style="width: 140px">
          <el-option label="按热度" value="hot" />
          <el-option label="按违规权重" value="violation" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border style="width: 100%" size="small">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="author" label="作者" width="140" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hotScore" label="热度" width="100" />
      <el-table-column prop="violationScore" label="违规权重" width="120" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="340" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="showDetail(row)">详情</el-button>
          <el-button size="small" text @click="audit(row, 'top')">置顶</el-button>
          <el-button size="small" text @click="audit(row, 'featured')">加精</el-button>
          <el-button type="warning" size="small" text @click="audit(row, 'reject')">驳回</el-button>
          <el-button type="danger" size="small" text @click="audit(row, 'ban')">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && tableData.length === 0" description="暂无数据" />

    <div class="mt16" style="text-align: right">
      <el-pagination background layout="total, sizes, prev, pager, next" :total="total" :page-size="pageSize"
        :page-sizes="[10, 20, 50]" :current-page="page" @current-change="onPageChange" @size-change="onSizeChange" />
    </div>

    <el-drawer v-model="detailVisible" title="帖子详情" size="50%" :with-header="true">
      <div v-if="detailLoading" class="drawer-loading">加载中...</div>
      <div v-else-if="selectedPost">
        <h3 class="post-title">{{ selectedPost.title }}</h3>
        <div class="meta">作者：{{ selectedPost.author }} | 状态：{{ statusText(selectedPost.status) }} | 创建时间：{{ selectedPost.createdAt }}</div>
        <el-divider />
        <div class="post-content">{{ selectedPost.content }}</div>
      </div>
      <div v-else class="drawer-loading">暂无内容</div>
    </el-drawer>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Post, PostReviewDTO } from '@/api/admin/posts'
import { listPosts, reviewPost, getPostDetail } from '@/api/admin/posts'

const query = reactive<{
  keyword?: string
  status?: 'normal' | 'pending' | 'archived' | ''
  sortBy: 'hot' | 'violation'
}>({
  keyword: '',
  status: '',
  sortBy: 'hot',
})
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<Post[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const selectedPost = ref<Post | null>(null)

const params = computed(() => ({
  keyword: query.keyword || undefined,
  status: (query.status || undefined) as 'normal' | 'pending' | 'archived' | undefined,
  sortBy: query.sortBy,
  page: page.value,
  pageSize: pageSize.value,
}))

async function fetchList() {
  loading.value = true
  try {
    const res = await listPosts(params.value)
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
  query.sortBy = 'hot'
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

function statusText(s: 'normal' | 'pending' | 'archived') {
  return s === 'normal' ? '正常' : s === 'pending' ? '待审' : '下架'
}
function statusTag(s: 'normal' | 'pending' | 'archived') {
  return s === 'normal' ? 'success' : s === 'pending' ? 'warning' : 'info'
}

async function audit(row: Post, action: 'top' | 'featured' | 'reject' | 'ban') {
  try {
    let reason: string | undefined
    if (action === 'reject' || action === 'ban') {
      try {
        const { value } = await ElMessageBox.prompt('请输入处理原因', '说明', {
          confirmButtonText: '提交',
          cancelButtonText: '取消',
          inputPlaceholder: '填写驳回/下架原因',
          inputValidator: (val) => (!!val && val.trim().length > 0) || '原因不能为空',
        })
        reason = value
      } catch (err) {
        void err
        return
      }
    }
    const payload: PostReviewDTO = { action, reason }
    await reviewPost(row.id, payload)
    ElMessage.success('操作成功')
    fetchList()
  } catch (e) {
    ElMessage.error('操作失败：' + (e as Error).message)
  }
}

async function showDetail(row: Post) {
  detailVisible.value = true
  detailLoading.value = true
  try {
    selectedPost.value = await getPostDetail(row.id)
  } finally {
    detailLoading.value = false
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

.drawer-loading {
  color: #666;
  padding: 16px;
}

.post-title {
  margin: 0 0 8px;
}

.meta {
  color: #666;
  font-size: 13px;
}

.post-content {
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
