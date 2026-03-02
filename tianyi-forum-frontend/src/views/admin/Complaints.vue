<template>
	<el-card>
		<template #header>
			<div class="card-header">
				<span>投诉受理</span>
			</div>
		</template>

		<el-form :inline="true" class="mb16">
			<el-form-item label="状态">
				<el-select v-model="query.status" placeholder="全部" clearable style="width: 160px"
					@change="onFilterChange">
					<el-option label="待处理" value="pending" />
					<el-option label="已处理" value="resolved" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-button @click="resetFilter">重置</el-button>
			</el-form-item>
		</el-form>

		<el-table :data="complaints" border style="width: 100%" v-loading="loading">
			<el-table-column prop="id" label="ID" width="80" />
			<el-table-column prop="targetType" label="类型" width="100" />
			<el-table-column prop="reason" label="投诉原因" min-width="220" />
			<el-table-column prop="reporter" label="举报人" width="140" />
			<el-table-column prop="status" label="状态" width="120">
				<template #default="{ row }">
					<el-tag :type="row.status === 'pending' ? 'warning' : 'success'">
						{{ row.status === 'pending' ? '待处理' : '已处理' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createdAt" label="举报时间" width="180" />
			<el-table-column label="操作" width="240" fixed="right">
				<template #default="{ row }">
					<el-button v-if="row.status === 'pending'" type="primary" size="small" text
						@click="openHandle(row)">
						处理
					</el-button>
					<el-button size="small" text @click="viewDetail(row)">查看</el-button>
					<el-button v-if="row.targetType === 'post'" size="small" text @click="viewPost(row.targetId)">
						查看帖子
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-empty v-if="!loading && complaints.length === 0" description="暂无数据" />

		<div class="mt16" style="text-align: right">
			<el-pagination background layout="total, sizes, prev, pager, next" :total="total"
				:page-size="query.pageSize" :page-sizes="[10, 20, 50]" :current-page="query.page"
				@current-change="onPageChange" @size-change="onSizeChange" />
		</div>

		<el-drawer v-model="detailVisible" title="投诉详情" size="40%">
			<div v-if="detailLoading" class="drawer-loading">加载中...</div>
			<div v-else-if="detailData">
				<el-descriptions :column="1" border>
					<el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
					<el-descriptions-item label="对象">{{ detailData.targetType }} #{{ detailData.targetId
						}}</el-descriptions-item>
					<el-descriptions-item label="举报人">{{ detailData.reporter }}</el-descriptions-item>
					<el-descriptions-item label="状态">{{
						detailData.status === 'pending' ? '待处理' : '已处理'
					}}</el-descriptions-item>
					<el-descriptions-item label="原因">{{ detailData.reason }}</el-descriptions-item>
					<el-descriptions-item label="处理说明">{{
						detailData.handleNote || '-'
					}}</el-descriptions-item>
					<el-descriptions-item label="举报时间">{{ detailData.createdAt }}</el-descriptions-item>
					<el-descriptions-item label="处理时间">{{
						detailData.handledAt || '-'
					}}</el-descriptions-item>
				</el-descriptions>
			</div>
			<div v-else class="drawer-loading">暂无内容</div>
		</el-drawer>

		<el-dialog v-model="handleDialogVisible" title="处理投诉" width="480px">
			<el-form label-width="100px">
				<el-form-item label="处理结果">
					<el-select v-model="handleForm.action" style="width: 240px">
						<el-option label="投诉不成立" value="dismiss" />
						<el-option label="下架/封禁帖子" value="takedown" />
						<el-option label="驳回帖子" value="reject_post" />
					</el-select>
				</el-form-item>
				<el-form-item label="处理说明">
					<el-input type="textarea" v-model="handleForm.handleNote" placeholder="填写处理原因" rows="3" />
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="handleDialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="handleSubmitting" @click="submitHandle">提交</el-button>
			</template>
		</el-dialog>

		<!-- 帖子详情对话框 -->
		<el-dialog v-model="postDetailVisible" title="帖子详情" width="720px">
			<div v-if="postDetailLoading" style="text-align: center; padding: 40px">
				<el-icon class="is-loading" style="font-size: 24px">
					<Loading />
				</el-icon>
				<p style="margin-top: 12px; color: #999">加载中...</p>
			</div>
			<div v-else-if="postDetail">
				<el-descriptions :column="1" border>
					<el-descriptions-item label="帖子ID">{{ postDetail.id }}</el-descriptions-item>
					<el-descriptions-item label="标题">{{ postDetail.title }}</el-descriptions-item>
					<el-descriptions-item label="作者">{{ postDetail.author }}</el-descriptions-item>
					<el-descriptions-item label="状态">
						<el-tag v-if="postDetail.status === 'normal'" type="success">正常</el-tag>
						<el-tag v-else-if="postDetail.status === 'pending'" type="warning">待审核</el-tag>
						<el-tag v-else type="info">已归档</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="热度分数">{{ postDetail.hotScore || 0 }}</el-descriptions-item>
					<el-descriptions-item label="违规分数">{{ postDetail.violationScore || 0 }}</el-descriptions-item>
					<el-descriptions-item label="发布时间">{{ postDetail.createdAt }}</el-descriptions-item>
				</el-descriptions>
				<el-divider />
				<div
					style="padding: 16px; background: #f5f7fa; border-radius: 8px; max-height: 400px; overflow-y: auto">
					<h4 style="margin: 0 0 12px; color: #606266">帖子内容：</h4>
					<div style="white-space: pre-wrap; line-height: 1.6; color: #303133">{{ postDetail.content }}</div>
				</div>
			</div>
			<div v-else style="text-align: center; padding: 40px; color: #999">暂无内容</div>
		</el-dialog>
	</el-card>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { listComplaints, getComplaint, resolveComplaint } from '@/api/admin/complaints'
