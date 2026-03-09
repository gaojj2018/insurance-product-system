-- 保单服务数据库初始化脚本
CREATE DATABASE IF NOT EXISTS insurance_policy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE insurance_policy;

-- 保单表
CREATE TABLE IF NOT EXISTS `policy` (
    `policy_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '保单ID',
    `policy_no` VARCHAR(32) NOT NULL COMMENT '保单号',
    `application_id` BIGINT NOT NULL COMMENT '投保单ID',
    `application_no` VARCHAR(32) NOT NULL COMMENT '投保单号',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `product_name` VARCHAR(100) COMMENT '产品名称',
    `product_code` VARCHAR(32) COMMENT '产品代码',
    `applicant_id` BIGINT COMMENT '投保人ID',
    `insured_id` BIGINT COMMENT '被保人ID',
    `coverage` DECIMAL(15,2) COMMENT '保额',
    `premium` DECIMAL(15,2) COMMENT '保费',
    `coverage_period` VARCHAR(50) COMMENT '保障期限',
    `payment_period` VARCHAR(50) COMMENT '缴费期限',
    `payment_method` VARCHAR(20) COMMENT '缴费方式',
    `status` VARCHAR(20) DEFAULT 'EFFECTIVE' COMMENT '状态: EFFECTIVE-有效, TERMINATED-终止, EXPIRED-满期, SURRENDERED-退保, LAPSED-失效',
    `issue_date` DATETIME COMMENT '签发日期',
    `effective_date` DATETIME COMMENT '生效日期',
    `expiration_date` DATETIME COMMENT '到期日期',
    `next_payment_date` DATETIME COMMENT '下次缴费日期',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`policy_id`),
    UNIQUE KEY `uk_policy_no` (`policy_no`),
    KEY `idx_application_id` (`application_id`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保单表';

-- 保全记录表
CREATE TABLE IF NOT EXISTS `policy_change` (
    `change_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '变更ID',
    `policy_id` BIGINT NOT NULL COMMENT '保单ID',
    `change_type` VARCHAR(30) NOT NULL COMMENT '变更类型: COVERAGE-保额变更, BENEFICIARY-受益人变更, PERSONAL_INFO-个人信息变更',
    `old_value` TEXT COMMENT '原值',
    `new_value` TEXT COMMENT '新值',
    `change_reason` VARCHAR(200) COMMENT '变更原因',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已批准, REJECTED-已拒绝',
    `apply_time` DATETIME COMMENT '申请时间',
    `approve_time` DATETIME COMMENT '审核时间',
    `operator` VARCHAR(50) COMMENT '操作人',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`change_id`),
    KEY `idx_policy_id` (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保全记录表';
