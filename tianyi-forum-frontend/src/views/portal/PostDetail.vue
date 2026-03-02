<template>
	<div class="page">
		<button class="back-btn" @click="$router.back()">
			<span>←</span> 返回列表
		</button>

		<div class="card post-card" v-if="post">
			<div class="post-header">
				<h1 class="post-title">{{ post.title }}</h1>
				<div class="badges">
					<span v-if="post.isTop" class="badge badge-top">置顶</span>
					<span v-if="post.isFeatured" class="badge badge-hot">精选</span>
				</div>
			</div>
			<div class="meta">
				<span class="pill">{{ post.boardName || '未知板块' }}</span>
				<span class="author-info">作者：{{ post.author }}</span>
				<span class="time-info">{{ formatTime(post.createdAt) }}</span>
				<span v-if="post.hotScore" class="hot-score">🔥 {{ post.hotScore }}</span>
			</div>
			<article class="content" v-html="post.content"></article>
			<div class="post-actions">
				<button class="btn-complaint" @click="showComplaintDialog = true">🚨 投诉此帖</button>
			</div>
		</div>
		<div v-else class="loading-hint">
			<div class="spinner"></div>
			加载中...
		</div>

		<!-- 投诉对话框 -->
		<div v-if="showComplaintDialog" class="modal-overlay" @click="showComplaintDialog = false">
			<div class="modal-dialog" @click.stop>
				<div class="modal-header">
					<h3>投诉帖子</h3>
					<button class="modal-close" @click="showComplaintDialog = false">×</button>
				</div>
				<div class="modal-body">
					<div class="field">
						<label class="field-label">投诉原因</label>
						<textarea v-model="complaintReason" class="field-textarea" placeholder="请描述违规内容..."
							rows="4"></textarea>
					</div>
					<div class="field">
						<label class="field-label">证据（可选）</label>
						<textarea v-model="complaintEvidence" class="field-textarea" placeholder="可提供相关证据链接或说明..."
							rows="3"></textarea>
					</div>
					<div v-if="complaintError" class="error-msg">{{ complaintError }}</div>
				</div>
				<div class="modal-footer">
					<button class="btn-cancel" @click="showComplaintDialog = false">取消</button>
					<button class="btn-submit" @click="submitComplaint" :disabled="complaintSubmitting">
						{{ complaintSubmitting ? '提交中...' : '提交投诉' }}
					</button>
				</div>
			</div>
		</div>

		<div class="card comments-card">
			<div class="comments-header">
				<h3>评论 <span class="count" v-if="comments.total">({{ comments.total }})</span></h3>
			</div>

			<div class="comment-form-section">
				<textarea v-model="commentText" rows="3" class="comment-input" placeholder="发表你的看法..."></textarea>
				<div class="form-actions">
					<div v-if="error" class="error-msg">{{ error }}</div>
					<button class="btn-submit" @click="submitComment">发表评论</button>
				</div>
			</div>

			<div v-if="commentLoading" class="loading-hint">
				<div class="spinner"></div>
				加载评论中...
			</div>
			<div v-else-if="!comments.items.length" class="empty-hint">
				暂无评论，快来抢沙发吧！
			</div>
			<ul class="comment-list" v-else>
				<li v-for="c in comments.items" :key="c.id" class="comment-item">
					<div class="comment-header">
						<span class="comment-author">{{ c.author }}</span>
						<span class="comment-time">{{ formatTime(c.createdAt) }}</span>
					</div>
					<div class="comment-body">{{ c.content }}</div>
				</li>
			</ul>

			<div class="pager" v-if="comments.total > pageSize">
				<button class="pager-btn" :disabled="page === 1" @click="changePage(page - 1)">
					上一页
				</button>
				<span class="pager-info">{{ page }} / {{ totalPages }}</span>
				<button class="pager-btn" :disabled="page === totalPages" @click="changePage(page + 1)">
					下一页
				</button>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostDetail } from '@/api/portal/posts'
import { listComments, createComment, type CommentItem } from '@/api/portal/comments'
import { createComplaint } from '@/api/portal/complaints'

const route = useRoute()
const router = useRouter()
const post = ref<any>(null)
const comments = reactive<{ items: CommentItem[]; total: number }>({ items: [], total: 0 })
const page = ref(1)
const pageSize = 10
const commentLoading = ref(false)
const commentText = ref('')
const error = ref('')

// 投诉相关状态
const showComplaintDialog = ref(false)
const complaintReason = ref('')
const complaintEvidence = ref('')
const complaintError = ref('')
const complaintSubmitting = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(comments.total / pageSize)))

onMounted(async () => {
	if (!localStorage.getItem('user_token')) {
		router.replace({ name: 'portal-login', query: { redirect: route.fullPath } })
		return
	}
	const id = Number(route.params.id)
	post.value = await getPostDetail(id)
	await fetchComments()
})

