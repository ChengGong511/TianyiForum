<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>板块配置</span>
        <el-button type="primary" @click="openCreate">新增板块</el-button>
      </div>
    </template>
    <el-table :data="boards" border style="width: 100%" :loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="板块名称" min-width="180" />
      <el-table-column prop="moderator" label="版主" width="140" />
      <el-table-column prop="postCount" label="帖子数" width="120" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" text @click="edit(row)">编辑</el-button>
          <el-button size="small" text @click="assignModerator(row)">委派版主</el-button>
          <el-button type="danger" size="small" text @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="createVisible" :title="isEdit ? '编辑板块' : '新增板块'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入板块名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入板块描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="onCreateCancel">取消</el-button>
          <el-button type="primary" :loading="saving" @click="onCreateSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="assignVisible" title="委派版主" width="400px">
      <el-form label-width="80px">
        <el-form-item label="选择版主">
          <el-select v-model="selectedModeratorId" filterable placeholder="请选择版主（活跃用户）" style="width: 260px">
            <el-option v-for="u in moderatorCandidates" :key="u.id" :label="u.username" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="onAssignCancel">取消</el-button>
          <el-button type="primary" :loading="assignSaving" @click="onAssignSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  assignModerator as assignModeratorApi,
  createBoard,
  deleteBoard,
  listBoards,
  updateBoard,
  type Board,
} from '@/api/admin/boards'
import { listUsers, type User } from '@/api/admin/users'

const boards = ref<Board[]>([])
const loading = ref(false)

type BoardForm = {
  name: string
  description: string
  sort: number
}

const createVisible = ref(false)
const isEdit = ref(false)
const editingBoardId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = reactive<BoardForm>({
  name: '',
  description: '',
  sort: 1,
})
const saving = ref(false)

const rules: FormRules<BoardForm> = {
  name: [
    { required: true, message: '请输入板块名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' },
  ],
  sort: [{ required: true, message: '请输入排序值', trigger: 'change' }],
}

async function fetchBoards() {
  try {
    loading.value = true
    boards.value = await listBoards()
  } catch (e) {
    ElMessage.error((e as Error).message || '加载板块列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void fetchBoards()
})

function openCreate() {
  isEdit.value = false
  editingBoardId.value = null
  createVisible.value = true
}

function resetForm() {
  form.name = ''
  form.description = ''
  form.sort = 1
  formRef.value?.clearValidate()
}

function onCreateCancel() {
  createVisible.value = false
  resetForm()
}

async function onCreateSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  try {
    saving.value = true
    if (isEdit.value && editingBoardId.value != null) {
      await updateBoard(editingBoardId.value, {
        name: form.name,
        description: form.description || undefined,
        sort: form.sort,
      })
      ElMessage.success('修改成功')
    } else {
      await createBoard({
        name: form.name,
        description: form.description || undefined,
        sort: form.sort,
      })
      ElMessage.success('新增成功')
    }
    createVisible.value = false
    resetForm()
    void fetchBoards()
  } catch (e) {
    ElMessage.error((e as Error).message || (isEdit.value ? '修改失败' : '新增失败'))
  } finally {
    saving.value = false
  }
}

function edit(row: Board) {
  isEdit.value = true
  editingBoardId.value = row.id
  form.name = row.name
  form.description = row.description || ''
  form.sort = row.sort
  createVisible.value = true
}

const assignVisible = ref(false)
const currentBoardForAssign = ref<Board | null>(null)
const moderatorCandidates = ref<User[]>([])
const selectedModeratorId = ref<number | null>(null)
const assignSaving = ref(false)

async function loadModeratorCandidates() {
  try {
    const res = await listUsers({ page: 1, pageSize: 50, status: 'active' })
    moderatorCandidates.value = res.items.filter(
      (u) => u.role === 'moderator' || u.role === 'admin',
    )
  } catch (e) {
    ElMessage.error((e as Error).message || '加载版主候选人失败')
  }
}

async function assignModerator(row: Board) {
  currentBoardForAssign.value = row
  selectedModeratorId.value = row.moderatorId ?? null
  assignVisible.value = true
  await loadModeratorCandidates()
}

function onAssignCancel() {
  assignVisible.value = false
  currentBoardForAssign.value = null
  selectedModeratorId.value = null
}

async function onAssignSubmit() {
  if (!currentBoardForAssign.value || selectedModeratorId.value == null) {
    ElMessage.warning('请选择版主')
    return
  }

  try {
    assignSaving.value = true
    await assignModeratorApi(currentBoardForAssign.value.id, selectedModeratorId.value)
    ElMessage.success('委派成功')
    assignVisible.value = false
    currentBoardForAssign.value = null
    selectedModeratorId.value = null
    void fetchBoards()
  } catch (e) {
    ElMessage.error((e as Error).message || '委派失败')
  } finally {
    assignSaving.value = false
  }
}

async function remove(row: Board) {
  try {
    await ElMessageBox.confirm(`确认要删除板块「${row.name}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteBoard(row.id)
    ElMessage.success('删除成功')
    void fetchBoards()
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
