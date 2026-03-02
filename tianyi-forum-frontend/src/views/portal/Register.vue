<template>
	<div class="auth-card">
		<p class="eyebrow">新朋友</p>
		<h2>注册</h2>
		<form @submit.prevent="submit">
			<label>用户名<input v-model="form.username" autocomplete="username" /></label>
			<label>邮箱<input v-model="form.email" type="email" autocomplete="email" /></label>
			<label>密码<input v-model="form.password" type="password" autocomplete="new-password" /></label>
			<button class="primary" type="submit">注册并登录</button>
			<p class="error" v-if="error">{{ error }}</p>
		</form>
		<p class="hint">已有账号？<RouterLink to="/login">去登录</RouterLink>
		</p>
	</div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/portal/auth'

const form = reactive({ username: '', email: '', password: '' })
const error = ref('')
const router = useRouter()

async function submit() {
	error.value = ''
	if (!form.username || !form.email || !form.password) {
		error.value = '请填写完整信息'
		return
	}
	try {
		const res = await register(form)
		localStorage.setItem('user_token', res.token)
		router.push('/')
	} catch (e: any) {
		error.value = e?.message || '注册失败'
	}
}
</script>

<style scoped>
.auth-card {
	max-width: 400px;
	margin: 80px auto;
	padding: 22px;
	border: 1px solid #e5e7eb;
	border-radius: 16px;
	background: #fff;
	box-shadow: 0 16px 32px rgba(0, 0, 0, 0.06);
}

.eyebrow {
	margin: 0 0 6px;
	color: #6b7280;
	font-size: 12px;
}

.auth-card h2 {
	margin: 0 0 12px;
}

form {
	display: flex;
	flex-direction: column;
	gap: 14px;
}

label {
	display: flex;
	flex-direction: column;
	gap: 6px;
	font-size: 14px;
}

input {
	padding: 10px;
	border: 1px solid #d1d5db;
	border-radius: 10px;
}

.primary {
	background: #2563eb;
	color: #fff;
	border: none;
	padding: 10px;
	border-radius: 10px;
	cursor: pointer;
}

.error {
	color: #dc2626;
}

.hint {
	margin-top: 12px;
	color: #6b7280;
}
</style>
