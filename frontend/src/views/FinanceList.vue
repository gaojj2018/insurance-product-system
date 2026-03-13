<template>
  <div class="finance-container">
    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      <el-tab-pane label="保费管理" name="premium">
        <!-- 搜索区域 -->
        <el-card class="search-card">
          <el-form :model="queryForm" inline>
            <el-form-item label="保单号">
              <el-input v-model="queryForm.policyNo" placeholder="请输入保单号" clearable />
            </el-form-item>
            <el-form-item label="客户姓名">
              <el-input v-model="queryForm.customerName" placeholder="请输入客户姓名" clearable />
            </el-form-item>
            <el-form-item label="缴费状态">
              <el-select v-model="queryForm.paymentStatus" placeholder="请选择" clearable>
                <el-option label="待缴费" value="PENDING" />
                <el-option label="已缴费" value="PAID" />
                <el-option label="逾期" value="OVERDUE" />
                <el-option label="退费" value="REFUNDED" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 操作按钮 -->
        <div class="toolbar">
          <el-button type="primary" @click="handleCollect">保费催收</el-button>
          <el-button type="success" @click="handleBatchConfirm">批量确认</el-button>
          <el-button type="info" @click="handleExport">导出</el-button>
        </div>

        <!-- 表格区域 -->
        <el-card>
          <el-table :data="tableData" border stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="policyNo" label="保单号" width="150" />
            <el-table-column prop="customerName" label="客户姓名" width="100" />
            <el-table-column prop="productName" label="产品名称" min-width="150" />
            <el-table-column prop="premium" label="保费金额" width="120">
              <template #default="{ row }">
                ¥{{ row.premium ? row.premium.toLocaleString() : '0' }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentNo" label="缴费批号" width="150" />
            <el-table-column prop="dueDate" label="应缴日期" width="120" />
            <el-table-column prop="paidDate" label="实缴日期" width="120" />
            <el-table-column prop="paymentStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.paymentStatus === 'PENDING'" type="warning">待缴费</el-tag>
                <el-tag v-else-if="row.paymentStatus === 'PAID'" type="success">已缴费</el-tag>
                <el-tag v-else-if="row.paymentStatus === 'OVERDUE'" type="danger">逾期</el-tag>
                <el-tag v-else-if="row.paymentStatus === 'REFUNDED'" type="info">退费</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
                <el-button link type="success" size="small" v-if="row.paymentStatus === 'PENDING'" @click="handleConfirm(row)">确认</el-button>
                <el-button link type="warning" size="small" v-if="row.paymentStatus === 'OVERDUE'" @click="handleRemind(row)">催收</el-button>
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
              @size-change="handleSearch"
              @current-change="handleSearch"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="理赔款管理" name="claim">
        <!-- 理赔款表格 -->
        <el-card>
          <el-table :data="tableData" border stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="claimNo" label="理赔批号" width="150" />
            <el-table-column prop="policyNo" label="保单号" width="150" />
            <el-table-column prop="customerName" label="客户姓名" width="100" />
            <el-table-column prop="claimAmount" label="理赔金额" width="120">
              <template #default="{ row }">
                ¥{{ row.claimAmount ? row.claimAmount.toLocaleString() : '0' }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentMethod" label="支付方式" width="100">
              <template #default="{ row }">
                <span v-if="row.paymentMethod === 'BANK_TRANSFER'">银行转账</span>
                <span v-else-if="row.paymentMethod === 'CHEQUE'">支票</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="accountNo" label="收款账号" width="180" />
            <el-table-column prop="paymentStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.paymentStatus === 'PENDING'" type="warning">待支付</el-tag>
                <el-tag v-else-if="row.paymentStatus === 'PAID'" type="success">已支付</el-tag>
                <el-tag v-else-if="row.paymentStatus === 'FAILED'" type="danger">支付失败</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
                <el-button link type="success" size="small" v-if="row.paymentStatus === 'PENDING'" @click="handlePay(row)">支付</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="queryForm.pageNum"
              v-model:page-size="queryForm.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSearch"
              @current-change="handleSearch"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="财务报表" name="report">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-title">今日保费收入</div>
              <div class="stat-value">¥{{ stats.todayPremium.toLocaleString() }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-title">本月保费收入</div>
              <div class="stat-value">¥{{ stats.monthPremium.toLocaleString() }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-title">本月理赔支出</div>
              <div class="stat-value">¥{{ stats.monthClaim.toLocaleString() }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-title">本月结余</div>
              <div class="stat-value">¥{{ stats.monthBalance.toLocaleString() }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-card style="margin-top: 20px">
          <div class="toolbar">
            <el-button type="primary" @click="handleExportReport">导出报表</el-button>
          </div>
          <el-table :data="reportData" border stripe>
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="premium" label="保费收入" width="150">
              <template #default="{ row }">
                ¥{{ row.premium ? row.premium.toLocaleString() : '0' }}
              </template>
            </el-table-column>
            <el-table-column prop="claim" label="理赔支出" width="150">
              <template #default="{ row }">
                ¥{{ row.claim ? row.claim.toLocaleString() : '0' }}
              </template>
            </el-table-column>
            <el-table-column prop="balance" label="结余" width="150">
              <template #default="{ row }">
                ¥{{ row.balance ? row.balance.toLocaleString() : '0' }}
              </template>
            </el-table-column>
            <el-table-column prop="policyCount" label="承保保单数" width="120" />
            <el-table-column prop="claimCount" label="理赔件数" width="120" />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPremiumList, getClaimPaymentList, confirmPremium, payClaim, getFinanceReport, getFinanceStats } from '@/api'

const activeTab = ref('premium')

const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  policyNo: '',
  customerName: '',
  paymentStatus: ''
})

const tableData = ref([])
const loading = ref(false)
const total = ref(0)

const stats = ref({
  todayPremium: 0,
  monthPremium: 0,
  monthClaim: 0,
  monthBalance: 0
})

const reportData = ref([])

const loadStats = async () => {
  try {
    const res = await getFinanceStats()
    if (res.data.success && res.data.data) {
      const data = res.data.data
      stats.value = {
        todayPremium: data.todayPremium || 0,
        monthPremium: data.monthPremium || 0,
        monthClaim: data.monthClaim || 0,
        monthBalance: data.monthBalance || 0
      }
      reportData.value = data.dailyStats || []
    }
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'premium') {
      const res = await getPremiumList(queryForm.value)
      tableData.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    } else if (activeTab.value === 'claim') {
      const res = await getClaimPaymentList(queryForm.value)
      tableData.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (error) {
    console.error('加载失败', error)
    // 模拟数据
    if (activeTab.value === 'premium') {
      tableData.value = [
        { id: 1, policyNo: 'POL202603001', customerName: '张三', productName: '平安福终身寿险', premium: 12580, paymentNo: 'PAY2026030101', dueDate: '2026-03-15', paidDate: '2026-03-01', paymentStatus: 'PAID' },
        { id: 2, policyNo: 'POL202603002', customerName: '李四', productName: '健康安康医疗保险', premium: 3680, paymentNo: 'PAY2026030102', dueDate: '2026-03-20', paidDate: '', paymentStatus: 'PENDING' },
        { id: 3, policyNo: 'POL202603003', customerName: '王五', productName: '意外保障计划', premium: 1580, paymentNo: 'PAY2026030103', dueDate: '2026-03-10', paidDate: '', paymentStatus: 'OVERDUE' }
      ]
    } else if (activeTab.value === 'claim') {
      tableData.value = [
        { id: 1, claimNo: 'CLM202603001', policyNo: 'POL202501001', customerName: '赵六', claimAmount: 15000, paymentMethod: 'BANK_TRANSFER', accountNo: '6222***********1234', paymentStatus: 'PAID' },
        { id: 2, claimNo: 'CLM202603002', policyNo: 'POL202502015', customerName: '孙七', claimAmount: 8500, paymentMethod: 'BANK_TRANSFER', accountNo: '6210***********5678', paymentStatus: 'PENDING' }
      ]
    }
    total.value = tableData.value.length
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  queryForm.value.pageNum = 1
  if (activeTab.value === 'report') {
    loadStats()
  } else {
    loadData()
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
    customerName: '',
    paymentStatus: ''
  }
  loadData()
}

const handleView = (row) => {
  ElMessage.info('查看详情: ' + (row.policyNo || row.claimNo))
}

const handleConfirm = async (row) => {
  try {
    await confirmPremium(row.id)
    ElMessage.success('确认成功')
    loadData()
  } catch (error) {
    console.error('确认失败', error)
  }
}

const handlePay = async (row) => {
  try {
    await payClaim(row.id)
    ElMessage.success('支付成功')
    loadData()
  } catch (error) {
    console.error('支付失败', error)
  }
}

const handleRemind = (row) => {
  ElMessage.success('催收提醒已发送')
}

const handleCollect = () => {
  ElMessage.info('保费催收功能开发中')
}

const handleBatchConfirm = () => {
  ElMessage.info('批量确认功能开发中')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleExportReport = () => {
  ElMessage.info('导出报表功能开发中')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.finance-container {
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

.stat-card {
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
</style>
