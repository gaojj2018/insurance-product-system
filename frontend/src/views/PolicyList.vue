<template>
  <div class="page-container">
    <div class="page-header">
      <h2>保单管理</h2>
    </div>
    
    <el-card>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="policyNo" label="保单号" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="insuredName" label="被保人" width="100" />
        <el-table-column prop="coverageAmount" label="保额" width="120">
          <template #default="{ row }">
            ¥{{ row.coverageAmount?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="保费" width="100">
          <template #default="{ row }">
            ¥{{ row.premium?.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="policyStatus" label="保单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.policyStatus)">
              {{ getStatusText(row.policyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="effectiveDate" label="生效日期" width="120" />
        <el-table-column prop="expiryDate" label="到期日期" width="120" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="warning" size="small" @click="handleChange(row)">变更</el-button>
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
    <el-dialog v-model="detailVisible" title="保单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="保单号">{{ currentDetail.policyNo }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentDetail.productName }}</el-descriptions-item>
        <el-descriptions-item label="被保人">{{ currentDetail.insuredName }}</el-descriptions-item>
        <el-descriptions-item label="保额">¥{{ currentDetail.coverage?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="保费">¥{{ currentDetail.premium?.toLocaleString() }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.policyStatus)">{{ getStatusText(currentDetail.policyStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="生效日期">{{ currentDetail.effectiveDate }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ currentDetail.expiryDate }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentDetail = ref(null)

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
    const response = await request.post('/policy/page', {
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

const handleView = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleChange = (row) => {
  ElMessage.info('保单变更: ' + row.policyNo)
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
