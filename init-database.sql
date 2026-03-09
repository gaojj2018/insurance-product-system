-- 创建数据库
CREATE DATABASE IF NOT EXISTS insurance_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE insurance_product;

-- 产品表
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_code` VARCHAR(50) NOT NULL COMMENT '产品代码',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `product_type` VARCHAR(20) NOT NULL COMMENT '产品类型：LIFE/PROPERTY/ACCIDENT/HEALTH',
  `insurance_type` VARCHAR(50) DEFAULT NULL COMMENT '保险类型',
  `coverage_period` VARCHAR(50) DEFAULT NULL COMMENT '保障期间',
  `payment_period` VARCHAR(50) DEFAULT NULL COMMENT '缴费期间',
  `min_coverage` DECIMAL(18,2) DEFAULT 0 COMMENT '最低保额',
  `max_coverage` DECIMAL(18,2) DEFAULT 0 COMMENT '最高保额',
  `coverage_period_unit` VARCHAR(20) DEFAULT NULL COMMENT '保障期间单位',
  `payment_period_unit` VARCHAR(20) DEFAULT NULL COMMENT '缴费期间单位',
  `waiting_period` INT DEFAULT NULL COMMENT '等待期',
  `waiting_period_unit` VARCHAR(20) DEFAULT NULL COMMENT '等待期单位',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT/ACTIVE/INACTIVE',
  `description` TEXT COMMENT '产品描述',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `uk_product_code` (`product_code`),
  KEY `idx_product_type` (`product_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 险种表
CREATE TABLE IF NOT EXISTS `coverage` (
  `coverage_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '险种ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `coverage_code` VARCHAR(50) NOT NULL COMMENT '险种代码',
  `coverage_name` VARCHAR(200) NOT NULL COMMENT '险种名称',
  `coverage_type` VARCHAR(20) NOT NULL COMMENT '险种类型',
  `coverage_kind` VARCHAR(50) DEFAULT NULL COMMENT '险种分类',
  `min_sum_insured` DECIMAL(18,2) DEFAULT 0 COMMENT '最低保额',
  `max_sum_insured` DECIMAL(18,2) DEFAULT 0 COMMENT '最高保额',
  `base_premium_rate` DECIMAL(10,6) DEFAULT 0 COMMENT '基础费率',
  `calculation_type` VARCHAR(20) DEFAULT NULL COMMENT '计算方式',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态',
  `description` TEXT COMMENT '描述',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`coverage_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='险种表';

-- 条款表
CREATE TABLE IF NOT EXISTS `clause` (
  `clause_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '条款ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `clause_code` VARCHAR(50) NOT NULL COMMENT '条款代码',
  `clause_name` VARCHAR(200) NOT NULL COMMENT '条款名称',
  `clause_type` VARCHAR(20) NOT NULL COMMENT '条款类型',
  `clause_content` TEXT COMMENT '条款内容',
  `clause_order` INT DEFAULT 0 COMMENT '排序',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`clause_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='条款表';

-- 插入测试数据
INSERT INTO `product` (`product_code`, `product_name`, `product_type`, `coverage_period`, `payment_period`, `min_coverage`, `max_coverage`, `status`) VALUES
('LIFE-001', '终身寿险', 'LIFE', '终身', '10年', 100000, 5000000, 'ACTIVE'),
('HEALTH-001', '百万医疗险', 'HEALTH', '1年', '1年', 1000000, 6000000, 'ACTIVE'),
('ACCIDENT-001', '意外伤害保险', 'ACCIDENT', '1年', '1年', 10000, 1000000, 'ACTIVE'),
('PROPERTY-001', '家财险', 'PROPERTY', '1年', '1年', 50000, 5000000, 'ACTIVE');

INSERT INTO `coverage` (`product_id`, `coverage_code`, `coverage_name`, `coverage_type`, `min_sum_insured`, `max_sum_insured`, `base_premium_rate`, `status`) VALUES
(1, 'LIFE-001-MAIN', '主险', 'MAIN', 100000, 5000000, 0.005, 'ACTIVE'),
(2, 'HEALTH-001-MAIN', '主险', 'MAIN', 1000000, 6000000, 0.001, 'ACTIVE');

INSERT INTO `clause` (`product_id`, `clause_code`, `clause_name`, `clause_type`, `clause_content`, `clause_order`, `status`) VALUES
(1, 'LIFE-001-CLAUSE-01', '保险责任', 'RESPONSIBILITY', '在本合同有效期内，本公司承担下列保险责任：\n一、身故保险金\n二、全残保险金', 1, 'ACTIVE'),
(1, 'LIFE-001-CLAUSE-02', '责任免除', 'EXCLUSION', '因下列情形之一导致被保险人身故或全残的，本公司不承担给付保险金的责任：\n一、投保人对被保险人的故意杀害、故意伤害\n二、被保险人故意犯罪或抗拒依法采取的刑事强制措施', 2, 'ACTIVE');

SELECT '数据库初始化完成！' AS result;
