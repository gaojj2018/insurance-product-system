CREATE DATABASE IF NOT EXISTS insurance_customer DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE insurance_customer;

DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_no VARCHAR(32) NOT NULL UNIQUE COMMENT '客户号',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    id_type VARCHAR(10) COMMENT '证件类型: ID_CARD-身份证, PASSPORT-护照',
    id_no VARCHAR(50) COMMENT '证件号码',
    gender CHAR(1) COMMENT '性别: M-男, F-女',
    birth_date DATETIME COMMENT '出生日期',
    mobile VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(500) COMMENT '地址',
    customer_type VARCHAR(20) DEFAULT 'PERSONAL' COMMENT '客户类型: PERSONAL-个人, CORPORATE-企业',
    risk_level VARCHAR(10) DEFAULT 'NORMAL' COMMENT '风险等级: LOW-低, NORMAL-普通, HIGH-高',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_customer_no (customer_no),
    INDEX idx_id_no (id_no),
    INDEX idx_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

DROP TABLE IF EXISTS customer_policy;
CREATE TABLE customer_policy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    policy_no VARCHAR(50) COMMENT '保单号',
    product_code VARCHAR(50) COMMENT '产品代码',
    product_name VARCHAR(100) COMMENT '产品名称',
    policy_status VARCHAR(20) COMMENT '保单状态',
    effective_date VARCHAR(20) COMMENT '生效日期',
    expiry_date VARCHAR(20) COMMENT '到期日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_customer_id (customer_id),
    INDEX idx_policy_no (policy_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户保单关联表';

INSERT INTO customer (customer_no, customer_name, id_type, id_no, gender, mobile, email, customer_type) VALUES
('C20260302001', '张三', 'ID_CARD', '110101199001011234', 'M', '13800138001', 'zhangsan@example.com', 'PERSONAL'),
('C20260302002', '李四', 'ID_CARD', '110101199002021234', 'F', '13800138002', 'lisi@example.com', 'PERSONAL');
