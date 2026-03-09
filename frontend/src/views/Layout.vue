<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <h3>保险核心系统</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/product">
          <el-icon><Shop /></el-icon>
          <span>产品管理</span>
        </el-menu-item>
        <el-menu-item index="/application">
          <el-icon><Document /></el-icon>
          <span>投保申请</span>
        </el-menu-item>
        <el-menu-item index="/underwriting">
          <el-icon><Check /></el-icon>
          <span>核保管理</span>
        </el-menu-item>
        <el-menu-item index="/policy">
          <el-icon><Tickets /></el-icon>
          <span>保单管理</span>
        </el-menu-item>
        <el-menu-item index="/claims">
          <el-icon><Money /></el-icon>
          <span>理赔管理</span>
        </el-menu-item>
        <el-menu-item index="/customer">
          <el-icon><User /></el-icon>
          <span>客户管理</span>
        </el-menu-item>
        <el-menu-item index="/finance">
          <el-icon><Wallet /></el-icon>
          <span>财务管理</span>
        </el-menu-item>
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/org">
            <el-icon><OfficeBuilding /></el-icon>
            <span>机构管理</span>
          </el-menu-item>
          <el-menu-item index="/role">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-right">
          <span class="username">{{ username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { 
  Shop, Document, Check, Tickets, Money, User, Wallet, Setting, OfficeBuilding, UserFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || 'Admin')

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    router.push('/login')
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
}

.logo h3 {
  color: #fff;
  margin: 0;
  font-size: 16px;
}

.el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #333;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
