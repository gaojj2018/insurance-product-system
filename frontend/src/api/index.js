import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器 - 自动添加Token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一错误处理
request.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // Token过期或无效，清除本地存储并跳转登录
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          window.location.href = '/login'
          break
        case 403:
          console.error('没有权限访问该资源')
          break
        default:
          console.error('请求错误:', error.response.data)
      }
    }
    return Promise.reject(error)
  }
)

// 产品API
export const getProductList = (data) => {
  return request.post('/product/page', data)
}

export const getProductById = (id) => {
  return request.get(`/product/${id}`)
}

export const createProduct = (data) => {
  return request.post('/product', data)
}

export const updateProduct = (data) => {
  return request.put('/product', data)
}

export const deleteProduct = (id) => {
  return request.delete(`/product/${id}`)
}

export const publishProduct = (id) => {
  return request.post(`/product/publish/${id}`)
}

export const stopProduct = (id) => {
  return request.post(`/product/stop/${id}`)
}

// 险种API
export const getCoverageList = (params) => {
  return request.get('/coverage/page', { params })
}

export const getCoverageByProductId = (productId) => {
  return request.get(`/coverage/product/${productId}`)
}

export const createCoverage = (data) => {
  return request.post('/coverage', data)
}

export const updateCoverage = (data) => {
  return request.put('/coverage', data)
}

export const deleteCoverage = (id) => {
  return request.delete(`/coverage/${id}`)
}

// 条款API
export const getClauseList = (params) => {
  return request.get('/clause/page', { params })
}

export const getClauseByProductId = (productId) => {
  return request.get(`/clause/product/${productId}`)
}

export const createClause = (data) => {
  return request.post('/clause', data)
}

export const updateClause = (data) => {
  return request.put('/clause', data)
}

export const deleteClause = (id) => {
  return request.delete(`/clause/${id}`)
}

export const getCustomerList = (params) => {
  return request.get('/customer/page', { params })
}

export const getCustomerById = (id) => {
  return request.get(`/customer/${id}`)
}

export const createCustomer = (data) => {
  return request.post('/customer', data)
}

export const updateCustomer = (data) => {
  return request.put(`/customer/${data.customerId}`, data)
}

export const deleteCustomer = (id) => {
  return request.delete(`/customer/${id}`)
}

export const freezeCustomer = (id) => {
  return request.post(`/customer/freeze/${id}`)
}

export const unfreezeCustomer = (id) => {
  return request.post(`/customer/unfreeze/${id}`)
}

export const getPremiumList = (data) => {
  return request.post('/finance/premium/page', data)
}

export const confirmPremium = (id) => {
  return request.post(`/finance/premium/confirm/${id}`)
}

export const refundPremium = (id) => {
  return request.post(`/finance/premium/refund/${id}`)
}

export const getClaimPaymentList = (data) => {
  return request.post('/finance/claim-payment/page', data)
}

export const payClaim = (id) => {
  return request.post(`/finance/claim-payment/pay/${id}`)
}

export const getFinanceStats = () => {
  return request.get('/finance/stats')
}

export const getFinanceReport = (params) => {
  return request.get('/finance/report', { params })
}

// 机构管理API
export const getOrgList = (params) => {
  return request.get('/org/list', { params })
}

export const getOrgTree = () => {
  return request.get('/org/tree')
}

export const getOrgPage = (params) => {
  return request.get('/org/page', { params })
}

export const getOrgById = (id) => {
  return request.get(`/org/${id}`)
}

export const createOrg = (data) => {
  return request.post('/org', data)
}

export const updateOrg = (data) => {
  return request.put(`/org/${data.id}`, data)
}

export const deleteOrg = (id) => {
  return request.delete(`/org/${id}`)
}

// 角色管理API
export const getRoleList = () => {
  return request.get('/role/list')
}

export const getRolePage = (params) => {
  return request.get('/role/page', { params })
}

export const getRoleById = (id) => {
  return request.get(`/role/${id}`)
}

export const createRole = (data) => {
  return request.post('/role', data)
}

export const updateRole = (data) => {
  return request.put(`/role/${data.id}`, data)
}

export const deleteRole = (id) => {
  return request.delete(`/role/${id}`)
}

// 权限管理API
export const getPermissionList = () => {
  return request.get('/permission/list')
}

export const getPermissionTree = () => {
  return request.get('/permission/tree')
}

export const getPermissionById = (id) => {
  return request.get(`/permission/${id}`)
}

export const createPermission = (data) => {
  return request.post('/permission', data)
}

export const updatePermission = (data) => {
  return request.put(`/permission/${data.id}`, data)
}

export const deletePermission = (id) => {
  return request.delete(`/permission/${id}`)
}

// 用户管理API
export const getUserList = (data) => {
  return request.post('/user/page', data)
}

export const getUserById = (id) => {
  return request.get(`/user/${id}`)
}

export const createUser = (data) => {
  return request.post('/user', data)
}

export const updateUser = (data) => {
  return request.put(`/user/${data.id}`, data)
}

export const deleteUser = (id) => {
  return request.delete(`/user/${id}`)
}

export const assignRole = (data) => {
  return request.post('/user/assignRole', data)
}

// 文件上传API
export const uploadFile = (file, type = 'general') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', type)
  return request.post('/customer/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000 // 文件上传超时时间设为60秒
  })
}

// 文件下载URL
export const getFileDownloadUrl = (type, datePath, filename) => {
  return `/api/customer/file/download/${type}/${datePath}/${filename}`
}

export const getRolePermissions = (roleId) => {
  return request.get(`/role/${roleId}/permissions`)
}

export const assignRolePermission = (data) => {
  return request.post('/role/assignPermission', data)
}

export default request
