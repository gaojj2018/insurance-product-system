-- 投保服务数据库初始化脚本
CREATE DATABASE IF NOT EXISTS insurance_application DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE insurance_application;

-- 投保申请表
CREATE TABLE IF NOT EXISTS `application` (
    `application_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投保单ID',
    `application_no` VARCHAR(32) NOT NULL COMMENT '投保单号',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `product_name` VARCHAR(100) COMMENT '产品名称',
    `product_code` VARCHAR(32) COMMENT '产品代码',
    `applicant_id` BIGINT COMMENT '投保人ID',
    `insured_id` BIGINT COMMENT '被保人ID',
    `beneficiary_id` BIGINT COMMENT '受益人ID',
    `coverage` DECIMAL(15,2) COMMENT '保额',
    `premium` DECIMAL(15,2) COMMENT '保费',
    `coverage_period` VARCHAR(50) COMMENT '保障期限',
    `payment_period` VARCHAR(50) COMMENT '缴费期限',
    `payment_method` VARCHAR(20) COMMENT '缴费方式',
    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, SUBMITTED-已提交, UNDERWRITING-核保中, APPROVED-核保通过, REJECTED-核保拒绝, POLICY_ISSUED-已出单',
    `underwriting_id` BIGINT COMMENT '核保ID',
    `policy_id` BIGINT COMMENT '保单ID',
    `remark` VARCHAR(500) COMMENT '备注',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`application_id`),
    UNIQUE KEY `uk_application_no` (`application_no`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投保申请表';

-- 投保人/被保人信息表
CREATE TABLE IF NOT EXISTS `applicant` (
    `applicant_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请人ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `id_type` VARCHAR(20) COMMENT '证件类型: ID_CARD-身份证, PASSPORT-护照, DRIVING_LICENSE-驾驶证',
    `id_no` VARCHAR(50) COMMENT '证件号码',
    `gender` VARCHAR(10) COMMENT '性别: M-男, F-女',
    `birth_date` DATETIME COMMENT '出生日期',
    `nationality` VARCHAR(20) DEFAULT '中国' COMMENT '国籍',
    `province` VARCHAR(50) COMMENT '省份',
    `city` VARCHAR(50) COMMENT '城市',
    `district` VARCHAR(50) COMMENT '区县',
    `address` VARCHAR(200) COMMENT '详细地址',
    `mobile` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `occupation` VARCHAR(100) COMMENT '职业',
    `occupation_category` VARCHAR(50) COMMENT '职业类别',
    `annual_income` DECIMAL(15,2) COMMENT '年收入',
    `relation_to_insured` VARCHAR(20) COMMENT '与被保人关系',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`applicant_id`),
    KEY `idx_id_no` (`id_no`),
    KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投保人/被保人信息表';

-- 受益人信息表
CREATE TABLE IF NOT EXISTS `beneficiary` (
    `beneficiary_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '受益人ID',
    `application_id` BIGINT NOT NULL COMMENT '投保单ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `id_type` VARCHAR(20) COMMENT '证件类型',
    `id_no` VARCHAR(50) COMMENT '证件号码',
    `relation_to_insured` VARCHAR(20) COMMENT '与被保人关系',
    `benefit_ratio` DECIMAL(5,2) COMMENT '受益比例',
    `benefit_order` INT COMMENT '受益顺序',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`beneficiary_id`),
    KEY `idx_application_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='受益人信息表';

-- 健康告知表
CREATE TABLE IF NOT EXISTS `health_declaration` (
    `declaration_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '告知ID',
    `application_id` BIGINT NOT NULL COMMENT '投保单ID',
    `question_id` VARCHAR(50) NOT NULL COMMENT '问题ID',
    `question_content` VARCHAR(500) NOT NULL COMMENT '问题内容',
    `answer` VARCHAR(10) NOT NULL COMMENT '答案: Y-是, N-否',
    `detail` VARCHAR(500) COMMENT '详细说明',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`declaration_id`),
    KEY `idx_application_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康告知表';

-- 插入测试数据
INSERT INTO `applicant` (`name`, `id_type`, `id_no`, `gender`, `birth_date`, `mobile`, `occupation`, `annual_income`) VALUES
('张三', 'ID_CARD', '110101199001011234', 'M', '1990-01-01', '13800138000', '软件工程师', 300000.00),
('李四', 'ID_CARD', '110101199205051234', 'F', '1992-05-05', '13900139000', '教师', 200000.00);

INSERT INTO `application` (`application_no`, `product_id`, `product_name`, `product_code`, `applicant_id`, `coverage`, `premium`, `coverage_period`, `payment_period`, `payment_method`, `status`) VALUES
('APP20260303100001', 1, '终身寿险', 'LIFE-001', 1, 500000.00, 5000.00, '终身', '10年', '年缴', 'SUBMITTED'),
('APP20260303100002', 2, '百万医疗险', 'HEALTH-001', 2, 3000000.00, 1500.00, '1年', '1年', '年缴', 'DRAFT');
