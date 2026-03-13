<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '180px'" class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon" v-if="isCollapse">保</div>
          <h3 v-else>保险核心系统</h3>
        </div>
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon v-if="isCollapse"><DArrowRight /></el-icon>
          <el-icon v-else><DArrowLeft /></el-icon>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapse"
        class="sidebar-menu"
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
        <el-menu-item index="/org">
          <el-icon><OfficeBuilding /></el-icon>
          <span>机构管理</span>
        </el-menu-item>
        <el-menu-item index="/role">
          <el-icon><UserFilled /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
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
  Shop, Document, Check, Tickets, Money, User, Wallet, OfficeBuilding, UserFilled, DArrowLeft, DArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || 'Admin')
const isCollapse = ref(false)

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

.sidebar {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: relative;
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 16px;
  background: rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header .collapse-btn {
  cursor: pointer;
  color: rgba(255, 255, 255, 0.7);
  font-size: 18px;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s;
}

.sidebar-header .collapse-btn:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
}

.logo h3 {
  color: #fff;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none !important;
  background: transparent !important;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 180px;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: rgba(255, 255, 255, 0.7) !important;
  height: 50px;
  line-height: 50px;
  margin: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  color: #fff !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #fff !important;
}

.sidebar-menu .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: #fff;
  border-radius: 0 4px 4px 0;
}

.el-menu--collapse .el-menu-item,
.el-menu--collapse .el-sub-menu__title {
  justify-content: center;
  padding: 0 12px !important;
}

.el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #333;
  font-weight: 500;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
