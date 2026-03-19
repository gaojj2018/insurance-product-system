<!--
 * 权限管理页面
 * 功能: 系统权限的树形结构管理，包括增删改查
 * API: GET /permission/tree, POST /permission, PUT /permission/:id, DELETE /permission/:id
 -->
<template>
  <div class="permission-container">
    <div class="page-header">
      <h2>权限管理</h2>
    </div>
    
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-actions">
          <el-button type="primary" @click="handleAdd">新增权限</el-button>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="tableData" row-key="id" :tree-props="{children: 'children'}" v-loading="loading" border stripe>
        <el-table-column prop="permissionCode" label="权限代码" width="180" />
        <el-table-column prop="permissionName" label="权限名称" min-width="150" />
        <el-table-column prop="permissionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.permissionType === 'MENU'" type="success">菜单</el-tag>
            <el-tag v-else-if="row.permissionType === 'BUTTON'" type="warning">按钮</el-tag>
            <el-tag v-else type="info">接口</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resourceUrl" label="资源路径" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAddChild(row)" v-if="row.permissionType === 'MENU'">新增子权限</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级权限" v-if="form.parentId">
          <el-input :value="parentPermissionName" disabled />
        </el-form-item>
        <el-form-item label="权限代码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="如: product:create" />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="form.permissionType" placeholder="请选择">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item label="资源路径" prop="resourceUrl">
          <el-input v-model="form.resourceUrl" placeholder="菜单路径或API地址" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api'

const tableData = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')
const formRef = ref()
const parentPermissionName = ref('')
const form = ref({
  id: null,
  permissionCode: '',
  permissionName: '',
  permissionType: 'BUTTON',
  parentId: 0,
  resourceUrl: '',
  sortOrder: 0
})

const rules = {
  permissionCode: [{ required: true, message: '请输入权限代码', trigger: 'blur' }],
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    tableData.value = res.data.data || []
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增权限'
  form.value = {
    id: null,
    permissionCode: '',
    permissionName: '',
    permissionType: 'BUTTON',
    parentId: 0,
    resourceUrl: '',
    sortOrder: 0
  }
  parentPermissionName.value = ''
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  dialogTitle.value = '新增子权限'
  form.value = {
    id: null,
    permissionCode: '',
    permissionName: '',
    permissionType: row.permissionType === 'MENU' ? 'BUTTON' : 'API',
    parentId: row.id,
    resourceUrl: '',
    sortOrder: 0
  }
  parentPermissionName.value = row.permissionName
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑权限'
  form.value = { ...row }
  parentPermissionName.value = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updatePermission(form.value)
      ElMessage.success('更新成功')
    } else {
      await createPermission(form.value)
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
    await ElMessageBox.confirm('确定删除该权限吗？', '提示', { type: 'warning' })
    await deletePermission(row.id)
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
.permission-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>
