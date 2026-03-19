@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 保险系统 - 初始化Admin用户
echo ========================================
echo.

set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.3\bin\mysql.exe"

:: 检查MySQL是否可用
"%MYSQL_HOME%" --version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到MySQL，请检查安装
    pause
    exit /b 1
)

:: 创建BCrypt密码 (123456的BCrypt hash)
:: 使用Java生成
set "TEMP_SCRIPT=%TEMP%\bcrypt_gen.java"

echo import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; > "%TEMP_SCRIPT%"
echo public class bcrypt_gen { >> "%TEMP_SCRIPT%"
echo     public static void main(String[] args) { >> "%TEMP_SCRIPT%"
echo         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); >> "%TEMP_SCRIPT%"
echo         String hash = encoder.encode("123456"); >> "%TEMP_SCRIPT%"
echo         System.out.println(hash); >> "%TEMP_SCRIPT%"
echo     } >> "%TEMP_SCRIPT%"
echo } >> "%TEMP_SCRIPT%"

:: 查找spring-security-crypto jar
set "CRYPTO_JAR="
for /r "C:\Users\jianjun\.m2\repository" %%f in (*spring-security-crypto*.jar) do (
    set "CRYPTO_JAR=%%f"
    goto :found_jar
)

:found_jar
if defined CRYPTO_JAR (
    java -cp "%CRYPTO_JAR%" bcrypt_gen 2>nul
) else (
    echo Using default BCrypt hash...
    echo $2a$10$/wRPuWGF0nbJWgBo1pvBnumbkMQT/E5rdGPaSQHF.d4Afh5TdkocW
)

echo.
echo 正在初始化数据库...

:: 1. 确保数据库存在
"%MYSQL_HOME%" -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS insurance_auth;" 2>nul

:: 2. 创建表结构
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
CREATE TABLE IF NOT EXISTS sys_org (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_code VARCHAR(50) NOT NULL UNIQUE,
    org_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    parent_path VARCHAR(500) DEFAULT '/0',
    level VARCHAR(20) DEFAULT '1',
    sort_order INT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100),
    mobile VARCHAR(20),
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'USER',
    org_id BIGINT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100),
    role_type VARCHAR(20) DEFAULT 'USER',
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100),
    permission_type VARCHAR(20),
    parent_id BIGINT DEFAULT 0,
    resource_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
"

:: 3. 初始化机构数据
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
INSERT IGNORE INTO sys_org (id, org_code, org_name, parent_id, parent_path, level, sort_order, leader, phone, status) VALUES
(1, 'ORG001', '总公司', 0, '/0', '1', 1, '系统管理员', '400-888-8888', 'ACTIVE'),
(2, 'ORG002', '北京分公司', 1, '/0/1', '2', 1, '北京负责人', '010-88888888', 'ACTIVE'),
(3, 'ORG003', '上海分公司', 1, '/0/1', '2', 2, '上海负责人', '021-88888888', 'ACTIVE');
"

:: 4. 初始化角色数据
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
INSERT IGNORE INTO sys_role (id, role_code, role_name, role_type, description, status) VALUES
(1, 'ADMIN', '系统管理员', 'SYSTEM', '系统管理员角色', 'ACTIVE'),
(2, 'USER', '普通用户', 'USER', '普通用户角色', 'ACTIVE'),
(3, 'AGENT', '代理人', 'BUSINESS', '保险代理人角色', 'ACTIVE'),
(4, 'UNDERWRITER', '核保员', 'BUSINESS', '核保员角色', 'ACTIVE'),
(5, 'CLAIMS_OFFICER', '理赔员', 'BUSINESS', '理赔员角色', 'ACTIVE');
"

