<!--
 * 数据统计页面
 * 功能: 显示系统关键指标统计数据，包括客户、保单、理赔、产品分布等
 * API: GET /api/report/dashboard, GET /api/report/policy, GET /api/report/claim, GET /api/report/product
 * 数据来源: customer-service, policy-service, claims-service, finance-service
 -->
<template>
  <div class="report-container">
    <div class="page-header">
      <h2>数据统计</h2>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-icon" style="background: #409EFF">
            <el-icon :size="30"><User /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.totalCustomers }}</div>
            <div class="stats-label">客户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-icon" style="background: #67C23A">
            <el-icon :size="30"><Document /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.totalPolicies }}</div>
            <div class="stats-label">保单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-icon" style="background: #E6A23C">
            <el-icon :size="30"><Money /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-value">¥{{ formatMoney(stats.totalPremium) }}</div>
            <div class="stats-label">总保费</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-icon" style="background: #F56C6C">
            <el-icon :size="30"><Warning /></el-icon>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ Number(stats.pendingClaims) || 0 }}</div>
            <div class="stats-label">待处理理赔</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>保单统计</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="总保单">{{ stats.policyStatistics?.totalPolicies || 0 }}</el-descriptions-item>
            <el-descriptions-item label="生效中">{{ stats.policyStatistics?.activePolicies || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已过期">{{ stats.policyStatistics?.expiredPolicies || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已退保">{{ stats.policyStatistics?.cancelledPolicies || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>理赔统计</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="总理赔">{{ stats.claimStatistics?.totalClaims || 0 }}</el-descriptions-item>
            <el-descriptions-item label="待处理">{{ stats.claimStatistics?.pendingClaims || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已通过">{{ stats.claimStatistics?.approvedClaims || 0 }}</el-descriptions-item>
            <el-descriptions-item label="已拒绝">{{ stats.claimStatistics?.rejectedClaims || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>产品分布</span>
          </template>
          <div class="product-distribution">
            <div v-for="(count, name) in stats.productStatistics?.productCounts || {}" :key="name" class="product-item">
              <span class="product-name">{{ name }}</span>
              <el-progress :percentage="getPercentage(count)" :stroke-width="20">
                <span>{{ count }}</span>
              </el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, Money, Warning } from '@element-plus/icons-vue'
import request from '@/api'

const stats = ref({})
const totalProductCount = ref(0)

const loadStats = async () => {
  try {
    const [dashboard, policy, claim, product] = await Promise.all([
      request.get('/api/report/dashboard'),
      request.get('/api/report/policy'),
      request.get('/api/report/claim'),
      request.get('/api/report/product')
    ])
    
    stats.value = {
      ...dashboard.data,
      policyStatistics: policy.data,
      claimStatistics: claim.data,
      productStatistics: product.data
    }
    
    const productCounts = product.data?.productCounts || {}
    totalProductCount.value = Object.values(productCounts).reduce((a, b) => a + b, 0)
  } catch (e) {
    console.error('Failed to load stats:', e)
  }
}

const formatMoney = (value) => {
  if (!value) return '0'
  const num = typeof value === 'number' ? value : Number(value) || 0
  return new Intl.NumberFormat('zh-CN', { maximumFractionDigits: 0 }).format(num)
}

const getPercentage = (count) => {
  if (!totalProductCount.value) return 0
  return Math.round((count / totalProductCount.value) * 100)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.report-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stats-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.chart-row {
  margin-bottom: 20px;
}

.product-distribution {
  padding: 10px 0;
}

.product-item {
  margin-bottom: 15px;
}

.product-name {
  display: inline-block;
  width: 100px;
  font-size: 14px;
}

.product-item .el-progress {
  display: inline-block;
  width: calc(100% - 120px);
}
</style>
