<template>
	<div class="page">
		<section class="hero">
			<div>
				<p class="eyebrow">🏠 帖子广场</p>
				<h1>发现好帖，参与讨论</h1>
				<p class="sub">浏览最新热门话题，选择感兴趣的板块参与讨论</p>
			</div>
			<button class="cta" @click="goToCreate">
				<span>✏️</span>
				<span>立即发帖</span>
			</button>
		</section>

		<section class="search-section">
			<div class="search-box">
				<input v-model="keyword" type="text" class="search-input" placeholder="🔍 搜索帖子标题、内容、作者..."
					@keyup.enter="handleSearch" />
				<select v-model="selectedBoard" class="board-select">
					<option :value="null">全部板块</option>
					<option v-for="b in boards" :key="b.id" :value="b.id">
						{{ b.name }}
					</option>
				</select>
				<button class="search-btn" @click="handleSearch">搜索</button>
			</div>
		</section>

		<section class="posts">
			<div class="posts-header">
				<div>
					<p class="eyebrow">📊 实时动态</p>
					<h3>所有帖子</h3>
				</div>
				<div class="actions">
					<div class="segmented" aria-label="排序">
						<button :class="{ active: sortBy === 'new' }" @click="setSort('new')">最新</button>
						<button :class="{ active: sortBy === 'hot' }" @click="setSort('hot')">热度</button>
					</div>
					<button class="primary ghost" @click="goToCreate">发布</button>
				</div>
			</div>
			<div v-if="loading" class="hint">加载中...</div>
			<div v-else-if="!posts.items.length" class="hint">暂无帖子</div>
			<ul class="post-list">
				<li v-for="p in posts.items" :key="p.id" @click="goDetail(p.id)">
					<div class="post-header">
						<div class="title">{{ p.title }}</div>
						<div class="badges">
							<span v-if="p.isTop" class="badge badge-top">置顶</span>
							<span v-if="p.isFeatured" class="badge badge-hot">精选</span>
						</div>
					</div>
					<div class="meta">
						<span class="pill">{{ p.boardName || '未知板块' }}</span>
						<span class="author">{{ p.author }}</span>
						<span class="time">{{ formatTime(p.createdAt) }}</span>
						<span v-if="p.hotScore" class="hot-score">🔥 {{ p.hotScore }}</span>
					</div>
				</li>
			</ul>
			<div class="pager" v-if="posts.total > pageSize">
				<button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
				<span>{{ page }} / {{ totalPages }}</span>
				<button :disabled="page === totalPages" @click="changePage(page + 1)">下一页</button>
			</div>
		</section>
	</div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { listBoards, type Board } from '@/api/portal/boards'
import { listPosts, type PostSummary } from '@/api/portal/posts'

const boards = ref<Board[]>([])
const selectedBoard = ref<number | null>(null)
const keyword = ref('')
const posts = reactive<{ items: PostSummary[]; total: number }>({ items: [], total: 0 })
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const sortBy = ref<'hot' | 'new'>('new')
const router = useRouter()

const totalPages = computed(() => Math.max(1, Math.ceil(posts.total / pageSize)))

onMounted(async () => {
	try {
		boards.value = await listBoards()
	} catch (e) {
		console.error('加载板块失败', e)
	}
	await fetchPosts()
})

watch([selectedBoard, sortBy, page], () => {
	fetchPosts()
})

async function fetchPosts() {
	loading.value = true
	try {
		const res = await listPosts({
			page: page.value,
			pageSize,
			sortBy: sortBy.value,
			boardId: selectedBoard.value || undefined,
			keyword: keyword.value.trim() || undefined
		})
		posts.items = res.items
		posts.total = res.total
	} catch (e) {
		console.error('加载帖子失败', e)
	} finally {
		loading.value = false
	}
}

function handleSearch() {
	page.value = 1
	fetchPosts()
}

function selectBoard(id: number | null) {
	selectedBoard.value = id
	page.value = 1
}

function goDetail(id: number) {
	router.push({ name: 'portal-post-detail', params: { id } })
}

function setSort(value: 'hot' | 'new') {
	sortBy.value = value
	page.value = 1
}

