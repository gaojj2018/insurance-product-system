-- 核保服务数据库初始化脚本
CREATE DATABASE IF NOT EXISTS insurance_underwriting DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE insurance_underwriting;

-- 核保记录表
CREATE TABLE IF NOT EXISTS `underwriting` (
    `underwriting_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '核保ID',
    `application_id` BIGINT NOT NULL COMMENT '投保单ID',
    `application_no` VARCHAR(32) NOT NULL COMMENT '投保单号',
    `result` VARCHAR(20) NOT NULL COMMENT '核保结果: PASS-通过, DECLINE-拒保, DEFERRED-延期, MANUAL-需人工核保',
    `type` VARCHAR(20) DEFAULT 'AUTO' COMMENT '核保类型: AUTO-自动核保, MANUAL-人工核保',
    `level` VARCHAR(5) COMMENT '核保等级: A/B/C/D',
    `risk_score` INT COMMENT '风险评分',
    `opinion` VARCHAR(500) COMMENT '核保意见',
    `underwriter` VARCHAR(50) COMMENT '核保人',
    `underwriting_time` DATETIME COMMENT '核保时间',
    `effective_time` DATETIME COMMENT '生效时间',
    `expiration_time` DATETIME COMMENT '失效时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`underwriting_id`),
    KEY `idx_application_id` (`application_id`),
    KEY `idx_application_no` (`application_no`),
    KEY `idx_result` (`result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核保记录表';

-- 核保规则表
CREATE TABLE IF NOT EXISTS `underwriting_rule` (
    `rule_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则代码',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_type` VARCHAR(20) NOT NULL COMMENT '规则类型: AGE-年龄, OCCUPATION-职业, HEALTH-健康, INCOME-收入',
    `condition_expression` VARCHAR(500) NOT NULL COMMENT '条件表达式',
    `result_action` VARCHAR(20) NOT NULL COMMENT '结果动作: PASS, DECLINE, DEFERRED, MANUAL',
    `priority` INT DEFAULT 100 COMMENT '优先级',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-启用, INACTIVE-禁用',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`rule_id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核保规则表';

-- 插入测试规则
INSERT INTO `underwriting_rule` (`rule_code`, `rule_name`, `rule_type`, `condition_expression`, `result_action`, `priority`) VALUES
('AGE_001', '年龄18-65岁', 'AGE', 'age >= 18 AND age <= 65', 'PASS', 100),
('AGE_002', '年龄<18岁', 'AGE', 'age < 18', 'DECLINE', 90),
('AGE_003', '年龄>65岁', 'AGE', 'age > 65', 'DEFERRED', 90),
('HEALTH_001', '无重大疾病', 'HEALTH', 'has_serious_disease = false', 'PASS', 80),
('HEALTH_002', '有重大疾病', 'HEALTH', 'has_serious_disease = true', 'DECLINE', 70),
('OCCUPATION_001', '1-3类职业', 'OCCUPATION', 'occupation_category IN (1,2,3)', 'PASS', 90),
('OCCUPATION_002', '5-6类职业', 'OCCUPATION', 'occupation_category IN (5,6)', 'DECLINE', 80);
