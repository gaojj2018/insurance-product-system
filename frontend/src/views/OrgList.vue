<template>
  <div class="org-container">
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-item">
          <label>机构名称</label>
          <el-input v-model="queryForm.keyword" placeholder="请输入机构名称" clearable />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增机构</el-button>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="tableData" row-key="id" :tree-props="{children: 'children'}" v-loading="loading" border stripe>
        <el-table-column prop="orgCode" label="机构代码" width="120" />
        <el-table-column prop="orgName" label="机构名称" min-width="150" />
        <el-table-column prop="level" label="层级" width="80" />
        <el-table-column prop="leader" label="负责人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">启用</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAddChild(row)">新增下级</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级机构" v-if="form.parentId">
          <el-input :value="parentOrgName" disabled />
        </el-form-item>
        <el-form-item label="机构代码" prop="orgCode">
          <el-input v-model="form.orgCode" placeholder="自动生成" disabled />
        </el-form-item>
        <el-form-item label="机构名称" prop="orgName">
          <el-input v-model="form.orgName" placeholder="请输入机构名称" />
        </el-form-item>
        <el-form-item label="机构层级" prop="level">
          <el-select v-model="form.level" placeholder="请选择">
            <el-option label="总公司" value="1" />
            <el-option label="分公司" value="2" />
            <el-option label="支公司" value="3" />
            <el-option label="营业部" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrgTree, getOrgById, createOrg, updateOrg, deleteOrg } from '@/api'

const queryForm = ref({ keyword: '' })
const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增机构')
const formRef = ref()
const parentOrgName = ref('')
const form = ref({
  id: null,
  orgCode: '',
  orgName: '',
  parentId: 0,
  level: '1',
  leader: '',
  phone: '',
  address: '',
  sortOrder: 0,
  status: 'ACTIVE'
})

const rules = {
  orgName: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择机构层级', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrgTree()
    tableData.value = res.data.data || []
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = loadData
const handleReset = () => {
  queryForm.value.keyword = ''
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增机构'
  form.value = {
    id: null,
    orgCode: '',
    orgName: '',
    parentId: 0,
    level: '1',
    leader: '',
    phone: '',
    address: '',
    sortOrder: 0,
    status: 'ACTIVE'
  }
  parentOrgName.value = ''
  dialogVisible.value = true
}

const handleAddChild = async (row) => {
  parentOrgName.value = row.orgName
  dialogTitle.value = '新增下级机构'
  form.value = {
    id: null,
    orgCode: '',
    orgName: '',
    parentId: row.id,
    level: String(parseInt(row.level) + 1),
    leader: '',
    phone: '',
    address: '',
    sortOrder: 0,
    status: 'ACTIVE'
  }
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑机构'
  const res = await getOrgById(row.id)
  form.value = { ...res.data.data }
  parentOrgName.value = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updateOrg(form.value)
      ElMessage.success('更新成功')
    } else {
      await createOrg(form.value)
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
    await ElMessageBox.confirm('确定删除该机构吗？', '提示', { type: 'warning' })
    await deleteOrg(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，该机构下存在子机构')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.org-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
</style>
