-- 认证服务数据库初始化脚本
CREATE DATABASE IF NOT EXISTS insurance_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE insurance_auth;

-- 机构表
DROP TABLE IF EXISTS sys_org;
CREATE TABLE sys_org (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_code VARCHAR(50) NOT NULL UNIQUE COMMENT '机构代码',
    org_name VARCHAR(100) NOT NULL COMMENT '机构名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父机构ID',
    parent_path VARCHAR(500) DEFAULT '/0' COMMENT '父路径',
    level VARCHAR(20) DEFAULT '1' COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '电话',
    address VARCHAR(255) COMMENT '地址',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-启用, INACTIVE-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_parent_id (parent_id),
    INDEX idx_org_code (org_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统机构表';

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) COMMENT '真实姓名',
    mobile VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: ADMIN-管理员, USER-普通用户, AGENT-代理人',
    org_id BIGINT COMMENT '所属机构ID',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    role_name VARCHAR(100) COMMENT '角色名称',
    role_type VARCHAR(20) DEFAULT 'USER' COMMENT '角色类型',
    description VARCHAR(255) COMMENT '描述',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    permission_name VARCHAR(100) COMMENT '权限名称',
    permission_type VARCHAR(20) COMMENT '权限类型: MENU-菜单, BUTTON-按钮, API-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    resource_url VARCHAR(255) COMMENT '资源URL',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_perm_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化机构数据
INSERT INTO sys_org (org_code, org_name, parent_id, parent_path, level, sort_order, leader, phone, status) VALUES
('ORG001', '总公司', 0, '/0', '1', 1, '系统管理员', '400-888-8888', 'ACTIVE'),
('ORG002', '北京分公司', 1, '/0/1', '2', 1, '北京负责人', '010-88888888', 'ACTIVE'),
('ORG003', '上海分公司', 1, '/0/1', '2', 2, '上海负责人', '021-88888888', 'ACTIVE');

-- 初始化用户数据 (密码均为 123456，使用BCrypt加密)
-- BCrypt hash for '123456': $2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW
INSERT INTO sys_user (username, password, real_name, mobile, email, role, org_id, status) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '系统管理员', '13800138000', 'admin@insurance.com', 'ADMIN', 1, 'ACTIVE'),
('agent001', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '代理人张三', '13800138001', 'agent001@insurance.com', 'AGENT', 2, 'ACTIVE');

INSERT INTO sys_role (role_code, role_name, role_type, description) VALUES
('ADMIN', '系统管理员', 'SYSTEM', '系统管理员角色'),
('USER', '普通用户', 'USER', '普通用户角色'),
('AGENT', '代理人', 'BUSINESS', '保险代理人角色'),
('UNDERWRITER', '核保员', 'BUSINESS', '核保员角色'),
('CLAIMS_OFFICER', '理赔员', 'BUSINESS', '理赔员角色');

INSERT INTO sys_permission (permission_code, permission_name, permission_type, parent_id, resource_url, sort_order) VALUES
-- 系统管理
('system', '系统管理', 'MENU', 0, '/system', 10),
('system:org', '机构管理', 'MENU', 1, '/org', 1),
('system:org:create', '机构新增', 'BUTTON', 2, '', 1),
('system:org:edit', '机构编辑', 'BUTTON', 2, '', 2),
('system:org:delete', '机构删除', 'BUTTON', 2, '', 3),
('system:role', '角色管理', 'MENU', 1, '/role', 2),
('system:role:create', '角色新增', 'BUTTON', 6, '', 1),
('system:role:edit', '角色编辑', 'BUTTON', 6, '', 2),
('system:role:delete', '角色删除', 'BUTTON', 6, '', 3),
('system:user', '用户管理', 'MENU', 1, '/user', 3),
('system:user:create', '用户新增', 'BUTTON', 10, '', 1),
('system:user:edit', '用户编辑', 'BUTTON', 10, '', 2),
('system:user:delete', '用户删除', 'BUTTON', 10, '', 3),
-- 业务管理
('product', '产品管理', 'MENU', 0, '/product', 2),
('product:view', '产品查看', 'BUTTON', 14, '', 1),
('product:create', '产品创建', 'BUTTON', 14, '', 2),
('product:edit', '产品编辑', 'BUTTON', 14, '', 3),
('product:delete', '产品删除', 'BUTTON', 14, '', 4),
('product:publish', '产品发布', 'BUTTON', 14, '', 5),
('application', '投保管理', 'MENU', 0, '/application', 3),
('application:view', '投保查看', 'BUTTON', 20, '', 1),
('application:create', '投保申请', 'BUTTON', 20, '', 2),
('application:cancel', '投保撤单', 'BUTTON', 20, '', 3),
('underwriting', '核保管理', 'MENU', 0, '/underwriting', 4),
('underwriting:view', '核保查看', 'BUTTON', 24, '', 1),
('underwriting:approve', '核保通过', 'BUTTON', 24, '', 2),
('underwriting:reject', '核保拒绝', 'BUTTON', 24, '', 3),
('policy', '保单管理', 'MENU', 0, '/policy', 5),
('policy:view', '保单查看', 'BUTTON', 28, '', 1),
('claims', '理赔管理', 'MENU', 0, '/claims', 6),
('claims:view', '理赔查看', 'BUTTON', 30, '', 1),
('claims:create', '理赔申请', 'BUTTON', 30, '', 2),
('claims:process', '理赔处理', 'BUTTON', 30, '', 3),
('claims:pay', '理赔支付', 'BUTTON', 30, '', 4),
('customer', '客户管理', 'MENU', 0, '/customer', 7),
('customer:view', '客户查看', 'BUTTON', 35, '', 1),
('customer:create', '客户新增', 'BUTTON', 35, '', 2),
('customer:edit', '客户编辑', 'BUTTON', 35, '', 3),
('customer:freeze', '客户冻结', 'BUTTON', 35, '', 4),
('finance', '财务管理', 'MENU', 0, '/finance', 8),
('finance:view', '财务查看', 'BUTTON', 40, '', 1);

-- 给ADMIN角色赋予所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 给用户分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