async function fetchComments() {
	commentLoading.value = true
	try {
		const id = Number(route.params.id)
		const res = await listComments(id, { page: page.value, pageSize })
		comments.items = res.items
		comments.total = res.total
	} finally {
		commentLoading.value = false
	}
}

function formatTime(text?: string) {
	if (!text) return ''
	const date = new Date(text)
	const now = new Date()
	const diff = now.getTime() - date.getTime()
	const minutes = Math.floor(diff / 60000)
	const hours = Math.floor(diff / 3600000)
	const days = Math.floor(diff / 86400000)

	if (minutes < 1) return '刚刚'
	if (minutes < 60) return `${minutes}分钟前`
	if (hours < 24) return `${hours}小时前`
	if (days < 7) return `${days}天前`
	return date.toLocaleDateString()
}

function changePage(next: number) {
	page.value = next
	fetchComments()
}

async function submitComment() {
	error.value = ''
	if (!localStorage.getItem('user_token')) {
		router.replace({ name: 'portal-login', query: { redirect: route.fullPath } })
		return
	}
	if (!commentText.value.trim()) {
		error.value = '请输入评论内容'
		return
	}
	try {
		const id = Number(route.params.id)
		await createComment(id, { content: commentText.value })
		commentText.value = ''
		page.value = 1
		await fetchComments()
	} catch (e: unknown) {
		const errorMessage = e instanceof Error ? e.message : '发表失败，需登录'
		error.value = errorMessage
	}
}

async function submitComplaint() {
	complaintError.value = ''
	if (!localStorage.getItem('user_token')) {
		router.replace({ name: 'portal-login', query: { redirect: route.fullPath } })
		return
	}
	if (!complaintReason.value.trim()) {
		complaintError.value = '请输入投诉原因'
		return
	}
	complaintSubmitting.value = true
	try {
		const id = Number(route.params.id)
		await createComplaint({
			targetType: 'post',
			targetId: id,
			reason: complaintReason.value.trim(),
			evidence: complaintEvidence.value.trim() || undefined
		})
		showComplaintDialog.value = false
		complaintReason.value = ''
		complaintEvidence.value = ''
		alert('投诉提交成功，我们会尽快处理')
	} catch (e: unknown) {
		const errorMessage = e instanceof Error ? e.message : '提交失败'
		complaintError.value = errorMessage
	} finally {
		complaintSubmitting.value = false
	}
}
</script>

<style scoped>
.page {
	max-width: 900px;
	margin: 0 auto;
	padding: 20px;
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.back-btn {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 8px 14px;
	border: 1px solid #d1d5db;
	border-radius: 8px;
	background: #fff;
	color: #374151;
	cursor: pointer;
	font-size: 14px;
	transition: all 0.2s;
	width: fit-content;
}

.back-btn:hover {
	background: #f9fafb;
	border-color: #9ca3af;
}

.card {
	background: #fff;
	border: 1px solid #e5e7eb;
	border-radius: 16px;
	padding: 24px;
	box-shadow: 0 10px 24px rgba(0, 0, 0, 0.04);
}

.post-card {
	padding: 28px;
}

.post-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 16px;
	margin-bottom: 12px;
}

.post-title {
	margin: 0;
	font-size: 26px;
	color: #111827;
	line-height: 1.3;
	flex: 1;
}

.badges {
	display: flex;
	gap: 8px;
	flex-shrink: 0;
}

.badge {
	padding: 4px 10px;
	border-radius: 6px;
	font-size: 12px;
	font-weight: 600;
	white-space: nowrap;
}

.badge-top {
	background: #fee2e2;
	color: #dc2626;
}

.badge-hot {
	background: #fef3c7;
	color: #d97706;
}

.meta {
	color: #6b7280;
	font-size: 14px;
	margin-bottom: 20px;
	display: flex;
	gap: 14px;
	flex-wrap: wrap;
	align-items: center;
	padding-bottom: 16px;
	border-bottom: 1px solid #f3f4f6;
}

.pill {
	padding: 4px 10px;
	border-radius: 999px;
	background: #eef2ff;
	color: #4338ca;
	font-size: 13px;
	font-weight: 500;
}

.author-info {
	color: #374151;
	font-weight: 500;
}

.time-info {
	color: #9ca3af;
}

.hot-score {
	color: #ea580c;
	font-weight: 600;
}

.content {
	line-height: 1.8;
	color: #1f2937;
	font-size: 15px;
	white-space: pre-wrap;
	word-wrap: break-word;
}

.post-actions {
	margin-top: 24px;
	padding-top: 20px;
	border-top: 1px solid #f3f4f6;
	display: flex;
	justify-content: flex-end;
	gap: 12px;
}

