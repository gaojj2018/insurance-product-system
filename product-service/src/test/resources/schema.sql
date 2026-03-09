-- H2 测试数据库初始化脚本

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50),
    product_name VARCHAR(100),
    product_type VARCHAR(50),
    insurance_type VARCHAR(50),
    coverage_period VARCHAR(50),
    payment_period VARCHAR(50),
    min_coverage DECIMAL(18,2),
    max_coverage DECIMAL(18,2),
    coverage_period_unit VARCHAR(20),
    payment_period_unit VARCHAR(20),
    waiting_period INT,
    waiting_period_unit VARCHAR(20),
    status VARCHAR(20) DEFAULT 'DRAFT',
    description TEXT,
    created_time DATETIME,
    updated_time DATETIME,
    deleted INT DEFAULT 0
);

-- 条款表
CREATE TABLE IF NOT EXISTS clause (
    clause_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    clause_code VARCHAR(50),
    clause_name VARCHAR(100),
    clause_type VARCHAR(50),
    content TEXT,
    created_time DATETIME,
    updated_time DATETIME,
    deleted INT DEFAULT 0
);

-- 保障责任表
CREATE TABLE IF NOT EXISTS coverage (
    coverage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    coverage_name VARCHAR(100),
    coverage_type VARCHAR(50),
    base_premium_rate DECIMAL(18,4),
    min_amount DECIMAL(18,2),
    max_amount DECIMAL(18,2),
    description TEXT,
    created_time DATETIME,
    updated_time DATETIME,
    deleted INT DEFAULT 0
);
