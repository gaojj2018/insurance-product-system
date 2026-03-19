<!--
 * 理赔管理页面
 * 功能: 理赔案件的报案、审核、付款操作
 * API: GET /claims/page, POST /claims, PUT /claims/:id
 -->
<template>
  <div class="page-container">
    <div class="page-header">
      <h2>理赔管理</h2>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <div class="search-row">
        <div class="search-item">
          <label>理赔单号</label>
          <el-input v-model="queryForm.claimNo" placeholder="请输入理赔单号" clearable />
        </div>
        <div class="search-item">
          <label>保单号</label>
          <el-input v-model="queryForm.policyNo" placeholder="请输入保单号" clearable />
        </div>
        <div class="search-item">
          <label>申请人</label>
          <el-input v-model="queryForm.claimantName" placeholder="请输入申请人" clearable />
        </div>
        <div class="search-item">
          <label>理赔状态</label>
          <el-select v-model="queryForm.claimStatus" placeholder="请选择" clearable>
            <el-option label="已报案" value="REPORTED" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已审核" value="APPROVED" />
            <el-option label="已付款" value="PAID" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleCreate">新建理赔</el-button>
        </div>
      </div>
    </div>
    
    <!-- 表格区域 -->
    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading">
        <template #empty>
          <el-empty description="暂无理赔数据" />
        </template>
        <el-table-column prop="claimNo" label="理赔单号" width="150" sortable />
        <el-table-column prop="policyNo" label="保单号" width="150" sortable />
        <el-table-column prop="claimantName" label="申请人" width="100" />
        <el-table-column prop="accidentType" label="事故类型" width="100" />
        <el-table-column prop="claimAmount" label="申请金额" width="120">
          <template #default="{ row }">
            ¥{{ row.claimAmount?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="approveAmount" label="审核金额" width="120">
          <template #default="{ row }">
            ¥{{ row.approveAmount?.toLocaleString() || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="claimStatus" label="理赔状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.claimStatus)">
              {{ getStatusText(row.claimStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="accidentDate" label="事故日期" width="120" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handleProcess(row)" v-if="row.claimStatus === 'REPORTED'">处理</el-button>
            <el-button link type="warning" size="small" @click="handlePay(row)" v-if="row.claimStatus === 'APPROVED'">付款</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="detailVisible" title="理赔详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="理赔单号">{{ currentDetail.claimNo }}</el-descriptions-item>
        <el-descriptions-item label="保单号">{{ currentDetail.policyNo }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentDetail.claimantName }}</el-descriptions-item>
        <el-descriptions-item label="事故类型">{{ currentDetail.accidentType }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">¥{{ currentDetail.claimAmount?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="审核金额">¥{{ currentDetail.approveAmount?.toLocaleString() || '-' }}</el-descriptions-item>
        <el-descriptions-item label="事故日期">{{ currentDetail.accidentDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.claimStatus)">{{ getStatusText(currentDetail.claimStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="事故地点" :span="2">{{ currentDetail.accidentLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="事故描述" :span="2">{{ currentDetail.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 处理/审核弹窗 -->
    <el-dialog v-model="processVisible" title="理赔审核" width="500px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="理赔单号">
          <el-input v-model="processForm.claimNo" disabled />
        </el-form-item>
        <el-form-item label="审核金额">
          <el-input-number v-model="processForm.approvedAmount" :min="0" :step="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="processForm.opinion" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject">拒绝</el-button>
        <el-button type="success" @click="handleApprove">通过</el-button>
      </template>
    </el-dialog>

    <!-- 付款弹窗 -->
    <el-dialog v-model="payVisible" title="理赔付款" width="400px">
      <el-form :model="payForm" label-width="100px">
        <el-form-item label="理赔单号">
          <el-input v-model="payForm.claimNo" disabled />
        </el-form-item>
        <el-form-item label="支付账号">
          <el-input v-model="payForm.payAccount" placeholder="请输入银行账号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确认付款</el-button>
      </template>
    </el-dialog>

    <!-- 新建理赔弹窗 -->
    <el-dialog v-model="createVisible" title="新建理赔报案" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保单号" prop="policyNo">
              <el-select 
                v-model="createForm.policyId" 
                placeholder="请选择保单" 
                style="width: 100%"
                filterable
                :loading="policyLoading"
                @change="handlePolicyChange"
              >
                <el-option
                  v-for="policy in policyList"
                  :key="policy.policyId"
                  :label="policy.policyNo"
                  :value="policy.policyId"
                >
                  <span>{{ policy.policyNo }}</span>
                  <span style="color: #999; font-size: 12px; margin-left: 8px">
                    ({{ policy.productName || '未知产品' }})
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="createForm.applicantName" placeholder="请输入申请人姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="事故类型" prop="accidentType">
              <el-select v-model="createForm.accidentType" placeholder="请选择" style="width: 100%">
                <el-option label="意外身故" value="意外身故" />
                <el-option label="意外医疗" value="意外医疗" />
                <el-option label="疾病医疗" value="疾病医疗" />
                <el-option label="重大疾病" value="重大疾病" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="事故日期" prop="accidentDate">
              <el-date-picker v-model="createForm.accidentDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="事故地点">
          <el-input v-model="createForm.accidentLocation" placeholder="请输入事故地点" />
        </el-form-item>
        <el-form-item label="事故描述">
          <el-input v-model="createForm.accidentDesc" type="textarea" :rows="3" placeholder="请描述事故经过" />
        </el-form-item>
        <el-form-item label="理赔金额" prop="claimAmount">
          <el-input-number v-model="createForm.claimAmount" :min="0" :step="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="材料上传">
          <el-upload
            v-model:file-list="claimFileList"
            action="#"
            :auto-upload="false"
            :on-change="handleClaimFileChange"
            :on-remove="handleClaimFileRemove"
            list-type="picture"
            :limit="5"
          >
            <el-button type="primary">上传理赔材料</el-button>
            <template #tip>
              <div class="el-upload__tip">支持jpg/png/pdf格式，最多5个文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitClaim" :loading="submitting">提交报案</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request, { deleteClaims, getPolicyList } from '@/api/index'

const claimFileList = ref([])
const claimUploadedFiles = ref([])
const policyList = ref([])
const policyLoading = ref(false)

const handleClaimFileChange = (file, files) => {
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  claimUploadedFiles.value.push(file)
}

const handleClaimFileRemove = (file, files) => {
  const index = claimUploadedFiles.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    claimUploadedFiles.value.splice(index, 1)
  }
}

const loadPolicyList = async () => {
  policyLoading.value = true
  try {
    const res = await getPolicyList({ pageNum: 1, pageSize: 100 })
    if (res.data.code === 200) {
      policyList.value = res.data.data.records || []
    }
  } catch (error) {
    console.error('加载保单列表失败', error)
  } finally {
    policyLoading.value = false
  }
}

const handlePolicyChange = (policyId) => {
  const selectedPolicy = policyList.value.find(p => p.policyId === policyId)
  if (selectedPolicy) {
    createForm.value.policyNo = selectedPolicy.policyNo
  }
}

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  claimNo: '',
  policyNo: '',
  claimantName: '',
  claimStatus: ''
})
const detailVisible = ref(false)
const currentDetail = ref(null)
const processVisible = ref(false)
const payVisible = ref(false)
const createVisible = ref(false)
const submitting = ref(false)

const processForm = ref({
  claimId: null,
  claimNo: '',
  approvedAmount: 0,
  opinion: ''
})

const payForm = ref({
  claimId: null,
  claimNo: '',
  payAccount: ''
})

const createForm = ref({
  policyId: null,
  policyNo: '',
  applicantName: '',
  accidentType: '',
  accidentDate: '',
  accidentLocation: '',
  accidentDesc: '',
  claimAmount: 0
})

const createRules = {
  policyId: [{ required: true, message: '请选择保单', trigger: 'change' }],
  applicantName: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  accidentType: [{ required: true, message: '请选择事故类型', trigger: 'change' }],
  accidentDate: [{ required: true, message: '请选择事故日期', trigger: 'change' }],
  claimAmount: [{ required: true, message: '请输入理赔金额', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const map = {
    'REPORTED': 'warning',
    'REVIEW': 'primary',
    'CALCULATING': 'primary',
    'APPROVED': 'success',
    'PAID': 'success',
    'REJECTED': 'danger',
    'CLOSED': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'REPORTED': '已报案',
    'REVIEW': '审核中',
    'CALCULATING': '理算中',
    'APPROVED': '已审核',
    'PAID': '已付款',
    'REJECTED': '已拒赔',
    'CLOSED': '已结案'
  }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryForm.value.pageNum,
      pageSize: queryForm.value.pageSize,
      claimNo: queryForm.value.claimNo,
      policyNo: queryForm.value.policyNo,
      claimantName: queryForm.value.claimantName,
      status: queryForm.value.claimStatus
    }
    const response = await request.post('/claims/page', null, { params })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records || []
      total.value = response.data.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
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
    claimNo: '',
    policyNo: '',
    claimantName: '',
    claimStatus: ''
  }
  loadData()
}

const handleView = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleCreate = () => {
  createForm.value = {
    policyId: null,
    policyNo: '',
    applicantName: '',
    accidentType: '',
    accidentDate: '',
    accidentLocation: '',
    accidentDesc: '',
    claimAmount: 0
  }
  claimFileList.value = []
  claimUploadedFiles.value = []
  loadPolicyList()
  createVisible.value = true
}

const handleSubmitClaim = async () => {
  if (!createForm.value.policyId || !createForm.value.applicantName) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitting.value = true
  try {
    const accidentDate = createForm.value.accidentDate ? 
      new Date(createForm.value.accidentDate).toISOString().slice(0,10) : ''
    const res = await request.post('/claims', null, {
      params: {
        policyId: createForm.value.policyId,
        policyNo: createForm.value.policyNo,
        applicantId: 1,
        applicantName: createForm.value.applicantName,
        accidentType: createForm.value.accidentType,
        accidentDate: accidentDate,
        accidentLocation: createForm.value.accidentLocation,
        accidentDesc: createForm.value.accidentDesc,
        claimAmount: createForm.value.claimAmount
      }
    })
    
    let claimId = res.data?.data?.claimId
    
    if (claimUploadedFiles.value.length > 0) {
      for (const file of claimUploadedFiles.value) {
        if (file.raw) {
          const formData = new FormData()
          formData.append('file', file.raw)
          formData.append('type', 'claims')
          await request.post('/api/customer/file/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
          })
        }
      }
      ElMessage.success('理赔材料上传成功')
    }
    
    if (res.data.code === 200) {
      ElMessage.success('理赔报案成功')
      createVisible.value = false
      claimFileList.value = []
      claimUploadedFiles.value = []
      loadData()
    } else {
      ElMessage.error(res.data.message || '报案失败')
    }
  } catch (error) {
    ElMessage.error('报案失败')
  } finally {
    submitting.value = false
  }
}

const handleProcess = (row) => {
  processForm.value = {
    claimId: row.claimsId,
    claimNo: row.claimNo,
    approvedAmount: row.claimAmount,
    opinion: ''
  }
  processVisible.value = true
}

const handleApprove = async () => {
  try {
    const res = await request.put(`/claims/${processForm.value.claimId}/approve`, null, {
      params: {
        approvedAmount: processForm.value.approvedAmount,
        handler: 'admin'
      }
    })
    if (res.data.code === 200) {
      ElMessage.success('审核通过')
      processVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async () => {
  try {
    const res = await request.put(`/claims/${processForm.value.claimId}/reject`, null, {
      params: {
        handler: 'admin'
      }
    })
    if (res.data.code === 200) {
      ElMessage.success('已拒绝')
      processVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handlePay = (row) => {
  payForm.value = {
    claimId: row.claimsId,
    claimNo: row.claimNo,
    payAccount: ''
  }
  payVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该理赔记录吗？', '提示', { type: 'warning' })
    await deleteClaims(row.claimsId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatStatus = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'REPORTED': '已报案',
    'PROCESSING': '处理中',
    'APPROVED': '已审核',
    'PAID': '已付款',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

const confirmPay = async () => {
  if (!payForm.value.payAccount) {
    ElMessage.warning('请输入支付账号')
    return
  }
  try {
    const res = await request.put(`/claims/${payForm.value.claimId}/pay`, null, {
      params: {
        payAccount: payForm.value.payAccount
      }
    })
    if (res.data.code === 200) {
      ElMessage.success('付款成功')
      payVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('付款失败')
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