.btn-complaint {
	padding: 8px 16px;
	border: 1px solid #dc2626;
	border-radius: 8px;
	background: #fff;
	color: #dc2626;
	cursor: pointer;
	font-size: 14px;
	font-weight: 500;
	transition: all 0.2s;
}

.btn-complaint:hover {
	background: #fef2f2;
	border-color: #b91c1c;
}

.modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 1000;
}

.modal-dialog {
	background: #fff;
	border-radius: 16px;
	width: 90%;
	max-width: 500px;
	box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20px 24px;
	border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
	margin: 0;
	font-size: 18px;
	color: #111827;
}

.modal-close {
	background: none;
	border: none;
	font-size: 32px;
	color: #9ca3af;
	cursor: pointer;
	line-height: 1;
	padding: 0;
	width: 32px;
	height: 32px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal-close:hover {
	color: #374151;
}

.modal-body {
	padding: 24px;
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.field {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.field-label {
	font-weight: 600;
	font-size: 14px;
	color: #374151;
}

.field-textarea {
	padding: 10px 12px;
	border: 1px solid #d1d5db;
	border-radius: 8px;
	font-size: 14px;
	font-family: inherit;
	resize: vertical;
	transition: border-color 0.2s, box-shadow 0.2s;
}

.field-textarea:focus {
	outline: none;
	border-color: #2563eb;
	box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.modal-footer {
	padding: 16px 24px;
	border-top: 1px solid #e5e7eb;
	display: flex;
	justify-content: flex-end;
	gap: 12px;
}

.btn-cancel {
	padding: 8px 16px;
	border: 1px solid #d1d5db;
	border-radius: 8px;
	background: #fff;
	color: #374151;
	cursor: pointer;
	font-size: 14px;
	transition: all 0.2s;
}

.btn-cancel:hover {
	background: #f9fafb;
}

.loading-hint,
.empty-hint {
	color: #9ca3af;
	text-align: center;
	padding: 40px 20px;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12px;
}

.spinner {
	width: 20px;
	height: 20px;
	border: 3px solid #e5e7eb;
	border-top-color: #2563eb;
	border-radius: 50%;
	animation: spin 0.8s linear infinite;
}

@keyframes spin {
	to {
		transform: rotate(360deg);
	}
}

.comments-card {
	padding: 24px;
}

.comments-header {
	margin-bottom: 20px;
	padding-bottom: 12px;
	border-bottom: 1px solid #f3f4f6;
}

.comments-header h3 {
	margin: 0;
	font-size: 20px;
	color: #111827;
}

.count {
	color: #9ca3af;
	font-weight: 400;
	font-size: 16px;
}

.comment-form-section {
	margin-bottom: 24px;
	padding: 16px;
	background: #f9fafb;
	border-radius: 12px;
}

.comment-input {
	width: 100%;
	padding: 12px;
	border: 1px solid #d1d5db;
	border-radius: 10px;
	font-size: 15px;
	font-family: inherit;
	resize: vertical;
	background: #fff;
	transition: border-color 0.2s, box-shadow 0.2s;
}

.comment-input:focus {
	outline: none;
	border-color: #2563eb;
	box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-actions {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 10px;
	gap: 12px;
}

.error-msg {
	color: #dc2626;
	font-size: 14px;
	flex: 1;
}

.btn-submit {
	background: #2563eb;
	color: #fff;
	border: none;
	padding: 10px 20px;
	border-radius: 10px;
	cursor: pointer;
	font-size: 14px;
	font-weight: 500;
	transition: all 0.2s;
}

.btn-submit:hover {
	background: #1d4ed8;
}

.comment-list {
	list-style: none;
	padding: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.comment-item {
	padding: 16px;
	border: 1px solid #e5e7eb;
	border-radius: 12px;
	background: #fafbff;
	transition: all 0.2s;
}

.comment-item:hover {
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.comment-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 8px;
}

.comment-author {
	font-weight: 600;
	color: #374151;
	font-size: 14px;
}

.comment-time {
	color: #9ca3af;
	font-size: 12px;
}

.comment-body {
	white-space: pre-wrap;
	word-wrap: break-word;
	color: #1f2937;
	line-height: 1.6;
	font-size: 14px;
}

.pager {
	display: flex;
	justify-content: flex-end;
	gap: 12px;
	align-items: center;
	margin-top: 20px;
	padding-top: 16px;
	border-top: 1px solid #f3f4f6;
}

.pager-btn {
	padding: 8px 16px;
	border: 1px solid #e5e7eb;
	border-radius: 8px;
	background: #fff;
	cursor: pointer;
	font-size: 14px;
	transition: all 0.2s;
	color: #374151;
}

.pager-btn:hover:not(:disabled) {
	background: #f9fafb;
	border-color: #d1d5db;
}

.pager-btn:disabled {
	opacity: 0.4;
	cursor: not-allowed;
}

.pager-info {
	color: #6b7280;
	font-size: 14px;
}
</style>
