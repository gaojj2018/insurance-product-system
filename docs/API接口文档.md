# 保险核心系统 API 文档

> 版本: v1.0  
> 日期: 2026-03-12

---

## 一、认证接口

### 1.1 登录

**请求**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**响应**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员"
  }
}
```

---

## 二、产品管理

### 2.1 产品分页查询

**请求**
```http
POST /api/product/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "productCode": "PROD-001",
  "productName": "平安福",
  "productType": "LIFE",
  "status": "ACTIVE"
}
```

**响应**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "productId": 1,
        "productCode": "PROD-001",
        "productName": "平安福终身寿险",
        "productType": "LIFE",
        "coveragePeriod": "终身",
        "paymentPeriod": "10年",
        "minCoverage": 100000,
        "maxCoverage": 5000000,
        "premium": null,
        "status": "ACTIVE",
        "createTime": "2026-01-01 10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2.2 创建产品

**请求**
```http
POST /api/product
Content-Type: application/json

{
  "productCode": "PROD-NEW-001",
  "productName": "新产品",
  "productType": "LIFE",
  "coveragePeriod": "10年",
  "paymentPeriod": "5年",
  "minCoverage": 100000,
  "maxCoverage": 1000000,
  "description": "这是一款新产品"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "productId": 14,
    "productCode": "PROD-NEW-001"
  }
}
```

### 2.3 更新产品

**请求**
```http
PUT /api/product
Content-Type: application/json

{
  "productId": 14,
  "productName": "新产品-已更新",
  "description": "更新后的描述"
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 2.4 删除产品

**请求**
```http
DELETE /api/product/14
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 2.5 发布产品

**请求**
```http
POST /api/product/publish/14
```

**响应**
```json
{
  "code": 200,
  "message": "发布成功"
}
```

---

## 三、客户管理

### 3.1 客户分页查询

**请求**
```http
POST /api/customer/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "customerName": "张",
  "customerType": "PERSONAL"
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "customerId": 1,
        "customerNo": "C20260101001",
        "customerName": "张三",
        "customerType": "PERSONAL",
        "idType": "ID_CARD",
        "idNo": "110101199001011234",
        "phone": "13900000001",
        "email": "zhangsan@example.com",
        "address": "北京市朝阳区",
        "status": "ACTIVE"
      }
    ],
    "total": 1
  }
}
```

### 3.2 创建客户

**请求**
```http
POST /api/customer
Content-Type: application/json

{
  "customerName": "李四",
  "customerType": "PERSONAL",
  "idType": "ID_CARD",
  "idNo": "110101199002022345",
  "phone": "13900000002",
  "email": "lisi@example.com",
  "address": "上海市浦东新区"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "customerId": 15
  }
}
```

### 3.3 冻结客户

**请求**
```http
POST /api/customer/freeze/15
```

**响应**
```json
{
  "code": 200,
  "message": "冻结成功"
}
```

### 3.4 解冻客户

**请求**
```http
POST /api/customer/unfreeze/15
```

**响应**
```json
{
  "code": 200,
  "message": "解冻成功"
}
```

---

## 四、投保申请

### 4.1 创建投保

**请求**
```http
POST /api/application
Content-Type: application/json

