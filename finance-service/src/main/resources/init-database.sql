-- 财务服务数据库初始化脚本
-- 数据库: insurance_finance

CREATE DATABASE IF NOT EXISTS insurance_finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE insurance_finance;

-- 保费记录表
DROP TABLE IF EXISTS premium_record;
CREATE TABLE premium_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    policy_no VARCHAR(50) COMMENT '保单号',
    application_no VARCHAR(50) COMMENT '投保单号',
    product_code VARCHAR(50) COMMENT '产品代码',
    product_name VARCHAR(100) COMMENT '产品名称',
    insured_name VARCHAR(100) COMMENT '被保险人姓名',
    premium DECIMAL(15,2) COMMENT '保费金额',
    payment_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '缴费状态: PENDING-待缴, PAID-已缴, OVERDUE-逾期',
    payment_date DATETIME COMMENT '缴费日期',
    payment_method VARCHAR(20) COMMENT '缴费方式: BANK_TRANSFER-银行转账, WECHAT-微信, ALIPAY-支付宝, CASH-现金',
    payment_batch_no VARCHAR(50) COMMENT '缴费批次号',
    invoice_no VARCHAR(50) COMMENT '发票号',
    invoice_status VARCHAR(20) DEFAULT 'NOT_ISSUED' COMMENT '发票状态: NOT_ISSUED-未开, ISSUED-已开',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_policy_no (policy_no),
    INDEX idx_application_no (application_no),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保费记录表';

-- 佣金记录表
DROP TABLE IF EXISTS commission_record;
CREATE TABLE commission_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    policy_no VARCHAR(50) COMMENT '保单号',
    application_no VARCHAR(50) COMMENT '投保单号',
    product_code VARCHAR(50) COMMENT '产品代码',
    product_name VARCHAR(100) COMMENT '产品名称',
    agent_id VARCHAR(50) COMMENT '代理人ID',
    agent_name VARCHAR(100) COMMENT '代理人姓名',
    agent_level VARCHAR(20) COMMENT '代理人等级',
    premium DECIMAL(15,2) COMMENT '保费金额',
    commission_rate DECIMAL(5,2) COMMENT '佣金比例(%)',
    commission_amount DECIMAL(15,2) COMMENT '佣金金额',
    commission_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '佣金状态: PENDING-待发放, PAID-已发放, FROZEN-冻结',
    commission_date DATETIME COMMENT '佣金发放日期',
    payment_batch_no VARCHAR(50) COMMENT '发放批次号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_policy_no (policy_no),
    INDEX idx_agent_id (agent_id),
    INDEX idx_commission_status (commission_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';

-- 财务核算记录表
DROP TABLE IF EXISTS accounting_record;
CREATE TABLE accounting_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    policy_no VARCHAR(50) COMMENT '保单号',
    application_no VARCHAR(50) COMMENT '投保单号',
    product_code VARCHAR(50) COMMENT '产品代码',
    account_type VARCHAR(20) COMMENT '账户类型: PREMIUM-保费, COMMISSION-佣金, CLAIMS-理赔',
    account_subject VARCHAR(50) COMMENT '会计科目',
    debit_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '借方金额',
    credit_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '贷方金额',
    transaction_type VARCHAR(30) COMMENT '交易类型: PREMIUM_COLLECT-保费收取, COMMISSION_PAY-佣金发放, CLAIMS_PAY-理赔支付',
    transaction_no VARCHAR(50) COMMENT '交易流水号',
    transaction_date DATETIME COMMENT '交易日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_policy_no (policy_no),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_date (transaction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务核算记录表';

-- 插入测试数据
INSERT INTO premium_record (policy_no, application_no, product_code, product_name, insured_name, premium, payment_status) VALUES
('POL20260001', 'APP20260001', 'P001', '终身寿险', '张三', 5000.00, 'PENDING'),
('POL20260002', 'APP20260002', 'P002', '医疗保险', '李四', 1200.00, 'PAID');

INSERT INTO commission_record (policy_no, application_no, product_code, product_name, agent_id, agent_name, agent_level, premium, commission_rate, commission_status) VALUES
('POL20260001', 'APP20260001', 'P001', '终身寿险', 'A001', '王五', 'SENIOR', 5000.00, 30.00, 'PENDING'),
('POL20260002', 'APP20260002', 'P002', '医疗保险', 'A002', '赵六', 'JUNIOR', 1200.00, 20.00, 'PAID');

INSERT INTO accounting_record (policy_no, application_no, product_code, account_type, account_subject, debit_amount, credit_amount, transaction_type, transaction_date) VALUES
('POL20260001', 'APP20260001', 'P001', 'PREMIUM', '保费收入', 5000.00, 0.00, 'PREMIUM_COLLECT', NOW()),
('POL20260001', 'APP20260001', 'P001', 'COMMISSION', '佣金支出', 0.00, 1500.00, 'COMMISSION_PAY', NOW());
