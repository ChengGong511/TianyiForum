<template>
	<div class="page">
		<div class="card">
			<div class="header">
				<button class="back-btn" @click="$router.back()">
					<span>←</span> 返回
				</button>
				<h2>发布新帖</h2>
			</div>

			<div class="form-section">
				<div class="field">
					<label class="field-label">标题</label>
					<input v-model="title" class="field-input" placeholder="请输入帖子标题..." maxlength="100" />
					<div class="field-hint">{{ title.length }}/100</div>
				</div>

				<div class="field">
					<label class="field-label">选择板块</label>
					<select v-model.number="boardId" class="field-select">
						<option :value="null" disabled>请选择板块</option>
						<option v-for="b in boards" :key="b.id" :value="b.id">
							{{ b.name }}
						</option>
					</select>
				</div>

				<div class="field">
					<label class="field-label">内容</label>
					<textarea v-model="content" class="field-textarea" placeholder="分享你的想法和观点..." rows="12"></textarea>
					<div class="field-hint">{{ content.length }} 字</div>
				</div>

				<div v-if="error" class="error-message">{{ error }}</div>

				<div class="actions">
					<button class="btn-cancel" @click="$router.back()">取消</button>
					<button class="btn-submit" @click="submit" :disabled="submitting">
						{{ submitting ? '提交中...' : '发布帖子' }}
					</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listBoards, type Board } from '@/api/portal/boards'
import { createPost } from '@/api/portal/posts'

const router = useRouter()
const boards = ref<Board[]>([])
const title = ref('')
const boardId = ref<number | null>(null)
const content = ref('')
const error = ref('')
const submitting = ref(false)

onMounted(async () => {
	try {
		boards.value = await listBoards()
	} catch {
		error.value = '加载板块失败'
	}
})

async function submit() {
	error.value = ''

	if (!title.value.trim()) {
		error.value = '请输入标题'
		return
	}

	if (!boardId.value) {
		error.value = '请选择板块'
		return
	}

	if (!content.value.trim()) {
		error.value = '请输入内容'
		return
	}

	submitting.value = true
	try {
		await createPost({
			title: title.value.trim(),
			content: content.value.trim(),
			boardId: boardId.value
		})
		router.push({ name: 'portal-home' })
	} catch (e: unknown) {
		const errorMessage = e instanceof Error ? e.message : '发布失败'
		error.value = errorMessage
	} finally {
		submitting.value = false
	}
}
</script>

<style scoped>
.page {
	max-width: 900px;
	margin: 0 auto;
	padding: 20px;
}

.card {
	background: #fff;
	border: 1px solid #e5e7eb;
	border-radius: 16px;
	padding: 28px;
	box-shadow: 0 10px 24px rgba(0, 0, 0, 0.04);
}

.header {
	display: flex;
	align-items: center;
	gap: 16px;
	margin-bottom: 24px;
	padding-bottom: 20px;
	border-bottom: 1px solid #e5e7eb;
}

.back-btn {
	display: flex;
	align-items: center;
	gap: 4px;
	padding: 8px 12px;
	border: 1px solid #d1d5db;
	border-radius: 8px;
	background: #fff;
	cursor: pointer;
	font-size: 14px;
	color: #374151;
	transition: all 0.2s;
}

.back-btn:hover {
	background: #f9fafb;
	border-color: #9ca3af;
}

.header h2 {
	margin: 0;
	font-size: 22px;
	color: #111827;
}

.form-section {
	display: flex;
	flex-direction: column;
	gap: 20px;
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

.field-input,
.field-select,
.field-textarea {
	padding: 12px;
	border: 1px solid #d1d5db;
	border-radius: 10px;
	font-size: 15px;
	font-family: inherit;
	transition: border-color 0.2s, box-shadow 0.2s;
}

.field-input:focus,
.field-select:focus,
.field-textarea:focus {
	outline: none;
	border-color: #2563eb;
	box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.field-textarea {
	resize: vertical;
	min-height: 200px;
	line-height: 1.6;
}

.field-hint {
	font-size: 12px;
	color: #9ca3af;
	text-align: right;
}

.error-message {
	padding: 12px;
	background: #fef2f2;
	border: 1px solid #fecaca;
	border-radius: 8px;
	color: #dc2626;
	font-size: 14px;
}

.actions {
	display: flex;
	gap: 12px;
	justify-content: flex-end;
	margin-top: 8px;
}

.btn-cancel,
.btn-submit {
	padding: 10px 24px;
	border-radius: 10px;
	font-size: 15px;
	font-weight: 500;
	cursor: pointer;
	transition: all 0.2s;
}

.btn-cancel {
	border: 1px solid #d1d5db;
	background: #fff;
	color: #374151;
}

.btn-cancel:hover {
	background: #f9fafb;
}

.btn-submit {
	border: none;
	background: #2563eb;
	color: #fff;
}

.btn-submit:hover:not(:disabled) {
	background: #1d4ed8;
}

.btn-submit:disabled {
	opacity: 0.6;
	cursor: not-allowed;
}
</style>
