<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>敏感词库</span>
        <div>
          <el-button type="primary" @click="handleAdd">新增敏感词</el-button>
        </div>
      </div>
    </template>

    <el-form :inline="true" :model="query" class="mb16">
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="搜索敏感词" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="words" border style="width: 100%" :loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="word" label="敏感词" min-width="200" />
      <el-table-column prop="level" label="级别" width="120">
        <template #default="{ row }">
          <el-tag :type="row.level === '高' ? 'danger' : row.level === '中' ? 'warning' : ''">
            {{ row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="edit(row)">编辑</el-button>
          <el-button type="danger" size="small" text @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="敏感词" prop="word">
          <el-input v-model="formData.word" placeholder="请输入敏感词" clearable />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select v-model="formData.level" placeholder="请选择级别" style="width: 100%">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createSensitiveWord,
  deleteSensitiveWord,
  listSensitiveWords,
  updateSensitiveWord,
  type SensitiveWord,
} from '@/api/admin/sensitive-words'

const query = reactive({
  keyword: '',
  page: 1,
  pageSize: 20,
})

const words = ref<SensitiveWord[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增敏感词')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

const formData = reactive({
  word: '',
  level: '低' as '高' | '中' | '低',
})

const rules: FormRules = {
  word: [{ required: true, message: '请输入敏感词', trigger: 'blur' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }],
}

async function fetchWords() {
  try {
    loading.value = true
    const res = await listSensitiveWords({
      page: query.page,
      pageSize: query.pageSize,
      keyword: query.keyword || '',
    })
    words.value = res.items
    total.value = res.total
  } catch (e) {
    ElMessage.error((e as Error).message || '加载敏感词失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.page = 1
  void fetchWords()
}

onMounted(() => {
  void fetchWords()
})

// 打开新增对话框
function handleAdd() {
  dialogTitle.value = '新增敏感词'
  editingId.value = null
  formData.word = ''
  formData.level = '低'
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    const payload = {
      word: formData.word.trim(),
      level: formData.level,
    }
    if (editingId.value !== null) {
      // 编辑
      await updateSensitiveWord(editingId.value, payload)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await createSensitiveWord(payload)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    void fetchWords()
  } catch (e) {
    if (e !== false) {
      ElMessage.error((e as Error).message || '操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 打开编辑对话框
function edit(row: SensitiveWord) {
  dialogTitle.value = '编辑敏感词'
  editingId.value = row.id
  formData.word = row.word
  formData.level = row.level
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

async function remove(row: SensitiveWord) {
  try {
    await ElMessageBox.confirm(`确认要删除敏感词「${row.word}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteSensitiveWord(row.id)
    ElMessage.success('删除成功')
    void fetchWords()
  } catch (e) {
    if ((e as Error).name === 'ElMessageBoxConfirm') return
    ElMessage.error((e as Error).message || '删除失败')
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
</style>
