<template>
  <div class="product-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="产品代码">
          <el-input v-model="queryForm.productCode" placeholder="请输入产品代码" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="queryForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="产品类型">
          <el-select v-model="queryForm.productType" placeholder="请选择" clearable>
            <el-option label="人寿保险" value="LIFE" />
            <el-option label="财产保险" value="PROPERTY" />
            <el-option label="意外保险" value="ACCIDENT" />
            <el-option label="健康保险" value="HEALTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="在售" value="ACTIVE" />
            <el-option label="停售" value="INACTIVE" />
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
      <el-button type="primary" @click="handleAdd">新增产品</el-button>
    </div>

    <!-- 表格区域 -->
    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="productId" label="ID" width="80" />
        <el-table-column prop="productCode" label="产品代码" width="120" />
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        <el-table-column prop="productType" label="产品类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.productType === 'LIFE'">人寿保险</el-tag>
            <el-tag v-else-if="row.productType === 'PROPERTY'" type="success">财产保险</el-tag>
            <el-tag v-else-if="row.productType === 'ACCIDENT'" type="warning">意外保险</el-tag>
            <el-tag v-else-if="row.productType === 'HEALTH'" type="danger">健康保险</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coveragePeriod" label="保障期间" width="100" />
        <el-table-column prop="paymentPeriod" label="缴费期间" width="100" />
        <el-table-column prop="minCoverage" label="最低保额" width="100">
          <template #default="{ row }">
            {{ row.minCoverage ? row.minCoverage.toLocaleString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 'ACTIVE'" type="success">在售</el-tag>
            <el-tag v-else-if="row.status === 'INACTIVE'" type="danger">停售</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)" v-permission="'product:edit'">编辑</el-button>
            <el-button link type="success" size="small" v-if="row.status === 'DRAFT'" @click="handlePublish(row)" v-permission="'product:publish'">发布</el-button>
            <el-button link type="danger" size="small" v-if="row.status === 'ACTIVE'" @click="handleStop(row)" v-permission="'product:publish'">停售</el-button>
            <el-button link type="info" size="small" @click="handleView(row)">详情</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-permission="'product:delete'">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="产品代码" prop="productCode">
          <el-input v-model="form.productCode" placeholder="请输入产品代码" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="产品类型" prop="productType">
          <el-select v-model="form.productType" placeholder="请选择产品类型">
            <el-option label="人寿保险" value="LIFE" />
            <el-option label="财产保险" value="PROPERTY" />
            <el-option label="意外保险" value="ACCIDENT" />
            <el-option label="健康保险" value="HEALTH" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保障期间" prop="coveragePeriod">
              <el-input v-model="form.coveragePeriod" placeholder="如：1年/10年/终身" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费期间" prop="paymentPeriod">
              <el-input v-model="form.paymentPeriod" placeholder="如：1年/5年/10年" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低保额" prop="minCoverage">
              <el-input-number v-model="form.minCoverage" :min="0" :step="1000" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高保额" prop="maxCoverage">
              <el-input-number v-model="form.maxCoverage" :min="0" :step="1000" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="产品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入产品描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 产品详情对话框（包含条款和险种） -->
    <el-dialog v-model="detailDialogVisible" title="产品详情" width="900px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="产品代码">{{ currentProduct.productCode }}</el-descriptions-item>
            <el-descriptions-item label="产品名称">{{ currentProduct.productName }}</el-descriptions-item>
            <el-descriptions-item label="产品类型">
              <el-tag v-if="currentProduct.productType === 'LIFE'">人寿保险</el-tag>
              <el-tag v-else-if="currentProduct.productType === 'PROPERTY'" type="success">财产保险</el-tag>
              <el-tag v-else-if="currentProduct.productType === 'ACCIDENT'" type="warning">意外保险</el-tag>
              <el-tag v-else-if="currentProduct.productType === 'HEALTH'" type="danger">健康保险</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="保障期间">{{ currentProduct.coveragePeriod }}</el-descriptions-item>
            <el-descriptions-item label="缴费期间">{{ currentProduct.paymentPeriod }}</el-descriptions-item>
            <el-descriptions-item label="最低保额">{{ currentProduct.minCoverage?.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="最高保额">{{ currentProduct.maxCoverage?.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag v-if="currentProduct.status === 'DRAFT'" type="info">草稿</el-tag>
              <el-tag v-else-if="currentProduct.status === 'ACTIVE'" type="success">在售</el-tag>
              <el-tag v-else-if="currentProduct.status === 'INACTIVE'" type="danger">停售</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="产品描述" :span="2">{{ currentProduct.description }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="条款配置" name="clauses">
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="handleAddClause">新增条款</el-button>
          </div>
          <el-table :data="clauseList" border stripe v-loading="clauseLoading">
            <el-table-column prop="clauseCode" label="条款代码" width="120" />
            <el-table-column prop="clauseName" label="条款名称" min-width="150" />
            <el-table-column prop="clauseType" label="条款类型" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.clauseType === 'BASIC'">基本条款</el-tag>
                <el-tag v-else-if="row.clauseType === 'EXTRA'" type="warning">附加条款</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="条款内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEditClause(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDeleteClause(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="险种配置" name="coverages">
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="handleAddCoverage">新增险种</el-button>
          </div>
          <el-table :data="coverageList" border stripe v-loading="coverageLoading">
            <el-table-column prop="coverageCode" label="险种代码" width="120" />
            <el-table-column prop="coverageName" label="险种名称" min-width="150" />
            <el-table-column prop="coverageType" label="险种类型" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.coverageType === 'MAIN'">主险</el-tag>
                <el-tag v-else-if="row.coverageType === 'RIDER'" type="warning">附加险</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="coverageAmount" label="保额/金额" width="120">
              <template #default="{ row }">
                {{ row.coverageAmount?.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="premium" label="保费" width="100">
              <template #default="{ row }">
                {{ row.premium?.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEditCoverage(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDeleteCoverage(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 条款新增/编辑对话框 -->
    <el-dialog v-model="clauseDialogVisible" :title="clauseDialogTitle" width="600px">
      <el-form :model="clauseForm" :rules="clauseRules" ref="clauseFormRef" label-width="100px">
        <el-form-item label="条款代码" prop="clauseCode">
          <el-input v-model="clauseForm.clauseCode" placeholder="请输入条款代码" />
        </el-form-item>
        <el-form-item label="条款名称" prop="clauseName">
          <el-input v-model="clauseForm.clauseName" placeholder="请输入条款名称" />
        </el-form-item>
        <el-form-item label="条款类型" prop="clauseType">
          <el-select v-model="clauseForm.clauseType" placeholder="请选择">
            <el-option label="基本条款" value="BASIC" />
            <el-option label="附加条款" value="EXTRA" />
          </el-select>
        </el-form-item>
        <el-form-item label="条款内容" prop="content">
          <el-input v-model="clauseForm.clauseContent" type="textarea" :rows="6" placeholder="请输入条款内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="clauseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleClauseSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 险种新增/编辑对话框 -->
    <el-dialog v-model="coverageDialogVisible" :title="coverageDialogTitle" width="600px">
      <el-form :model="coverageForm" :rules="coverageRules" ref="coverageFormRef" label-width="100px">
        <el-form-item label="险种代码" prop="coverageCode">
          <el-input v-model="coverageForm.coverageCode" placeholder="请输入险种代码" />
        </el-form-item>
        <el-form-item label="险种名称" prop="coverageName">
          <el-input v-model="coverageForm.coverageName" placeholder="请输入险种名称" />
        </el-form-item>
        <el-form-item label="险种类型" prop="coverageType">
          <el-select v-model="coverageForm.coverageType" placeholder="请选择">
            <el-option label="主险" value="MAIN" />
            <el-option label="附加险" value="RIDER" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低保额" prop="minSumInsured">
              <el-input-number v-model="coverageForm.minSumInsured" :min="0" :step="1000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高保额" prop="maxSumInsured">
              <el-input-number v-model="coverageForm.maxSumInsured" :min="0" :step="1000" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="基准费率" prop="basePremiumRate">
              <el-input-number v-model="coverageForm.basePremiumRate" :min="0" :precision="4" :step="0.0001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计算方式" prop="calculationType">
              <el-select v-model="coverageForm.calculationType" placeholder="请选择" style="width: 100%">
                <el-option label="固定" value="FIXED" />
                <el-option label="比例" value="PERCENTAGE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="coverageDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCoverageSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, createProduct, updateProduct, deleteProduct, publishProduct, stopProduct, getClauseByProductId, getCoverageByProductId, createClause, updateClause, deleteClause, createCoverage, updateCoverage, deleteCoverage } from '@/api'

// 查询表单
const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  productCode: '',
  productName: '',
  productType: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增产品')
const formRef = ref()
const form = ref({
  productId: null,
  productCode: '',
  productName: '',
  productType: '',
  coveragePeriod: '',
  paymentPeriod: '',
  minCoverage: 0,
  maxCoverage: 0,
  description: '',
  status: 'DRAFT'
})

// 表单校验规则
const rules = {
  productCode: [{ required: true, message: '请输入产品代码', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  productType: [{ required: true, message: '请选择产品类型', trigger: 'change' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const activeTab = ref('basic')
const currentProduct = ref({})
const clauseList = ref([])
const coverageList = ref([])
const clauseLoading = ref(false)
const coverageLoading = ref(false)

// 条款对话框
const clauseDialogVisible = ref(false)
const clauseDialogTitle = ref('新增条款')
const clauseFormRef = ref()
const clauseForm = ref({
  clauseId: null,
  productId: null,
  clauseCode: '',
  clauseName: '',
  clauseType: 'BASIC',
  clauseContent: ''
})
const clauseRules = {
  clauseCode: [{ required: true, message: '请输入条款代码', trigger: 'blur' }],
  clauseName: [{ required: true, message: '请输入条款名称', trigger: 'blur' }],
  clauseType: [{ required: true, message: '请选择条款类型', trigger: 'change' }]
}

// 险种对话框
const coverageDialogVisible = ref(false)
const coverageDialogTitle = ref('新增险种')
const coverageFormRef = ref()
const coverageForm = ref({
  coverageId: null,
  productId: null,
  coverageCode: '',
  coverageName: '',
  coverageType: 'MAIN',
  coverageKind: 'MAIN',
  minSumInsured: 0,
  maxSumInsured: 0,
  basePremiumRate: 0,
  calculationType: 'FIXED'
})
const coverageRules = {
  coverageCode: [{ required: true, message: '请输入险种代码', trigger: 'blur' }],
  coverageName: [{ required: true, message: '请输入险种名称', trigger: 'blur' }],
  coverageType: [{ required: true, message: '请选择险种类型', trigger: 'change' }]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductList(queryForm.value)
    tableData.value = res.data.data.records || []
    total.value = res.data.data.total || 0
  } catch (error) {
    console.error('加载失败', error)
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
    productCode: '',
    productName: '',
    productType: '',
    status: ''
  }
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增产品'
  form.value = {
    productId: null,
    productCode: '',
    productName: '',
    productType: '',
    coveragePeriod: '',
    paymentPeriod: '',
    minCoverage: 0,
    maxCoverage: 0,
    description: '',
    status: 'DRAFT'
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑产品'
  form.value = { ...row }
  dialogVisible.value = true
}

// 查看详情
const handleView = async (row) => {
  currentProduct.value = { ...row }
  detailDialogVisible.value = true
  activeTab.value = 'basic'
  
  // 加载条款和险种
  clauseLoading.value = true
  coverageLoading.value = true
  try {
    const [clauseRes, coverageRes] = await Promise.all([
      getClauseByProductId(row.productId),
      getCoverageByProductId(row.productId)
    ])
    clauseList.value = clauseRes.data.data || []
    coverageList.value = coverageRes.data.data || []
  } catch (error) {
    console.error('加载条款/险种失败', error)
  } finally {
    clauseLoading.value = false
    coverageLoading.value = false
  }
}

// 条款操作
const handleAddClause = () => {
  clauseDialogTitle.value = '新增条款'
  clauseForm.value = {
    clauseId: null,
    productId: currentProduct.value.productId,
    clauseCode: '',
    clauseName: '',
    clauseType: 'BASIC',
    content: ''
  }
  clauseDialogVisible.value = true
}

const handleEditClause = (row) => {
  clauseDialogTitle.value = '编辑条款'
  clauseForm.value = { ...row, productId: currentProduct.value.productId }
  clauseDialogVisible.value = true
}

const handleClauseSubmit = async () => {
  try {
    await clauseFormRef.value.validate()
    if (clauseForm.value.clauseId) {
      await updateClause(clauseForm.value)
      ElMessage.success('更新成功')
    } else {
      await createClause(clauseForm.value)
      ElMessage.success('创建成功')
    }
    clauseDialogVisible.value = false
    // 重新加载条款
    const res = await getClauseByProductId(currentProduct.value.productId)
    clauseList.value = res.data.data || []
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleDeleteClause = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该条款吗？', '提示', { type: 'warning' })
    await deleteClause(row.clauseId)
    ElMessage.success('删除成功')
    // 重新加载条款
    const res = await getClauseByProductId(currentProduct.value.productId)
    clauseList.value = res.data.data || []
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

// 险种操作
const handleAddCoverage = () => {
  coverageDialogTitle.value = '新增险种'
  coverageForm.value = {
    coverageId: null,
    productId: currentProduct.value.productId,
    coverageCode: '',
    coverageName: '',
    coverageType: 'MAIN',
    coverageAmount: 0,
    premium: 0
  }
  coverageDialogVisible.value = true
}

const handleEditCoverage = (row) => {
  coverageDialogTitle.value = '编辑险种'
  coverageForm.value = { ...row, productId: currentProduct.value.productId }
  coverageDialogVisible.value = true
}

const handleCoverageSubmit = async () => {
  try {
    await coverageFormRef.value.validate()
    if (coverageForm.value.coverageId) {
      await updateCoverage(coverageForm.value)
      ElMessage.success('更新成功')
    } else {
      await createCoverage(coverageForm.value)
      ElMessage.success('创建成功')
    }
    coverageDialogVisible.value = false
    // 重新加载险种
    const res = await getCoverageByProductId(currentProduct.value.productId)
    coverageList.value = res.data.data || []
  } catch (error) {
    console.error('操作失败', error)
  }
}

const handleDeleteCoverage = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该险种吗？', '提示', { type: 'warning' })
    await deleteCoverage(row.coverageId)
    ElMessage.success('删除成功')
    // 重新加载险种
    const res = await getCoverageByProductId(currentProduct.value.productId)
    coverageList.value = res.data.data || []
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.productId) {
      await updateProduct(form.value)
      ElMessage.success('更新成功')
    } else {
      await createProduct(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败', error)
  }
}

// 发布
const handlePublish = async (row) => {
  try {
    await publishProduct(row.productId)
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    console.error('发布失败', error)
  }
}

// 停售
const handleStop = async (row) => {
  try {
    await stopProduct(row.productId)
    ElMessage.success('停售成功')
    loadData()
  } catch (error) {
    console.error('停售失败', error)
  }
}

// 删除产品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该产品吗？', '提示', { type: 'warning' })
    const res = await deleteProduct(row.productId)
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
.product-container {
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

.tab-toolbar {
  margin-bottom: 15px;
}
</style>
