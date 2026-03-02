<template>
	<div class="login-wrap">
		<el-card class="box-card">
			<template #header>
				<div class="header-title">
					<h2>天翼论坛 - 管理员登录</h2>
				</div>
			</template>
			<el-form :model="form" @keyup.enter="onSubmit">
				<el-form-item label="用户名">
					<el-input v-model="form.username" placeholder="请输入用户名" />
				</el-form-item>
				<el-form-item label="密码">
					<el-input v-model="form.password" type="password" placeholder="请输入密码" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" style="width: 100%" :loading="loading" @click="onSubmit">
						登录
					</el-button>
				</el-form-item>
				<el-form-item>
					<div class="user-login-link">
						<span>普通用户？</span>
						<el-link type="primary" @click="goToUserLogin">用户登录</el-link>
					</div>
				</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/admin/auth'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const router = useRouter()
const route = useRoute()

async function onSubmit() {
	if (!form.username || !form.password) {
		ElMessage.warning('请输入用户名和密码')
		return
	}
	loading.value = true
	try {
		const token = await login(form.username, form.password)
		localStorage.removeItem('user_token')
		localStorage.setItem('admin_token', token)
		ElMessage.success('登录成功')
		const redirect = (route.query.redirect as string) || '/admin'
		router.replace(redirect)
	} catch (e) {
		ElMessage.error('登录失败：' + (e as Error).message)
	} finally {
		loading.value = false
	}
}

function goToUserLogin() {
	router.push('/login')
}
</script>

<style scoped>
.login-wrap {
	height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.box-card {
	width: 400px;
}

.header-title {
	text-align: center;
}

.header-title h2 {
	margin: 0;
	color: #333;
}

.user-login-link {
	text-align: center;
	padding-top: 8px;
	border-top: 1px solid #e5e7eb;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	color: #6b7280;
	font-size: 14px;
}
</style>
