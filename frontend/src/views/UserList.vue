<!--
 * 用户管理页面
 * 功能: 系统用户的增删改查、角色分配、启用/禁用操作
 * API: GET /user/page, POST /user, PUT /user/:id, DELETE /user/:id
 -->
<template>
  <div class="user-container">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>
    
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-item">
          <label>用户名</label>
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </div>
        <div class="search-item">
          <label>所属机构</label>
          <el-select v-model="queryForm.orgId" placeholder="请选择" clearable>
            <el-option v-for="org in orgList" :key="org.id" :label="org.orgName" :value="org.id" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="username" label="用户名" width="120" sortable />
        <el-table-column prop="realName" label="真实姓名" width="120" sortable />
        <el-table-column prop="mobile" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="orgName" label="所属机构" width="150" />
        <el-table-column prop="roleName" label="角色" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.roleName || row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">启用</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" size="small" @click="handleAssignRole(row)">分配角色</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="所属机构" prop="orgId">
          <el-select v-model="form.orgId" placeholder="请选择" style="width: 100%">
            <el-option v-for="org in orgList" :key="org.id" :label="org.orgName" :value="org.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择" style="width: 100%">
            <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户">
          <el-input :value="currentUser.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="selectedRoleId" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, assignRole, getOrgList, getRoleList } from '@/api'

const queryForm = ref({ username: '', orgId: null })
const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const orgList = ref([])
const roleList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const roleDialogVisible = ref(false)
const currentUser = ref({})
const selectedRoleId = ref(null)
const formRef = ref()
const form = ref({
  id: null,
  username: '',
  password: '',
  realName: '',
  mobile: '',
  email: '',
  orgId: null,
  role: '',
  status: 'ACTIVE'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadOrgAndRole = async () => {
  try {
    const [orgRes, roleRes] = await Promise.all([getOrgList(), getRoleList()])
    orgList.value = orgRes.data.data || []
    roleList.value = roleRes.data.data || []
  } catch (error) {
    console.error('加载机构角色失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryForm.value)
    tableData.value = res.data.data?.records || []
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { loadData() }
const handleReset = () => {
  queryForm.value = { username: '', orgId: null }
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  form.value = {
    id: null,
    username: '',
    password: '',
    realName: '',
    mobile: '',
    email: '',
    orgId: null,
    role: '',
    status: 'ACTIVE'
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  form.value = { ...row, password: '' }
  dialogVisible.value = true
}

const handleAssignRole = (row) => {
  currentUser.value = row
  const role = roleList.value.find(r => r.roleCode === row.role)
  selectedRoleId.value = role?.id || null
  roleDialogVisible.value = true
}

const handleRoleSubmit = async () => {
  try {
    await assignRole({ userId: currentUser.value.id, roleId: selectedRoleId.value })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('分配失败', error)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updateUser(form.value)
      ElMessage.success('更新成功')
    } else {
      await createUser(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

onMounted(() => {
  loadOrgAndRole()
  loadData()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>