{
  "productId": 1,
  "applicantId": 1,
  "insuredId": 1,
  "coverageAmount": 100000,
  "paymentMethod": "ANNUAL",
  "coveragePeriod": "10年",
  "paymentPeriod": "10年",
  "remark": "投保备注"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "applicationId": 1,
    "applicationNo": "APP202603121010001"
  }
}
```

### 4.2 提交投保

**请求**
```http
PUT /api/application/1/submit
```

**响应**
```json
{
  "code": 200,
  "message": "提交成功"
}
```

### 4.3 投保分页查询

**请求**
```http
POST /api/application/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "status": "DRAFT"
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "applicationId": 1,
        "applicationNo": "APP202603121010001",
        "productId": 1,
        "productName": "平安福终身寿险",
        "applicantId": 1,
        "applicantName": "张三",
        "insuredId": 1,
        "coverageAmount": 100000,
        "premium": 1000,
        "status": "DRAFT",
        "createTime": "2026-03-12 10:10:00"
      }
    ]
  }
}
```

---

## 五、核保管理

### 5.1 核保分页查询

**请求**
```http
POST /api/underwriting/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "underwriteStatus": "PENDING"
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "applicationId": 1,
        "applicationNo": "APP202603121010001",
        "productName": "平安福终身寿险",
        "applicantName": "张三",
        "coverageAmount": 100000,
        "premium": 1000,
        "underwriteStatus": "PENDING",
        "createTime": "2026-03-12 10:15:00"
      }
    ]
  }
}
```

### 5.2 核保通过

**请求**
```http
PUT /api/underwriting/1/result?result=PASSED&opinion=核保通过
```

**响应**
```json
{
  "code": 200,
  "message": "核保通过，已生成保单",
  "data": {
    "id": 1,
    "underwriteStatus": "PASSED"
  }
}
```

### 5.3 核保拒绝

**请求**
```http
PUT /api/underwriting/1/result?result=REJECTED&opinion=风险过高
```

**响应**
```json
{
  "code": 200,
  "message": "已拒保",
  "data": {
    "id": 1,
    "underwriteStatus": "REJECTED"
  }
}
```

---

## 六、保单管理

### 6.1 保单分页查询

**请求**
```http
POST /api/policy/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "status": "EFFECTIVE"
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "policyId": 1,
        "policyNo": "POL202603001",
        "productId": 1,
        "productName": "平安福终身寿险",
        "insuredId": 1,
        "coverage": 100000,
        "premium": 1000,
        "status": "EFFECTIVE",
        "effectiveDate": "2026-03-12",
        "expirationDate": "2036-03-12"
      }
    ]
  }
}
```

### 6.2 保单退保

**请求**
```http
PUT /api/policy/1/surrender?reason=客户申请退保
```

**响应**
```json
{
  "code": 200,
  "message": "退保成功"
}
```

---

## 七、理赔管理

### 7.1 理赔分页查询

**请求**
```http
POST /api/claims/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "status": "REPORTED"
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "claimsId": 1,
        "claimsNo": "C202603120001",
        "policyId": 1,
        "policyNo": "POL202603001",
        "applicantId": 1,
        "applicantName": "张三",
        "accidentType": "意外医疗",
        "accidentDate": "2026-03-10",
        "claimAmount": 5000,
        "approvedAmount": null,
        "status": "REPORTED",
        "createTime": "2026-03-12 14:00:00"
      }
    ]
  }
}
```

### 7.2 创建理赔

**请求**
```http
POST /api/claims
Content-Type: application/json

{
  "policyId": 1,
  "policyNo": "POL202603001",
  "applicantId": 1,
  "applicantName": "张三",
  "accidentType": "疾病医疗",
  "accidentDate": "2026-03-10",
  "accidentLocation": "北京积水潭医院",
  "accidentDesc": "因病住院治疗",
  "claimAmount": 8000
}
```

**响应**
```json
{
  "code": 200,
  "message": "理赔报案成功",
  "data": {
    "claimsId": 2,
    "claimsNo": "C202603120002"
  }
}
```

### 7.3 理赔审核

**请求**
```http
PUT /api/claims/2/approve?approvedAmount=7000&handler=admin
```

**响应**
```json
{
  "code": 200,
  "message": "审核通过"
}
```

### 7.4 理赔付款

**请求**
```http
PUT /api/claims/2/pay?payAccount=6222021234567890123
```

**响应**
```json
{
  "code": 200,
  "message": "付款成功"
}
```

### 7.5 理赔拒绝

**请求**
```http
PUT /api/claims/2/reject?handler=admin
```

**响应**
```json
{
  "code": 200,
  "message": "已拒绝"
}
```

---

## 八、财务管理

### 8.1 保费分页查询

**请求**
```http
POST /api/premium/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "paymentStatus": "PENDING"
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "policyNo": "POL202603001",
        "applicationNo": "APP202603001",
        "productName": "平安福终身寿险",
        "premium": 1000,
        "paymentStatus": "PENDING",
        "dueDate": "2026-03-15"
      }
    ]
  }
}
```

### 8.2 确认缴费

**请求**
```http
POST /api/premium/confirm/1
```

**响应**
```json
{
  "code": 200,
  "message": "确认成功"
}
```

### 8.3 财务统计

**请求**
```http
GET /api/stats/summary
```

**响应**
```json
{
  "code": 200,
  "data": {
    "todayPremium": 5000,
    "monthPremium": 15000,
    "todayClaim": 0,
    "monthClaim": 5000,
    "balance": 10000
  }
}
```

---

## 九、机构管理

### 9.1 机构树形查询

**请求**
```http
GET /api/org/tree
```

**响应**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "orgCode": "ORG001",
      "orgName": "总公司",
      "parentId": 0,
      "level": "1",
      "leader": "王五",
      "phone": "010-12345678",
      "status": "ACTIVE",
      "children": [
        {
          "id": 2,
          "orgCode": "ORG002",
          "orgName": "分公司",
          "parentId": 1,
          "level": "2"
        }
      ]
    }
  ]
}
```

