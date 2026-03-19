<!--
 * 任务调度页面
 * 功能: 显示定时任务配置列表和执行日志
 * API: GET /api/scheduler/tasks, GET /api/scheduler/logs
 -->
<template>
  <div class="task-container">
    <div class="page-header">
      <h2>任务调度</h2>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="task in taskConfigs" :key="task.id">
        <el-card class="task-card">
          <div class="task-icon" :style="{ background: task.color || '#409EFF' }">
            <el-icon :size="24"><Clock /></el-icon>
          </div>
          <div class="task-info">
            <div class="task-name">{{ task.taskName }}</div>
            <div class="task-desc">{{ task.description }}</div>
            <el-tag :type="task.status === '运行中' ? 'success' : 'info'" size="small">
              {{ task.status }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="log-card">
      <template #header>
        <div class="card-header">
          <span>任务执行日志</span>
          <el-button type="primary" size="small" @click="loadLogs">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="taskLogs" v-loading="loading" height="400">
        <el-table-column prop="taskName" label="任务名称" width="150" />
        <el-table-column prop="executeTime" label="执行时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '成功' ? 'success' : 'danger'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="执行结果" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Clock } from '@element-plus/icons-vue'
import request from '@/api'

// 加载状态
const loading = ref(false)
// 任务配置列表
const taskConfigs = ref([])
// 任务执行日志列表
const taskLogs = ref([])

// 加载任务配置列表
const loadTaskConfigs = async () => {
  try {
    const res = await request.get('/api/scheduler/tasks')
    taskConfigs.value = (res.data.data || []).map(task => ({
      ...task,
      status: task.status === 'RUNNING' ? '运行中' : task.status === 'PAUSED' ? '暂停' : task.status,
      description: task.service ? `${task.service}服务` : ''
    }))
  } catch (e) {
    taskConfigs.value = []
  }
}

// 加载任务执行日志
const loadLogs = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/scheduler/logs')
    taskLogs.value = res.data.data || []
  } catch (e) {
    taskLogs.value = []
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadTaskConfigs()
  loadLogs()
})
</script>

<style scoped>
.task-container {
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

.stats-row {
  margin-bottom: 20px;
}

.task-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.task-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.task-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.task-info {
  flex: 1;
}

.task-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.task-desc {
  font-size: 12px;
  color: #909399;
  margin: 4px 0 6px;
}

.log-card {
  margin-top: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
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
</style>
