<!--
 * 角色管理页面
 * 功能: 系统角色的增删改查、权限配置
 * API: GET /role/page, POST /role, PUT /role/:id, DELETE /role/:id
 -->
<template>
  <div class="role-container">
    <div class="page-header">
      <h2>角色管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-item">
          <label>角色代码</label>
          <el-input v-model="queryForm.roleCode" placeholder="请输入角色代码" clearable />
        </div>
        <div class="search-item">
          <label>角色名称</label>
          <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
        </div>
        <div class="search-item">
          <label>状态</label>
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="roleCode" label="角色代码" width="150" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleType" label="角色类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.roleType === 'SYSTEM'" type="danger">系统</el-tag>
            <el-tag v-else-if="row.roleType === 'BUSINESS'" type="warning">业务</el-tag>
            <el-tag v-else>普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
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
            <el-button link type="success" size="small" @click="handleAssignPermission(row)">权限</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色代码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="如: ADMIN, AGENT" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色类型" prop="roleType">
          <el-select v-model="form.roleType" placeholder="请选择">
            <el-option label="系统角色" value="SYSTEM" />
            <el-option label="业务角色" value="BUSINESS" />
            <el-option label="普通角色" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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

    <!-- 权限分配弹窗 -->
    <el-dialog v-model="permDialogVisible" title="权限分配" width="500px">
      <el-tree
        ref="permTree"
        :data="permissionTree"
        :props="{ label: 'permissionName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, getRolePage, createRole, updateRole, deleteRole, getPermissionTree, getRolePermissions, assignRolePermission } from '@/api'

const permDialogVisible = ref(false)
const permTree = ref()
const currentRoleId = ref(null)
const permissionTree = ref([])
const checkedPermissions = ref([])

const loadPermissions = async () => {
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data?.data || []
  } catch (e) {
    console.error('加载权限失败', e)
  }
}

const handleAssignPermission = async (row) => {
  currentRoleId.value = row.id
  await loadPermissions()
  try {
    const res = await getRolePermissions(row.id)
    checkedPermissions.value = res.data?.data || []
  } catch (e) {
    checkedPermissions.value = []
  }
  permDialogVisible.value = true
}

const handlePermSubmit = async () => {
  const checkedNodes = permTree.value.getCheckedKeys()
  const halfCheckedNodes = permTree.value.getHalfCheckedKeys()
  const allPermIds = [...checkedNodes, ...halfCheckedNodes]
  
  try {
    await assignRolePermission({ roleId: currentRoleId.value, permissionIds: allPermIds })
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
  } catch (e) {
    ElMessage.error('权限分配失败')
  }
}

const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const queryForm = ref({
  roleCode: '',
  roleName: '',
  status: ''
})
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref()
const form = ref({
  id: null,
  roleCode: '',
  roleName: '',
  roleType: 'USER',
  description: '',
  status: 'ACTIVE'
})

const rules = {
  roleCode: [{ required: true, message: '请输入角色代码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleType: [{ required: true, message: '请选择角色类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRolePage(queryForm.value)
    tableData.value = res.data.data?.records || res.data.data || []
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  queryForm.value = {
    roleCode: '',
    roleName: '',
    status: ''
  }
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  form.value = {
    id: null,
    roleCode: '',
    roleName: '',
    roleType: 'USER',
    description: '',
    status: 'ACTIVE'
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updateRole(form.value)
      ElMessage.success('更新成功')
    } else {
      await createRole(form.value)
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
    await ElMessageBox.confirm('确定删除该角色吗？', '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>