### 9.2 创建机构

**请求**
```http
POST /api/org
Content-Type: application/json

{
  "orgName": "新机构",
  "parentId": 1,
  "level": "2",
  "leader": "李四",
  "phone": "010-87654321",
  "address": "北京市海淀区",
  "status": "ACTIVE"
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "orgCode": "ORG005"
  }
}
```

---

## 十、角色管理

### 10.1 角色列表

**请求**
```http
GET /api/role/list
```

**响应**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "roleCode": "ADMIN",
      "roleName": "系统管理员",
      "roleType": "SYSTEM",
      "description": "系统管理角色",
      "status": "ACTIVE"
    }
  ]
}
```

### 10.2 创建角色

**请求**
```http
POST /api/role
Content-Type: application/json

{
  "roleCode": "USER",
  "roleName": "普通用户",
  "roleType": "NORMAL",
  "description": "普通用户角色"
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 3
  }
}
```

---

## 十一、权限管理

### 11.1 权限树

**请求**
```http
GET /api/permission/tree
```

**响应**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "permissionCode": "system",
      "permissionName": "系统管理",
      "permissionType": "MENU",
      "children": [
        {
          "id": 2,
          "permissionCode": "system:user",
          "permissionName": "用户管理",
          "permissionType": "MENU"
        }
      ]
    }
  ]
}
```

---

## 十二、产品条款

### 12.1 产品条款查询

**请求**
```http
GET /api/clause/product/1
```

**响应**
```json
{
  "code": 200,
  "data": [
    {
      "clauseId": 1,
      "clauseCode": "TERM-001",
      "clauseName": "基本条款",
      "clauseType": "BASIC",
      "clauseContent": "条款内容...",
      "status": "ACTIVE"
    }
  ]
}
```

### 12.2 创建条款

**请求**
```http
POST /api/clause
Content-Type: application/json

{
  "productId": 1,
  "clauseCode": "TERM-NEW",
  "clauseName": "新条款",
  "clauseType": "BASIC",
  "clauseContent": "条款内容"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功"
}
```

---

## 十三、产品险种

### 13.1 产品险种查询

**请求**
```http
GET /api/coverage/product/1
```

**响应**
```json
{
  "code": 200,
  "data": [
    {
      "coverageId": 1,
      "coverageCode": "COV-001",
      "coverageName": "主险",
      "coverageType": "MAIN",
      "minSumInsured": 100000,
      "maxSumInsured": 5000000,
      "basePremiumRate": 0.005,
      "status": "ACTIVE"
    }
  ]
}
```

### 13.2 创建险种

**请求**
```http
POST /api/coverage
Content-Type: application/json

{
  "productId": 1,
  "coverageCode": "COV-NEW",
  "coverageName": "新险种",
  "coverageType": "MAIN",
  "coverageKind": "MAIN",
  "minSumInsured": 100000,
  "maxSumInsured": 1000000,
  "basePremiumRate": 0.003,
  "calculationType": "FIXED"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功"
}
```

---

## 十四、错误响应

### 14.1 通用错误格式

```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

### 14.2 常见错误码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权(缺少Token) |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

*文档更新时间: 2026-03-12*
