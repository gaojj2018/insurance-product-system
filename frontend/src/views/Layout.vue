<!--
 * 系统布局页面
 * 功能: 提供系统整体布局结构，包含侧边栏导航和顶部Header
 * 包含菜单: 产品管理、投保申请、核保管理、保单管理、理赔管理、客户管理、财务管理、机构管理、角色管理、数据统计、任务监控、任务配置、产品对比
 -->
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
        <el-menu-item index="/report">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/task">
          <el-icon><Monitor /></el-icon>
          <span>任务监控</span>
        </el-menu-item>
        <el-menu-item index="/task/config">
          <el-icon><Setting /></el-icon>
          <span>任务配置</span>
        </el-menu-item>
        <el-menu-item index="/product/compare">
          <el-icon><Switch /></el-icon>
          <span>产品对比</span>
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
  Shop, Document, Check, Tickets, Money, User, Wallet, OfficeBuilding, UserFilled, DArrowLeft, DArrowRight,
  DataAnalysis, Clock, Switch, Monitor, Setting
} from '@element-plus/icons-vue'

// 路由实例
const router = useRouter()
// 当前路由实例，用于获取当前激活的菜单项
const route = useRoute()
// 当前用户名，从localStorage获取
const username = ref(localStorage.getItem('username') || 'Admin')
// 侧边栏折叠状态
const isCollapse = ref(false)

// 计算当前激活的菜单项，基于当前路由路径
const activeMenu = computed(() => route.path)

// 处理退出登录操作
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除本地存储的用户信息
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    // 跳转到登录页
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
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
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
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo h3 {
  color: #fff;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-menu {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
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

/* Sub-menu styling */
.sidebar-menu .el-sub-menu__title {
  padding-left: 16px !important;
}

.sidebar-menu .el-sub-menu__title:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  color: #fff !important;
}

.sidebar-menu .el-sub-menu .el-menu-item {
  padding-left: 40px !important;
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: 6px;
  font-size: 13px;
}

.sidebar-menu .el-sub-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

.sidebar-menu .el-sub-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #fff !important;
}

.sidebar-menu .el-sub-menu__icon-arrow {
  color: rgba(255, 255, 255, 0.6);
  right: 12px;
}

/* Expanded sub-menu background */
.sidebar-menu .el-sub-menu.is-opened .el-sub-menu__title {
  background: rgba(255, 255, 255, 0.05) !important;
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
