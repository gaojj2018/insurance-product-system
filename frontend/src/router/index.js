import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import ProductList from '@/views/ProductList.vue'
import ApplicationList from '@/views/ApplicationList.vue'
import UnderwritingList from '@/views/UnderwritingList.vue'
import PolicyList from '@/views/PolicyList.vue'
import ClaimsList from '@/views/ClaimsList.vue'
import CustomerList from '@/views/CustomerList.vue'
import FinanceList from '@/views/FinanceList.vue'
import OrgList from '@/views/OrgList.vue'
import RoleList from '@/views/RoleList.vue'
import UserList from '@/views/UserList.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/product',
    children: [
      {
        path: 'product',
        name: 'ProductList',
        component: ProductList,
        meta: { title: '产品管理', permission: 'product' }
      },
      {
        path: 'application',
        name: 'ApplicationList',
        component: ApplicationList,
        meta: { title: '投保申请', permission: 'application' }
      },
      {
        path: 'underwriting',
        name: 'UnderwritingList',
        component: UnderwritingList,
        meta: { title: '核保管理', permission: 'underwriting' }
      },
      {
        path: 'policy',
        name: 'PolicyList',
        component: PolicyList,
        meta: { title: '保单管理', permission: 'policy' }
      },
      {
        path: 'claims',
        name: 'ClaimsList',
        component: ClaimsList,
        meta: { title: '理赔管理', permission: 'claims' }
      },
      {
        path: 'customer',
        name: 'CustomerList',
        component: CustomerList,
        meta: { title: '客户管理', permission: 'customer' }
      },
      {
        path: 'finance',
        name: 'FinanceList',
        component: FinanceList,
        meta: { title: '财务管理', permission: 'finance' }
      },
      {
        path: 'org',
        name: 'OrgList',
        component: OrgList,
        meta: { title: '机构管理', permission: 'system:org' }
      },
      {
        path: 'role',
        name: 'RoleList',
        component: RoleList,
        meta: { title: '角色管理', permission: 'system:role' }
      },
      {
        path: 'user',
        name: 'UserList',
        component: UserList,
        meta: { title: '用户管理', permission: 'system:user' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 检查用户是否有访问权限
 */
function hasPermission(requiredPermission) {
  if (!requiredPermission) return true
  
  const permissionsStr = localStorage.getItem('permissions')
  if (!permissionsStr) return false
  
  try {
    const permissions = JSON.parse(permissionsStr)
    // 检查是否有完全匹配的权限或者前缀匹配
    return permissions.some(p => 
      p === requiredPermission || 
      p === 'system' || // 系统管理员有所有权限
      p.startsWith(requiredPermission + ':') // 支持通配符权限
    )
  } catch (e) {
    return false
  }
}

router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 保险核心系统` : '保险核心系统'
  
  // 公开页面直接放行
  if (to.meta.public) {
    next()
    return
  }
  
  const token = localStorage.getItem('token')
  
  // 没有token，跳转登录
  if (!token) {
    next('/login')
    return
  }
  
  // 检查权限
  const requiredPermission = to.meta.permission
  if (requiredPermission && !hasPermission(requiredPermission)) {
    next('/product') // 无权限则跳转到默认页面
    return
  }
  
  next()
})

export default router
