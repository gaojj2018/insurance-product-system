<!--
 * 产品对比页面
 * 功能: 选择多个产品进行对比，展示产品的条款和险种差异
 * API: GET /product/list (获取产品列表), GET /clause/product/:id, GET /coverage/product/:id
 -->
<template>
  <div class="compare-container">
    <div class="page-header">
      <h2>产品对比</h2>
    </div>
    
    <el-card>
      <div class="select-area">
        <el-select v-model="selectedProducts" multiple placeholder="选择产品进行对比（最多3个）" @change="handleSelectionChange">
          <el-option v-for="product in productList" :key="product.productId" :label="product.productName" :value="product.productId" />
        </el-select>
        <el-button type="primary" @click="loadCompare" :disabled="selectedProducts.length < 2">开始对比</el-button>
      </div>
      
      <div v-if="compareProducts.length > 0" class="compare-table">
        <el-table :data="compareData" border stripe>
          <el-table-column prop="feature" label="对比项" width="150" fixed />
          <el-table-column v-for="product in compareProducts" :key="product.productId" :label="product.productName" min-width="180">
            <template #default="{ row }">
              <span v-html="row.values[product.productId] || '-'"></span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <el-empty v-else description="请选择至少2个产品进行对比" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api'

const productList = ref([])
const selectedProducts = ref([])
const compareProducts = ref([])
const compareData = ref([])

const loadProducts = async () => {
  try {
    const res = await request.post('/api/product/page', { pageNum: 1, pageSize: 100 })
    productList.value = res.data.data?.records || []
  } catch (e) {
    console.error('Failed to load products:', e)
  }
}

const handleSelectionChange = (val) => {
  if (val.length > 3) {
    selectedProducts.value = val.slice(0, 3)
  }
}

const loadCompare = async () => {
  try {
    const res = await request.get('/api/product/compare', { params: { ids: selectedProducts.value.join(',') } })
    compareProducts.value = res.data.data?.products || []
    compareData.value = res.data.data?.comparison || []
  } catch (e) {
    const products = productList.value.filter(p => selectedProducts.value.includes(p.productId))
    compareProducts.value = products
    compareData.value = buildCompareData(products)
  }
}

const buildCompareData = (products) => {
  return [
    { feature: '产品代码', values: mapValues(products, 'productCode') },
    { feature: '产品类型', values: mapValues(products, 'productType', typeMap) },
    { feature: '保障期间', values: mapValues(products, 'coveragePeriod') },
    { feature: '缴费期间', values: mapValues(products, 'paymentPeriod') },
    { feature: '最低保额', values: mapValues(products, 'minCoverage', v => v ? v + '元' : '-') },
    { feature: '最高保额', values: mapValues(products, 'maxCoverage', v => v ? v + '元' : '-') },
    { feature: '产品描述', values: mapValues(products, 'description', v => v || '-') },
  ]
}

const mapValues = (products, field, formatter = v => v || '-') => {
  const result = {}
  products.forEach(p => {
    result[p.productId] = formatter(p[field])
  })
  return result
}

const typeMap = {
  'LIFE': '人寿保险',
  'PROPERTY': '财产保险',
  'ACCIDENT': '意外保险',
  'HEALTH': '健康保险'
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.compare-container {
  padding: 20px;
}

.select-area {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.select-area .el-select {
  flex: 1;
  max-width: 400px;
}

.compare-table {
  margin-top: 20px;
}
</style>
