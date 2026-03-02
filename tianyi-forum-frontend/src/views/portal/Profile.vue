<template>
	<div class="page">
		<div class="profile-header" v-if="profile">
			<div class="avatar-section">
				<div class="avatar">👤</div>
				<div class="user-basic">
					<h1 class="username">{{ profile.username }}</h1>
					<p class="email">{{ profile.email }}</p>
				</div>
			</div>
		</div>

		<div class="card" v-if="profile">
			<div class="card-header">
				<h3>📊 账户信息</h3>
			</div>
			<div class="info-grid">
				<div class="info-item">
					<span class="info-label">🆔 用户ID</span>
					<span class="info-value">{{ profile.id }}</span>
				</div>
				<div class="info-item">
					<span class="info-label">📧 邮箱</span>
					<span class="info-value">{{ profile.email }}</span>
				</div>
				<div class="info-item">
					<span class="info-label">📅 注册时间</span>
					<span class="info-value">{{ formatTime(profile.createdAt) }}</span>
				</div>
			</div>
		</div>
		<div v-else class="loading">
			<div class="spinner"></div>
			加载中...
		</div>

		<div class="card" v-if="profile">
			<div class="card-header">
				<h3>📝 我的帖子</h3>
				<button class="btn-create" @click="goCreate">发布新帖</button>
			</div>
			<ul class="post-list" v-if="myPosts.items.length">
				<li v-for="p in myPosts.items" :key="p.id" @click="goToPost(p.id)">
					<div class="post-content">
						<div class="post-title">{{ p.title }}</div>
						<div class="post-meta">
							<span class="board-tag">{{ p.boardName }}</span>
							<span class="post-time">{{ formatTime(p.createdAt) }}</span>
							<span v-if="p.hotScore" class="hot-score">🔥 {{ p.hotScore }}</span>
						</div>
					</div>
				</li>
			</ul>
			<div class="empty-state" v-else>
				<div class="empty-icon">📭</div>
				<p>还没有发布过帖子</p>
				<button class="btn-create-primary" @click="goCreate">发布第一个帖子</button>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getProfile, type UserProfile } from '@/api/portal/profile'
import { listPosts, type PostSummary } from '@/api/portal/posts'
import { clearAuthStorage } from '@/utils/auth'

const profile = ref<UserProfile | null>(null)
const myPosts = reactive<{ items: PostSummary[]; total: number }>({ items: [], total: 0 })
const router = useRouter()

onMounted(async () => {
	try {
		profile.value = await getProfile()
		await loadMyPosts()
	} catch (e) {
		router.push({ name: 'portal-login', query: { redirect: '/profile' } })
	}
})

function goCreate() {
	router.push('/posts/create')
}

function goToPost(id: number) {
	router.push(`/posts/${id}`)
}

function logout() {
	clearAuthStorage('user')
	localStorage.removeItem('user_info')
	router.push('/login')
}

async function loadMyPosts() {
	const res = await listPosts({ page: 1, pageSize: 50, sortBy: 'new' })
	if (profile.value) {
		myPosts.items = res.items.filter((p) => p.authorId === profile.value?.id)
		myPosts.total = myPosts.items.length
	}
}

function formatTime(text?: string) {
	if (!text) return '未知'
	const date = new Date(text)
	return date.toLocaleDateString('zh-CN', {
		year: 'numeric',
		month: 'long',
		day: 'numeric',
		hour: '2-digit',
		minute: '2-digit'
	})
}
</script>

<style scoped>
.page {
	max-width: 900px;
	margin: 0 auto;
	padding: 20px;
	display: flex;
	flex-direction: column;
	gap: 20px;
}

.profile-header {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 16px;
	padding: 32px;
	color: white;
	box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.avatar-section {
	display: flex;
	align-items: center;
	gap: 20px;
}

.avatar {
	width: 80px;
	height: 80px;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 40px;
	backdrop-filter: blur(10px);
	border: 3px solid rgba(255, 255, 255, 0.3);
}

.user-basic {
	flex: 1;
}

.username {
	margin: 0 0 8px 0;
	font-size: 28px;
	font-weight: 700;
}

.email {
	margin: 0;
	opacity: 0.9;
	font-size: 16px;
}

.card {
	background: #fff;
	border: 1px solid #e5e7eb;
	border-radius: 16px;
	padding: 24px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
	padding-bottom: 16px;
	border-bottom: 2px solid #f3f4f6;
}

.card-header h3 {
	margin: 0;
	font-size: 20px;
	color: #111827;
	display: flex;
	align-items: center;
	gap: 8px;
}

.btn-create {
	padding: 8px 16px;
	background: #3b82f6;
	color: white;
	border: none;
	border-radius: 8px;
	cursor: pointer;
	font-size: 14px;
	font-weight: 500;
	transition: all 0.2s;
}

.btn-create:hover {
	background: #2563eb;
	transform: translateY(-1px);
	box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.info-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
	gap: 20px;
}

.info-item {
	display: flex;
	flex-direction: column;
	gap: 8px;
	padding: 16px;
	background: #f9fafb;
	border-radius: 12px;
	border: 1px solid #e5e7eb;
}

.info-label {
	font-size: 13px;
	color: #6b7280;
	font-weight: 500;
}

.info-value {
	font-size: 15px;
	color: #111827;
	font-weight: 600;
}

.loading {
	text-align: center;
	padding: 60px 20px;
	color: #9ca3af;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12px;
	font-size: 16px;
}

.spinner {
	width: 24px;
	height: 24px;
	border: 3px solid #e5e7eb;
	border-top-color: #3b82f6;
	border-radius: 50%;
	animation: spin 0.8s linear infinite;
}

@keyframes spin {
	to {
		transform: rotate(360deg);
	}
}

.post-list {
	list-style: none;
	padding: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.post-list li {
	padding: 16px;
	border: 1px solid #e5e7eb;
	border-radius: 12px;
	cursor: pointer;
	transition: all 0.2s;
	background: #fff;
}

.post-list li:hover {
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
	transform: translateY(-2px);
	border-color: #d1d5db;
}

.post-content {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.post-title {
	font-weight: 600;
	color: #111827;
	font-size: 15px;
}

.post-meta {
	display: flex;
	gap: 12px;
	align-items: center;
	flex-wrap: wrap;
}

.board-tag {
	padding: 2px 10px;
	background: #eef2ff;
	color: #4338ca;
	border-radius: 999px;
	font-size: 12px;
	font-weight: 500;
}

.post-time {
	color: #9ca3af;
	font-size: 13px;
}

.hot-score {
	color: #ea580c;
	font-size: 13px;
	font-weight: 600;
}

.empty-state {
	text-align: center;
	padding: 60px 20px;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 16px;
}

.empty-icon {
	font-size: 64px;
	opacity: 0.5;
}

.empty-state p {
	color: #6b7280;
	font-size: 16px;
	margin: 0;
}

.btn-create-primary {
	padding: 10px 24px;
	background: linear-gradient(135deg, #3b82f6, #2563eb);
	color: white;
	border: none;
	border-radius: 10px;
	cursor: pointer;
	font-size: 15px;
	font-weight: 500;
	transition: all 0.2s;
	box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-create-primary:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}
</style>
