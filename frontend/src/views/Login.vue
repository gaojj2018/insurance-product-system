<!--
 * 登录页面
 * 功能: 用户身份验证，提供用户名密码登录功能
 * API: POST /auth/login
 -->
<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>保险核心系统</h2>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名" 
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            :loading="loading" 
            style="width: 100%"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/api'

// 路由实例，用于登录成功后跳转
const router = useRouter()
// 表单引用，用于表单验证
const loginFormRef = ref(null)
// 加载状态，控制登录按钮的loading效果
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 处理登录操作
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  // 验证表单是否有效
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用登录API
        const response = await request.post('/auth/login', {
          username: loginForm.username,
          password: loginForm.password
        })
        
        // 登录成功，保存用户信息到localStorage
        if (response.data.success) {
          localStorage.setItem('token', response.data.token)
          localStorage.setItem('username', response.data.username)
          localStorage.setItem('role', response.data.role)
          localStorage.setItem('userId', response.data.userId)
          localStorage.setItem('permissions', JSON.stringify(response.data.permissions || []))
          ElMessage.success('登录成功')
          router.push('/product')
        } else {
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请检查用户名密码')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #333;
}
</style>