function changePage(nextPage: number) {
	page.value = nextPage
	window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToCreate() {
	const token = localStorage.getItem('user_token')
	if (!token) {
		router.push({ name: 'portal-login', query: { redirect: '/posts/create' } })
		return
	}
	router.push({ name: 'portal-create-post' })
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
</script>

<script lang="ts">
export default {
	name: 'HomePage'
}
</script>

<style scoped>
.page {
	max-width: 1000px;
	margin: 0 auto;
	display: flex;
	flex-direction: column;
	gap: 18px;
}

.hero {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
	background: linear-gradient(135deg, #e0e7ff, #f9fafb);
	padding: 18px 22px;
	border-radius: 14px;
	border: 1px solid #e5e7eb;
	box-shadow: 0 10px 24px rgba(59, 130, 246, 0.08);
}

.eyebrow {
	margin: 0 0 6px;
	color: #6b7280;
	font-size: 12px;
	letter-spacing: 0.2px;
}

.hero h1 {
	margin: 0 0 6px;
	font-size: 24px;
	color: #0f172a;
}

.hero .sub {
	margin: 0;
	color: #475569;
}

.cta {
	background: linear-gradient(135deg, #3b82f6, #2563eb);
	color: #fff;
	border: none;
	padding: 12px 20px;
	border-radius: 10px;
	cursor: pointer;
	box-shadow: 0 10px 20px rgba(37, 99, 235, 0.3);
	transition: all 0.2s;
	font-weight: 600;
	font-size: 15px;
	display: flex;
	align-items: center;
	gap: 6px;
}

.cta:hover {
	transform: translateY(-2px);
	box-shadow: 0 12px 28px rgba(37, 99, 235, 0.4);
}

.search-section {
	background: #fff;
	padding: 20px;
	border-radius: 14px;
	border: 1px solid #e5e7eb;
	box-shadow: 0 8px 18px rgba(0, 0, 0, 0.03);
}

.search-box {
	display: flex;
	gap: 12px;
	align-items: center;
}

.search-input {
	flex: 1;
	padding: 12px 16px;
	border: 1px solid #d1d5db;
	border-radius: 10px;
	font-size: 15px;
	transition: all 0.2s;
}

.search-input:focus {
	outline: none;
	border-color: #2563eb;
	box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.board-select {
	padding: 12px 16px;
	border: 1px solid #d1d5db;
	border-radius: 10px;
	font-size: 15px;
	background: #fff;
	cursor: pointer;
	transition: all 0.2s;
	min-width: 150px;
}

.board-select:focus {
	outline: none;
	border-color: #2563eb;
	box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.search-btn {
	padding: 12px 24px;
	background: linear-gradient(135deg, #2563eb, #3b82f6);
	color: #fff;
	border: none;
	border-radius: 10px;
	font-size: 15px;
	font-weight: 600;
	cursor: pointer;
	transition: all 0.2s;
	box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.search-btn:hover {
	transform: translateY(-1px);
	box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
}

.boards,
.posts {
	background: #fff;
	padding: 14px;
	border-radius: 14px;
	border: 1px solid #e5e7eb;
	box-shadow: 0 8px 18px rgba(0, 0, 0, 0.03);
}

.boards h3,
.posts h3 {
	margin: 0 0 8px;
}

.board-list {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.board-pill {
	border: 1px solid #e5e7eb;
	padding: 6px 12px;
	border-radius: 999px;
	background: #fff;
	cursor: pointer;
	transition: all 0.15s ease;
}

.board-pill:hover {
	border-color: #cbd5e1;
	background: #f8fafc;
}

.board-pill.active {
	background: #e4ecff;
	color: #1d4ed8;
	border-color: #c7d2fe;
	font-weight: 500;
}

.posts-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12px;
}

.actions {
	display: flex;
	gap: 10px;
	align-items: center;
}

.segmented {
	display: inline-flex;
	border: 1px solid #dbeafe;
	border-radius: 12px;
	overflow: hidden;
	background: #f8fbff;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.segmented button {
	border: none;
	background: transparent;
	padding: 8px 12px;
	color: #1e3a8a;
	cursor: pointer;
	transition: all 0.15s ease;
	font-size: 14px;
}

.segmented button:hover {
	background: #e0ecff;
}

.segmented button.active {
	background: linear-gradient(135deg, #2563eb, #3b82f6);
	color: #fff;
	box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
}

.primary {
	background: #2563eb;
	color: #fff;
	border: none;
	padding: 8px 12px;
	border-radius: 10px;
	cursor: pointer;
	font-size: 14px;
	transition: all 0.2s;
}

.primary.ghost {
	background: #0ea5e9;
	box-shadow: 0 8px 18px rgba(14, 165, 233, 0.24);
}

.primary:hover {
	background: #1d4ed8;
}

.primary:disabled {
	opacity: 0.6;
	cursor: not-allowed;
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
	padding: 14px;
	border: 1px solid #e5e7eb;
	border-radius: 12px;
	cursor: pointer;
	transition: box-shadow 0.18s ease, transform 0.12s ease;
	background: #fff;
}

.post-list li:hover {
	box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
	transform: translateY(-2px);
	border-color: #d1d5db;
}

.post-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 8px;
}

.title {
	font-weight: 600;
	color: #0f172a;
	font-size: 15px;
	flex: 1;
}

.badges {
	display: flex;
	gap: 6px;
	flex-shrink: 0;
}

.badge {
	padding: 2px 8px;
	border-radius: 4px;
	font-size: 11px;
	font-weight: 600;
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
	display: flex;
	gap: 12px;
	color: #6b7280;
	font-size: 13px;
	flex-wrap: wrap;
	align-items: center;
}

.pill {
	padding: 2px 8px;
	border-radius: 999px;
	background: #eef2ff;
	color: #4338ca;
	font-size: 12px;
}

.author {
	color: #374151;
	font-weight: 500;
}

.time {
	color: #9ca3af;
}

.hot-score {
	color: #ea580c;
	font-weight: 500;
}

.hint {
	color: #6b7280;
	padding: 20px 0;
	text-align: center;
}

.pager {
	display: flex;
	align-items: center;
	gap: 12px;
	justify-content: flex-end;
	margin-top: 12px;
}

.pager button {
	padding: 6px 12px;
	border: 1px solid #e5e7eb;
	border-radius: 10px;
	background: #fff;
	cursor: pointer;
	transition: all 0.2s;
	font-size: 14px;
}

.pager button:hover:not(:disabled) {
	background: #f9fafb;
	border-color: #d1d5db;
}

.pager button:disabled {
	opacity: 0.4;
	cursor: not-allowed;
}

.pager span {
	color: #6b7280;
	font-size: 14px;
}
</style>
