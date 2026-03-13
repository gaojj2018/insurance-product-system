<template>
  <div class="customer-container">
    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-item">
          <label>客户姓名</label>
          <el-input v-model="queryForm.keyword" placeholder="请输入客户姓名/手机号" clearable />
        </div>
        <div class="search-item">
          <label>客户类型</label>
          <el-select v-model="queryForm.customerType" placeholder="请选择" clearable>
            <el-option label="个人" value="PERSONAL" />
            <el-option label="企业" value="CORPORATE" />
          </el-select>
        </div>
        <div class="search-item">
          <label>状态</label>
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="正常" value="ACTIVE" />
            <el-option label="冻结" value="FROZEN" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增客户</el-button>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading">
        <template #empty>
          <el-empty description="暂无客户数据" />
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="customerType" label="客户类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.customerType === 'PERSONAL'">个人</el-tag>
            <el-tag v-else-if="row.customerType === 'CORPORATE'" type="success">企业</el-tag>
            <el-tag v-else>{{ row.customerType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="idType" label="证件类型" width="100">
          <template #default="{ row }">
            <span v-if="row.idType === 'ID_CARD'">身份证</span>
            <span v-else-if="row.idType === 'PASSPORT'">护照</span>
            <span v-else-if="row.idType === 'BUSINESS_LICENSE'">营业执照</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="idNo" label="证件号码" width="180" />
        <el-table-column prop="mobile" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 'FROZEN'" type="warning">冻结</el-tag>
            <el-tag v-else type="info">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="handleView(row)">详情</el-button>
            <el-button 
              link 
              :type="row.status === 'ACTIVE' ? 'warning' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '冻结' : '解冻' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" top="5vh" destroy-on-close>
      <el-form :model="form" :rules="form.readOnly ? {} : rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="form.customerName" placeholder="请输入客户姓名" :disabled="form.readOnly" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customerType">
              <el-select v-model="form.customerType" placeholder="请选择" style="width: 100%" :disabled="form.readOnly">
                <el-option label="个人" value="PERSONAL" />
                <el-option label="企业" value="CORPORATE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="证件类型" prop="idType">
              <el-select v-model="form.idType" placeholder="请选择" style="width: 100%" :disabled="form.readOnly">
                <el-option label="身份证" value="ID_CARD" />
                <el-option label="护照" value="PASSPORT" />
                <el-option label="营业执照" value="BUSINESS_LICENSE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证件号码" prop="idNo">
              <el-input v-model="form.idNo" placeholder="请输入证件号码" :disabled="form.readOnly" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="mobile">
              <el-input v-model="form.mobile" placeholder="请输入联系电话" :disabled="form.readOnly" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" :disabled="form.readOnly" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" :disabled="form.readOnly" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ form.readOnly ? '关闭' : '取消' }}</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting" v-if="!form.readOnly">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request, { deleteCustomer } from '@/api'

// 查询表单
const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  customerType: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增客户')
const formRef = ref()
const submitting = ref(false)
const form = ref({
  id: null,
  customerName: '',
  customerType: 'PERSONAL',
  idType: 'ID_CARD',
  idNo: '',
  mobile: '',
  email: '',
  address: '',
  status: 'ACTIVE'
})

// 表单校验规则
const rules = {
  customerName: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  customerType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  idType: [{ required: true, message: '请选择证件类型', trigger: 'change' }],
  idNo: [{ required: true, message: '请输入证件号码', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryForm.value.pageNum,
      pageSize: queryForm.value.pageSize,
      keyword: queryForm.value.keyword
    }
    const res = await request.get('/customer/page', { params })
    if (res.data.code === 200) {
      tableData.value = res.data.data?.records || []
      total.value = res.data.data?.total || 0
    } else {
      ElMessage.error(res.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载失败', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  queryForm.value.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryForm.value = {
    pageNum: 1,
    pageSize: 10,
    keyword: '',
    customerType: '',
    status: ''
  }
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增客户'
  form.value = {
    id: null,
    customerName: '',
    customerType: 'PERSONAL',
    idType: 'ID_CARD',
    idNo: '',
    mobile: '',
    email: '',
    address: '',
    status: 'ACTIVE',
    readOnly: false
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑客户'
  form.value = { ...row, readOnly: false }
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  dialogTitle.value = '客户详情'
  form.value = { ...row, readOnly: true }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        let res
        if (!form.value.id) {
          res = await request.post('/customer', form.value)
        } else {
          res = await request.put(`/customer/${form.value.id}`, form.value)
        }
        
        if (res.data.code === 200) {
          ElMessage.success(form.value.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.data.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败', error)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 切换状态（冻结/解冻）
const handleToggleStatus = async (row) => {
  const action = row.status === 'ACTIVE' ? '冻结' : '解冻'
  try {
    await ElMessageBox.confirm(`确定要${action}该客户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const url = row.status === 'ACTIVE' ? `/customer/freeze/${row.id}` : `/customer/unfreeze/${row.id}`
    const res = await request.post(url)
    
    if (res.data.code === 200) {
      ElMessage.success(`${action}成功`)
      loadData()
    } else {
      ElMessage.error(res.data.message || `${action}失败`)
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 删除客户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该客户吗？', '提示', { type: 'warning' })
    const res = await deleteCustomer(row.id)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error?.response?.data?.message || error?.message || '删除失败'
      ElMessage.error(errorMsg)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.customer-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 15px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
