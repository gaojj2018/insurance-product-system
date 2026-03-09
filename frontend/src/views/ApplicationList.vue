<template>
  <div class="page-container">
    <div class="page-header">
      <h2>投保申请</h2>
      <el-button type="primary" @click="handleCreate">新建投保</el-button>
    </div>
    
    <el-card>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="applicationNo" label="投保单号" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column label="投保人" width="100">
          <template #default="{ row }">
            {{ getCustomerName(row.applicantId) }}
          </template>
        </el-table-column>
        <el-table-column label="被保人" width="100">
          <template #default="{ row }">
            {{ getCustomerName(row.insuredId) }}
          </template>
        </el-table-column>
        <el-table-column prop="coverage" label="保额" width="120">
          <template #default="{ row }">
            ¥{{ row.coverage?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="保费" width="100">
          <template #default="{ row }">
            ¥{{ row.premium?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="申请时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'DRAFT'" link type="success" size="small" @click="handleSubmit(row)">提交</el-button>
            <el-button v-if="row.status === 'DRAFT'" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="投保单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="投保单号">{{ currentDetail.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentDetail.productName }}</el-descriptions-item>
        <el-descriptions-item label="投保人">{{ getCustomerName(currentDetail.applicantId) }}</el-descriptions-item>
        <el-descriptions-item label="被保人">{{ getCustomerName(currentDetail.insuredId) }}</el-descriptions-item>
        <el-descriptions-item label="保额">¥{{ currentDetail.coverage?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="保费">¥{{ currentDetail.premium?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="保障期间">{{ currentDetail.coveragePeriod }}</el-descriptions-item>
        <el-descriptions-item label="缴费期间">{{ currentDetail.paymentPeriod }}</el-descriptions-item>
        <el-descriptions-item label="缴费方式">{{ currentDetail.paymentMethod }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.status)">{{ getStatusText(currentDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentDetail.createdTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增投保弹窗 -->
    <el-dialog v-model="createVisible" title="新建投保" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="选择产品" prop="productId">
          <el-select v-model="createForm.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.productId" :label="p.productName" :value="p.productId" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="投保人" prop="applicantId">
              <el-select v-model="createForm.applicantId" placeholder="请选择投保人" style="width: 100%">
                <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="被保人" prop="insuredId">
              <el-select v-model="createForm.insuredId" placeholder="请选择被保人" style="width: 100%">
                <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保额" prop="coverage">
              <el-input-number v-model="createForm.coverage" :min="0" :step="10000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费方式" prop="paymentMethod">
              <el-select v-model="createForm.paymentMethod" placeholder="请选择" style="width: 100%">
                <el-option label="年缴" value="年缴" />
                <el-option label="季缴" value="季缴" />
                <el-option label="月缴" value="月缴" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="default" @click="handleSaveDraft" :loading="submitting">保存草稿</el-button>
        <el-button type="primary" @click="handleSubmitCreate" :loading="submitting">提交核保</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const customerMap = ref({})
const detailVisible = ref(false)
const currentDetail = ref(null)
const createVisible = ref(false)
const createFormRef = ref(null)
const submitting = ref(false)
const products = ref([])
const customers = ref([])
const createForm = ref({
  productId: null,
  applicantId: null,
  insuredId: null,
  coverage: 100000,
  paymentMethod: '年缴',
  remark: ''
})
const createRules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  applicantId: [{ required: true, message: '请选择投保人', trigger: 'change' }],
  insuredId: [{ required: true, message: '请选择被保人', trigger: 'change' }],
  coverage: [{ required: true, message: '请输入保额', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择缴费方式', trigger: 'change' }]
}

const getCustomerName = (id) => {
  if (!id) return '-'
  return customerMap.value[id] || `客户${id}`
}

const loadCustomers = async () => {
  try {
    const res = await request.get('/customer/page', { params: { pageNum: 1, pageSize: 1000 } })
    if (res.data.code === 200 && res.data.data.records) {
      customers.value = res.data.data.records || []
      res.data.data.records.forEach(c => {
        customerMap.value[c.id] = c.customerName
      })
    }
  } catch (e) {
    console.error('加载客户失败', e)
  }
}

const getStatusType = (status) => {
  const map = {
    'DRAFT': 'info',
    'SUBMITTED': 'warning',
    'UNDERWRITING': 'primary',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'POLICY_ISSUED': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'DRAFT': '草稿',
    'SUBMITTED': '已提交',
    'UNDERWRITING': '核保中',
    'APPROVED': '核保通过',
    'REJECTED': '核保拒绝',
    'POLICY_ISSUED': '已出单'
  }
  return map[status] || status
}

const loadData = async () => {
  try {
    const response = await request.post('/application/page', {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records || []
      total.value = response.data.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const loadProducts = async () => {
  try {
    const res = await request.post('/product/page', { pageNum: 1, pageSize: 100 })
    if (res.data.code === 200) {
      products.value = res.data.data.records || []
    }
  } catch (e) {
    console.error('加载产品失败', e)
  }
}

onMounted(async () => {
  await loadCustomers()
  await loadProducts()
  await loadData()
})

const handleCreate = async () => {
  if (customers.value.length === 0) {
    await loadCustomers()
  }
  createForm.value = {
    productId: null,
    applicantId: null,
    insuredId: null,
    coverage: 100000,
    paymentMethod: '年缴',
    remark: ''
  }
  createVisible.value = true
}

// 保存草稿
const handleSaveDraft = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await request.post('/application', createForm.value)
        if (res.data.code === 200) {
          ElMessage.success('草稿保存成功')
          createVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.data.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 提交核保
const handleSubmitCreate = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await request.post('/application/submit', createForm.value)
        if (res.data.code === 200) {
          ElMessage.success('投保申请已提交，正在核保中')
          createVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.data.message || '提交失败')
        }
      } catch (error) {
        ElMessage.error('提交失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 提交草稿投保单
const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该投保申请吗？提交后将进入核保流程。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.put(`/application/${row.applicationId}/submit`)
    if (res.data.code === 200) {
      ElMessage.success('提交成功，已进入核保流程')
      loadData()
    } else {
      ElMessage.error(res.data.message || '提交失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('提交失败')
    }
  }
}

// 删除投保单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该投保申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/application/${row.applicationId}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleView = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
