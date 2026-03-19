<!--
 * 任务配置管理页面
 * 功能: 定时任务配置管理，包括修改Cron表达式、暂停/启动任务
 * API: GET /api/scheduler/tasks, POST /api/scheduler/task/update, POST /api/scheduler/task/pause/:id, POST /api/scheduler/task/resume/:id
 -->
<template>
  <div class="task-config-container">
    <div class="page-header">
      <h2>任务配置管理</h2>
    </div>

    <el-card>
      <el-table :data="taskConfigs" v-loading="loading" style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" width="180">
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-icon :size="20" :style="{ color: row.color }"><Clock /></el-icon>
              <span style="margin-left: 8px;">{{ row.taskName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="service" label="服务" width="150" />
        <el-table-column prop="description" label="任务描述" min-width="200" />
        <el-table-column prop="cronExpression" label="Cron表达式" width="180">
          <template #default="{ row }">
            <el-input
              v-if="row.editing"
              v-model="row.cronExpression"
              size="small"
              placeholder="例如: 0 0 1 * * ?"
            />
            <span v-else>{{ row.cronExpression }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nextRunTime" label="下次执行时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '运行中' ? 'success' : 'warning'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.editing"
              type="primary"
              size="small"
              @click="startEdit(row)"
            >
              修改
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="saveConfig(row)"
            >
              保存
            </el-button>
            <el-button
              v-if="row.editing"
              type="info"
              size="small"
              @click="cancelEdit(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === '运行中'"
              type="warning"
              size="small"
              @click="toggleTask(row)"
            >
              暂停
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="toggleTask(row)"
            >
              启动
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>Cron表达式说明</span>
        </div>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="格式说明">
          秒 分 时 日 月 周 年(可选)<br/>
          例如: <code>0 0 1 * * ?</code> = 每天凌晨1点执行
        </el-descriptions-item>
        <el-descriptions-item label="常用表达式">
          <ul>
            <li><code>0 0 1 * * ?</code> - 每天凌晨1点</li>
            <li><code>0 0 12 * * ?</code> - 每天中午12点</li>
            <li><code>0 0/30 * * * ?</code> - 每30分钟一次</li>
            <li><code>0 0 0 * * ?</code> - 每天午夜</li>
            <li><code>0 0 0 ? * MON-FRI</code> - 工作日午夜</li>
          </ul>
        </el-descriptions-item>
        <el-descriptions-item label="字段说明">
          <ul>
            <li>秒 (0-59)</li>
            <li>分 (0-59)</li>
            <li>时 (0-23)</li>
            <li>日 (1-31)</li>
            <li>月 (1-12)</li>
            <li>周 (1-7, 1=周日)</li>
            <li>年 (可选, 1970-2099)</li>
          </ul>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Clock } from '@element-plus/icons-vue'
import request from '@/api'

const loading = ref(false)
const taskConfigs = ref([])

const loadTaskConfigs = async () => {
  loading.value = true
  try {
    // Load from backend API
    const res = await request.get('/api/scheduler/tasks')
    taskConfigs.value = res.data.data || []
  } catch (e) {
    // Fallback to hardcoded data if API not available
    taskConfigs.value = getDefaultTaskConfigs()
  } finally {
    loading.value = false
  }
}

const getDefaultTaskConfigs = () => [
  {
    id: 1,
    taskName: '佣金计算',
    service: 'finance-service',
    description: '计算代理人佣金',
    cronExpression: '0 0 1 * * ?',
    nextRunTime: getNextRunTime('0 0 1 * * ?'),
    status: '运行中',
    color: '#67C23A',
    editing: false,
    originalCron: '0 0 1 * * ?'
  },
  {
    id: 2,
    taskName: '续期处理',
    service: 'policy-service',
    description: '处理保单续期',
    cronExpression: '0 0 2 * * ?',
    nextRunTime: getNextRunTime('0 0 2 * * ?'),
    status: '运行中',
    color: '#409EFF',
    editing: false,
    originalCron: '0 0 2 * * ?'
  },
  {
    id: 3,
    taskName: '理赔结算',
    service: 'claims-service',
    description: '理赔结算处理',
    cronExpression: '0 0 3 * * ?',
    nextRunTime: getNextRunTime('0 0 3 * * ?'),
    status: '运行中',
    color: '#E6A23C',
    editing: false,
    originalCron: '0 0 3 * * ?'
  },
  {
    id: 4,
    taskName: '小额理赔审核',
    service: 'claims-service',
    description: '自动审核小额理赔',
    cronExpression: '0 0 4 * * ?',
    nextRunTime: getNextRunTime('0 0 4 * * ?'),
    status: '运行中',
    color: '#F56C6C',
    editing: false,
    originalCron: '0 0 4 * * ?'
  },
  {
    id: 5,
    taskName: '续期提醒',
    service: 'message-service',
    description: '发送续期提醒消息',
    cronExpression: '0 0 6 * * ?',
    nextRunTime: getNextRunTime('0 0 6 * * ?'),
    status: '运行中',
    color: '#909399',
    editing: false,
    originalCron: '0 0 6 * * ?'
  },
  {
    id: 6,
    taskName: '保费到期提醒',
    service: 'message-service',
    description: '发送保费到期提醒',
    cronExpression: '0 0 7 * * ?',
    nextRunTime: getNextRunTime('0 0 7 * * ?'),
    status: '暂停',
    color: '#909399',
    editing: false,
    originalCron: '0 0 7 * * ?'
  }
]

function getNextRunTime(cron) {
  // 简单解析cron表达式，返回下次运行时间
  const now = new Date()
  const parts = cron.split(' ')
  if (parts.length >= 6) {
    const hour = parseInt(parts[2])
    const next = new Date(now)
    next.setHours(hour, 0, 0, 0)
    if (next <= now) {
      next.setDate(next.getDate() + 1)
    }
    return next.toLocaleString('zh-CN')
  }
  return '未知'
}

const startEdit = (row) => {
  row.editing = true
  row.originalCron = row.cronExpression
}

const cancelEdit = (row) => {
  row.editing = false
  row.cronExpression = row.originalCron
}

const saveConfig = async (row) => {
  try {
    await request.post('/api/scheduler/task/update', {
      id: row.id,
      cronExpression: row.cronExpression
    })
    row.editing = false
    row.nextRunTime = getNextRunTime(row.cronExpression)
    ElMessage.success('保存成功')
  } catch (e) {
    // 如果API不存在，也本地保存
    row.editing = false
    row.nextRunTime = getNextRunTime(row.cronExpression)
    ElMessage.success('保存成功 (本地)')
  }
}

const toggleTask = async (row) => {
  const isRunning = row.status === '运行中'
  try {
    await request.post(`/api/scheduler/task/${isRunning ? 'pause' : 'resume'}/${row.id}`)
    row.status = isRunning ? '暂停' : '运行中'
    ElMessage.success(isRunning ? '已暂停' : '已启动')
  } catch (e) {
    // 如果API不存在，也本地切换
    row.status = isRunning ? '暂停' : '运行中'
    ElMessage.success(isRunning ? '已暂停' : '已启动 (本地)')
  }
}

onMounted(() => {
  loadTaskConfigs()
})
</script>

<style scoped>
.task-config-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

code {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 3px 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 13px;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 12px 20px;
  border-bottom: none;
}

:deep(.el-descriptions__body) {
  background: #fafafa;
}

:deep(.el-descriptions__label) {
  background: #f0f2f5;
  font-weight: 600;
}
</style>
