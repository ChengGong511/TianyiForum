<template>
	<div class="auth-card">
		<p class="eyebrow">欢迎回来</p>
		<h2>登录</h2>
		<form @submit.prevent="submit">
			<label>用户名<input v-model="form.username" autocomplete="username" /></label>
			<label>密码<input v-model="form.password" type="password" autocomplete="current-password" /></label>
			<button class="primary" type="submit">登录</button>
			<p class="error" v-if="error">{{ error }}</p>
		</form>
		<p class="hint">没有账号？<RouterLink to="/register">去注册</RouterLink>
		</p>
		<p class="hint admin-link">管理员？<RouterLink to="/admin/login">管理员登录</RouterLink>
		</p>
	</div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '@/api/portal/auth'

const form = reactive({ username: '', password: '' })
const error = ref('')
const router = useRouter()
const route = useRoute()

async function submit() {
	error.value = ''
	if (!form.username || !form.password) {
		error.value = '请输入用户名和密码'
		return
	}
	try {
		const res = await login(form)
		console.log('[Login] Login successful, token received')

		// 清除管理员token，确保不混用
		localStorage.removeItem('admin_token')
		localStorage.setItem('user_token', res.token)

		// 登录成功后获取用户信息并缓存
		try {
			const profile = await import('@/api/portal/profile').then(m => m.getProfile())
			localStorage.setItem('user_info', JSON.stringify(profile))
			console.log('[Login] User profile cached:', profile.username)
		} catch (profileErr) {
			console.error('[Login] Failed to fetch profile:', profileErr)
		}

		// 触发自定义事件通知 PortalLayout 刷新用户信息
		window.dispatchEvent(new CustomEvent('user-login'))

		// 跳转到目标页面
		const redirect = (route.query.redirect as string) || '/'
		console.log('[Login] Redirecting to:', redirect)
		await router.replace(redirect)
	} catch (e: unknown) {
		const errorMessage = e instanceof Error ? e.message : '登录失败'
		error.value = errorMessage
	}
}
</script>

<script lang="ts">
export default {
	name: 'UserLogin'
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

.admin-link {
	margin-top: 8px;
	padding-top: 8px;
	border-top: 1px solid #e5e7eb;
}
</style>
