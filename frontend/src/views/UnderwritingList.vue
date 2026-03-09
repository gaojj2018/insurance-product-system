<template>
  <div class="page-container">
    <div class="page-header">
      <h2>核保管理</h2>
    </div>
    
    <el-card>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="applicationNo" label="投保单号" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="applicantName" label="投保人" width="100" />
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
        <el-table-column prop="underwriteStatus" label="核保状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.underwriteStatus)">
              {{ getStatusText(row.underwriteStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="success" size="small" @click="handleUnderwrite(row, 'PASSED')" v-if="row.underwriteStatus === 'PENDING'">通过</el-button>
            <el-button link type="danger" size="small" @click="handleUnderwrite(row, 'REJECTED')" v-if="row.underwriteStatus === 'PENDING'">拒保</el-button>
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
    const response = await request.post('/underwriting/page', {
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
  ElMessage.info('查看投保单: ' + row.applicationNo)
}

const handleUnderwrite = async (row, result) => {
  try {
    await request.put(`/api/underwriting/${row.id}/result?result=${result}`)
    ElMessage.success('核保完成')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
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