:: 5. 初始化权限数据
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
INSERT IGNORE INTO sys_permission (id, permission_code, permission_name, permission_type, parent_id, resource_url, sort_order) VALUES
(1, 'system', '系统管理', 'MENU', 0, '/system', 10),
(2, 'system:org', '机构管理', 'MENU', 1, '/org', 1),
(3, 'system:org:create', '机构新增', 'BUTTON', 2, '', 1),
(4, 'system:org:edit', '机构编辑', 'BUTTON', 2, '', 2),
(5, 'system:org:delete', '机构删除', 'BUTTON', 2, '', 3),
(6, 'system:role', '角色管理', 'MENU', 1, '/role', 2),
(7, 'system:role:create', '角色新增', 'BUTTON', 6, '', 1),
(8, 'system:role:edit', '角色编辑', 'BUTTON', 6, '', 2),
(9, 'system:role:delete', '角色删除', 'BUTTON', 6, '', 3),
(10, 'system:user', '用户管理', 'MENU', 1, '/user', 3),
(11, 'system:user:create', '用户新增', 'BUTTON', 10, '', 1),
(12, 'system:user:edit', '用户编辑', 'BUTTON', 10, '', 2),
(13, 'system:user:delete', '用户删除', 'BUTTON', 10, '', 3),
(14, 'product', '产品管理', 'MENU', 0, '/product', 20),
(15, 'product:view', '产品查看', 'BUTTON', 14, '', 1),
(16, 'product:create', '产品创建', 'BUTTON', 14, '', 2),
(17, 'product:edit', '产品编辑', 'BUTTON', 14, '', 3),
(18, 'product:delete', '产品删除', 'BUTTON', 14, '', 4),
(19, 'product:publish', '产品发布', 'BUTTON', 14, '', 5),
(20, 'application', '投保管理', 'MENU', 0, '/application', 30),
(21, 'application:view', '投保查看', 'BUTTON', 20, '', 1),
(22, 'application:create', '投保申请', 'BUTTON', 20, '', 2),
(23, 'application:cancel', '投保撤单', 'BUTTON', 20, '', 3),
(24, 'underwriting', '核保管理', 'MENU', 0, '/underwriting', 40),
(25, 'underwriting:view', '核保查看', 'BUTTON', 24, '', 1),
(26, 'underwriting:approve', '核保通过', 'BUTTON', 24, '', 2),
(27, 'underwriting:reject', '核保拒绝', 'BUTTON', 24, '', 3),
(28, 'policy', '保单管理', 'MENU', 0, '/policy', 50),
(29, 'policy:view', '保单查看', 'BUTTON', 28, '', 1),
(30, 'claims', '理赔管理', 'MENU', 0, '/claims', 60),
(31, 'claims:view', '理赔查看', 'BUTTON', 30, '', 1),
(32, 'claims:create', '理赔申请', 'BUTTON', 30, '', 2),
(33, 'claims:process', '理赔处理', 'BUTTON', 30, '', 3),
(34, 'claims:pay', '理赔支付', 'BUTTON', 30, '', 4),
(35, 'customer', '客户管理', 'MENU', 0, '/customer', 70),
(36, 'customer:view', '客户查看', 'BUTTON', 35, '', 1),
(37, 'customer:create', '客户新增', 'BUTTON', 35, '', 2),
(38, 'customer:edit', '客户编辑', 'BUTTON', 35, '', 3),
(39, 'customer:freeze', '客户冻结', 'BUTTON', 35, '', 4),
(40, 'finance', '财务管理', 'MENU', 0, '/finance', 80),
(41, 'finance:view', '财务查看', 'BUTTON', 40, '', 1);
"

:: 6. 初始化Admin用户 (密码: 123456)
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
DELETE FROM sys_user WHERE username = 'admin';
INSERT INTO sys_user (id, username, password, real_name, mobile, email, role, org_id, status) VALUES
(1, 'admin', '\$2a\$10\$/wRPuWGF0nbJWgBo1pvBnumbkMQT/E5rdGPaSQHF.d4Afh5TdkocW', '系统管理员', '13800138000', 'admin@insurance.com', 'ADMIN', 1, 'ACTIVE');
"

:: 7. 给Admin用户分配角色
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
DELETE FROM sys_user_role WHERE user_id = 1;
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
"

:: 8. 给ADMIN角色赋予所有权限
"%MYSQL_HOME%" -u root -p123456 insurance_auth -e "
DELETE FROM sys_role_permission WHERE role_id = 1;
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;
"

echo.
echo ========================================
echo 初始化完成!
echo ========================================
echo.
echo 登录账号: admin
echo 登录密码: 123456
echo.
echo 权限列表:
echo   - system        (系统管理)
echo   - product       (产品管理)
echo   - application   (投保管理)
echo   - underwriting  (核保管理)
echo   - policy        (保单管理)
echo   - claims        (理赔管理)
echo   - customer      (客户管理)
echo   - finance       (财务管理)
echo ========================================

pause
