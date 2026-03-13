<template>
  <div class="page-container">
    <div class="page-header">
      <h2>核保管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="投保单号">
          <el-input v-model="queryForm.applicationNo" placeholder="请输入投保单号" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="queryForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="投保人">
          <el-input v-model="queryForm.applicantName" placeholder="请输入投保人姓名" clearable />
        </el-form-item>
        <el-form-item label="核保状态">
          <el-select v-model="queryForm.result" placeholder="请选择" clearable>
            <el-option label="待核保" value="PENDING" />
            <el-option label="已通过" value="PASSED" />
            <el-option label="已拒保" value="REJECTED" />
            <el-option label="核保中" value="UNDERWRITING" />
            <el-option label="延期" value="DEFERRED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="applicationNo" label="投保单号" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="applicantName" label="投保人" width="100" />
        <el-table-column prop="coverageAmount" label="保额" width="120">
          <template #default="{ row }">
            ¥{{ row.coverageAmount || row.appliedAmount || 0 | toLocaleString }}
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="保费" width="100">
          <template #default="{ row }">
            ¥{{ row.premium || 0 | toLocaleString }}
          </template>
        </el-table-column>
        <el-table-column prop="result" label="核保状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.result || row.underwriteStatus)">
              {{ getStatusText(row.result || row.underwriteStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看详情</el-button>
            <el-button link type="success" size="small" @click="handleApprove(row)" v-if="(row.result || row.underwriteStatus) === 'PENDING'">通过</el-button>
            <el-button link type="danger" size="small" @click="openRejectDialog(row)" v-if="(row.result || row.underwriteStatus) === 'PENDING'">拒保</el-button>
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
    <el-dialog v-model="detailVisible" title="核保详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="投保单号">{{ currentDetail.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentDetail.productName }}</el-descriptions-item>
        <el-descriptions-item label="投保人">{{ currentDetail.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="被保人">{{ currentDetail.insuredName || currentDetail.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="保额">¥{{ currentDetail.coverageAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="保费">¥{{ currentDetail.premium?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="缴费方式">{{ currentDetail.paymentMethod }}</el-descriptions-item>
        <el-descriptions-item label="保障期间">{{ currentDetail.coveragePeriod }}</el-descriptions-item>
        <el-descriptions-item label="核保状态">
          <el-tag :type="getStatusType(currentDetail.result || currentDetail.underwriteStatus)">{{ getStatusText(currentDetail.result || currentDetail.underwriteStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="核保意见" v-if="currentDetail.opinion">{{ currentDetail.opinion }}</el-descriptions-item>
        <el-descriptions-item label="核保时间" v-if="currentDetail.underwritingTime">{{ currentDetail.underwritingTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="success" @click="handleApprove(currentDetail)" v-if="(currentDetail.result || currentDetail.underwriteStatus) === 'PENDING'">通过</el-button>
        <el-button type="danger" @click="openRejectDialog(currentDetail)" v-if="(currentDetail.result || currentDetail.underwriteStatus) === 'PENDING'">拒保</el-button>
      </template>
    </el-dialog>

    <!-- 拒保弹窗 -->
    <el-dialog v-model="rejectVisible" title="拒保确认" width="500px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒保理由" required>
          <el-input v-model="rejectForm.opinion" type="textarea" :rows="4" placeholder="请输入拒保理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒保</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const tableData = ref([])
const total = ref(0)
const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  applicationNo: '',
  productName: '',
  applicantName: '',
  result: ''
})
const detailVisible = ref(false)
const currentDetail = ref(null)
const rejectVisible = ref(false)
const rejectForm = ref({
  id: null,
  opinion: ''
})

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'UNDERWRITING': 'primary',
    'PASSED': 'success',
    'REJECTED': 'danger',
    'DEFERRED': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待核保',
    'UNDERWRITING': '核保中',
    'PASSED': '已通过',
    'REJECTED': '已拒保',
    'DEFERRED': '延期'
  }
  return map[status] || status
}

const loadData = async () => {
  try {
    const response = await request.post('/underwriting/page', queryForm.value)
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
    applicationNo: '',
    productName: '',
    applicantName: '',
    result: ''
  }
  loadData()
}

const handleView = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const openRejectDialog = (row) => {
  rejectForm.value = {
    id: row.underwritingId || row.id,
    opinion: ''
  }
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.opinion.trim()) {
    ElMessage.warning('请输入拒保理由')
    return
  }
  try {
    await request.put(`/underwriting/${rejectForm.value.id}/result?result=REJECTED&opinion=${encodeURIComponent(rejectForm.value.opinion)}`)
    ElMessage.success('已拒保')
    rejectVisible.value = false
    detailVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || ''))
  }
}

const handleApprove = async (row) => {
  const id = row.underwritingId || row.id
  try {
    await request.put(`/underwriting/${id}/result?result=PASSED`)
    ElMessage.success('核保通过，已生成保单')
    detailVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || ''))
  }
}

const handleReject = async (row) => {
  const id = row.underwritingId || row.id
  try {
    await request.put(`/underwriting/${id}/result?result=REJECTED`)
    ElMessage.success('已拒保')
    detailVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || ''))
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
