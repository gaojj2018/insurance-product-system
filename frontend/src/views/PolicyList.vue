<template>
  <div class="page-container">
    <div class="page-header">
      <h2>保单管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-item">
          <label>保单号</label>
          <el-input v-model="queryForm.policyNo" placeholder="请输入保单号" clearable />
        </div>
        <div class="search-item">
          <label>产品名称</label>
          <el-input v-model="queryForm.productName" placeholder="请输入产品名称" clearable />
        </div>
        <div class="search-item">
          <label>被保人ID</label>
          <el-input v-model="queryForm.insuredId" placeholder="请输入被保人ID" clearable />
        </div>
        <div class="search-item">
          <label>保单状态</label>
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="生效" value="EFFECTIVE" />
            <el-option label="失效" value="LAPSED" />
            <el-option label="终止" value="TERMINATED" />
            <el-option label="已退保" value="SURRENDERED" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>
    
    <!-- 表格区域 -->
    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading">
        <template #empty>
          <el-empty description="暂无保单数据" />
        </template>
        <el-table-column prop="policyNo" label="保单号" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="insuredId" label="被保人ID" width="100" />
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
        <el-table-column prop="status" label="保单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        <el-table-column prop="expirationDate" label="到期日期" width="120" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="warning" size="small" @click="handleChange(row)">变更</el-button>
            <el-button link type="danger" size="small" @click="handleSurrender(row)" v-if="row.status === 'EFFECTIVE'">退保</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="保单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="保单号">{{ currentDetail.policyNo }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentDetail.productName }}</el-descriptions-item>
        <el-descriptions-item label="被保人ID">{{ currentDetail.insuredId }}</el-descriptions-item>
        <el-descriptions-item label="保额">¥{{ currentDetail.coverage?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="保费">¥{{ currentDetail.premium?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.status)">{{ getStatusText(currentDetail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="生效日期">{{ currentDetail.effectiveDate }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ currentDetail.expirationDate }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 退保弹窗 -->
    <el-dialog v-model="surrenderVisible" title="保单退保" width="500px">
      <el-form :model="surrenderForm" label-width="100px">
        <el-form-item label="保单号">
          <el-input v-model="surrenderForm.policyNo" disabled />
        </el-form-item>
        <el-form-item label="退保原因">
          <el-input v-model="surrenderForm.reason" type="textarea" :rows="3" placeholder="请输入退保原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="surrenderVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmSurrender">确认退保</el-button>
      </template>
    </el-dialog>

    <!-- 保单变更弹窗 -->
    <el-dialog v-model="changeVisible" title="保单变更" width="500px">
      <el-form :model="changeForm" label-width="100px">
        <el-form-item label="保单号">
          <el-input v-model="changeForm.policyNo" disabled />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="changeForm.productName" disabled />
        </el-form-item>
        <el-form-item label="新保额">
          <el-input-number v-model="changeForm.coverage" :min="0" :step="10000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="新保费">
          <el-input-number v-model="changeForm.premium" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="变更原因">
          <el-input v-model="changeForm.changeReason" type="textarea" :rows="3" placeholder="请输入变更原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeVisible = false">取消</el-button>
        <el-button type="warning" @click="confirmChange">确认变更</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  policyNo: '',
  productName: '',
  insuredId: '',
  status: ''
})
const detailVisible = ref(false)
const currentDetail = ref(null)
const surrenderVisible = ref(false)
const surrenderForm = ref({
  policyId: null,
  policyNo: '',
  reason: ''
})
const changeVisible = ref(false)
const changeForm = ref({
  policyId: null,
  policyNo: '',
  productName: '',
  coverage: 0,
  premium: 0,
  changeReason: ''
})

const getStatusType = (status) => {
  const map = {
    'EFFECTIVE': 'success',
    'ACTIVE': 'success',
    'LAPSED': 'warning',
    'TERMINATED': 'danger',
    'SURRENDERED': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'EFFECTIVE': '生效',
    'ACTIVE': '有效',
    'LAPSED': '失效',
    'TERMINATED': '终止',
    'SURRENDERED': '已退保'
  }
  return map[status] || status
}

const loadData = async () => {
  try {
    const response = await request.post('/policy/page', queryForm.value)
    if (response.data.code === 200) {
      tableData.value = response.data.data.records || []
      total.value = response.data.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  queryForm.value.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryForm.value = {
    pageNum: 1,
    pageSize: 10,
    policyNo: '',
    productName: '',
    insuredId: '',
    status: ''
  }
  loadData()
}

const handleView = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleChange = (row) => {
  changeForm.value = {
    policyId: row.policyId,
    policyNo: row.policyNo,
    productName: row.productName,
    coverage: row.coverage,
    premium: row.premium,
    changeReason: ''
  }
  changeVisible.value = true
}

const confirmChange = async () => {
  if (!changeForm.value.changeReason.trim()) {
    ElMessage.warning('请输入变更原因')
    return
  }
  try {
    await request.put(`/policy/${changeForm.value.policyId}/change`, null, {
      params: {
        coverage: changeForm.value.coverage,
        premium: changeForm.value.premium,
        changeReason: changeForm.value.changeReason
      }
    })
    ElMessage.success('保单变更成功')
    changeVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('变更失败: ' + (error.response?.data?.message || ''))
  }
}

const handleSurrender = (row) => {
  ElMessageBox.confirm('确定要对该保单进行退保操作吗？', '退保确认', {
    confirmButtonText: '确定退保',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    surrenderForm.value = {
      policyId: row.policyId,
      policyNo: row.policyNo,
      reason: ''
    }
    surrenderVisible.value = true
  }).catch(() => {})
}

const confirmSurrender = async () => {
  try {
    await request.put(`/policy/${surrenderForm.value.policyId}/surrender`, null, {
      params: { reason: surrenderForm.value.reason }
    })
    ElMessage.success('退保成功')
    surrenderVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('退保失败: ' + (error.response?.data?.message || ''))
  }
}

const handleSizeChange = (val) => {
  queryForm.value.pageSize = val
  loadData()
}

const handleCurrentChange = (val) => {
  queryForm.value.pageNum = val
  loadData()
}

onMounted(() => {
  loadData()
})
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

.search-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
