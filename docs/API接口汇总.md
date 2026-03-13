# API 接口汇总

> 基础URL: http://localhost:8888 (通过网关)  
> 直接访问: http://localhost:8092 (auth), 8080 (product), 8081 (customer), etc.

---

## 认证 Auth

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录 |
| POST | /api/auth/register | 注册 |
| GET | /api/auth/me | 当前用户 |

---

## 产品 Product (8080)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/product/page | 分页查询 |
| GET | /api/product/{id} | 详情 |
| POST | /api/product | 创建 |
| PUT | /api/product | 更新 |
| DELETE | /api/product/{id} | 删除 |
| POST | /api/product/publish/{id} | 发布 |
| POST | /api/product/stop/{id} | 停售 |

---

## 条款 Clause (8080)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/clause/product/{productId} | 产品条款 |
| POST | /api/clause | 创建 |
| PUT | /api/clause | 更新 |
| DELETE | /api/clause/{id} | 删除 |

---

## 险种 Coverage (8080)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/coverage/product/{productId} | 产品险种 |
| POST | /api/coverage | 创建 |
| PUT | /api/coverage | 更新 |
| DELETE | /api/coverage/{id} | 删除 |

---

## 客户 Customer (8081)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/customer/page | 分页查询 |
| GET | /api/customer/{id} | 详情 |
| POST | /api/customer | 创建 |
| PUT | /api/customer | 更新 |
| DELETE | /api/customer/{id} | 删除 |
| POST | /api/customer/freeze/{id} | 冻结 |
| POST | /api/customer/unfreeze/{id} | 解冻 |

---

## 投保 Application (8082)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/application/page | 分页查询 |
| GET | /api/application/{id} | 详情 |
| POST | /api/application | 创建 |
| PUT | /api/application/{id}/submit | 提交 |
| PUT | /api/application/{id}/status | 状态更新 |
| DELETE | /api/application/{id} | 删除 |

---

## 核保 Underwriting (8083)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/underwriting/page | 分页查询 |
| GET | /api/underwriting/{id} | 详情 |
| PUT | /api/underwriting/{id}/approve | 人工通过 |
| PUT | /api/underwriting/{id}/reject | 拒绝 |
| PUT | /api/underwriting/{id}/result | 结果(PASSED/REJECTED) |
| DELETE | /api/underwriting/{id} | 删除 |

---

## 保单 Policy (8085)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/policy/page | 分页查询 |
| GET | /api/policy/{id} | 详情 |
| POST | /api/policy/from-application/{id} | 从投保创建 |
| PUT | /api/policy/{id}/status | 状态更新 |
| PUT | /api/policy/{id}/surrender | 退保 |
| DELETE | /api/policy/{id} | 删除 |

---

## 理赔 Claims (8086)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/claims/page | 分页查询 |
| GET | /api/claims/{id} | 详情 |
| POST | /api/claims | 创建(报案) |
| PUT | /api/claims/{id}/approve | 审核 |
| PUT | /api/claims/{id}/pay | 付款 |
| PUT | /api/claims/{id}/reject | 拒绝 |
| DELETE | /api/claims/{id} | 删除 |
| GET | /api/claims/policy/{id} | 保单理赔 |

---

## 财务 Finance (8084)

### 保费 Premium
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/premium/page | 分页查询 |
| POST | /api/premium/confirm/{id} | 确认缴费 |

### 会计 Accounting
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/accounting | 创建记录 |
| GET | /api/accounting/policy/{no} | 保单会计 |

### 统计 Stats
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/stats/summary | 财务汇总 |

---

## 系统管理 System (8092)

### 用户 User
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/page | 分页查询 |
| GET | /api/user/list | 列表 |
| POST | /api/user | 创建 |
| PUT | /api/user/{id} | 更新 |
| DELETE | /api/user/{id} | 删除 |

### 角色 Role
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/role/list | 列表 |
| POST | /api/role | 创建 |
| POST | /api/role/assignPermission | 分配权限 |
| DELETE | /api/role/{id} | 删除 |

### 机构 Org
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/org/tree | 树形 |
| GET | /api/org/list | 列表 |
| POST | /api/org | 创建 |
| PUT | /api/org/{id} | 更新 |
| DELETE | /api/org/{id} | 删除 |

### 权限 Permission
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/permission/tree | 树形 |
| GET | /api/permission/list | 列表 |
| POST | /api/permission | 创建 |
| PUT | /api/permission/{id} | 更新 |
| DELETE | /api/permission/{id} | 删除 |

---

## 消息 Message

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/message/sms | 发送短信 |
| POST | /api/message/email | 发送邮件 |

---

## 报表 Report

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/report/dashboard | 仪表盘 |
| GET | /api/report/policy | 保单报表 |
| GET | /api/report/claim | 理赔报表 |

---

*详细Mock参数见 docs/API接口文档.md*