import type { Complaint, ComplaintHandleDTO } from '@/api/admin/complaints'
import { getPostDetail } from '@/api/admin/posts'
import type { Post } from '@/api/admin/posts'

defineOptions({ name: 'AdminComplaints' })

const query = reactive<{ page: number; pageSize: number; status: '' | 'pending' | 'resolved' }>({
	page: 1,
	pageSize: 10,
	status: '',
})

const complaints = ref<Complaint[]>([])
const total = ref(0)
const loading = ref(false)

const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<Complaint | null>(null)

const handleDialogVisible = ref(false)
const handleSubmitting = ref(false)
const currentComplaintId = ref<number | null>(null)
const handleForm = reactive<ComplaintHandleDTO>({ action: 'takedown', handleNote: '' })

const postDetailVisible = ref(false)
const postDetailLoading = ref(false)
const postDetail = ref<Post | null>(null)

async function fetchList() {
	loading.value = true
	try {
		const res = await listComplaints({
			page: query.page,
			pageSize: query.pageSize,
			...(query.status ? { status: query.status } : {}),
		})
		complaints.value = res.items
		total.value = res.total
	} finally {
		loading.value = false
	}
}

watch(() => [query.status, query.page, query.pageSize], fetchList, { immediate: true })

function onPageChange(p: number) {
	query.page = p
}

function onSizeChange(s: number) {
	query.pageSize = s
	query.page = 1
}

function onFilterChange() {
	query.page = 1
}

function resetFilter() {
	query.status = ''
	query.page = 1
}

async function viewDetail(row: Complaint) {
	detailVisible.value = true
	detailLoading.value = true
	try {
		detailData.value = await getComplaint(row.id)
	} finally {
		detailLoading.value = false
	}
}

function openHandle(row: Complaint) {
	currentComplaintId.value = row.id
	handleForm.action = 'takedown'
	handleForm.handleNote = ''
	handleDialogVisible.value = true
}

async function submitHandle() {
	if (!currentComplaintId.value) return
	handleSubmitting.value = true
	try {
		await resolveComplaint(currentComplaintId.value, { ...handleForm })
		ElMessage.success('处理成功')
		handleDialogVisible.value = false
		fetchList()
	} catch (e) {
		ElMessage.error('处理失败：' + (e as Error).message)
	} finally {
		handleSubmitting.value = false
	}
}

async function viewPost(postId: number) {
	postDetailVisible.value = true
	postDetailLoading.value = true
	postDetail.value = null
	try {
		postDetail.value = await getPostDetail(postId)
	} catch (e) {
		ElMessage.error('加载帖子失败：' + (e as Error).message)
		postDetailVisible.value = false
	} finally {
		postDetailLoading.value = false
	}
}
</script>

<style scoped>
.card-header {
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
</style>
