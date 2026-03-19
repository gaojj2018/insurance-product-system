-- 保险系统数据库初始化脚本
-- 执行前请创建数据库: CREATE DATABASE insurance_db;

USE insurance_db;

-- 审计日志表
CREATE TABLE IF NOT EXISTS `sys_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '模块',
  `method` VARCHAR(200) DEFAULT NULL COMMENT '方法',
  `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
  `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
  `request_params` TEXT COMMENT '请求参数',
  `response_result` TEXT COMMENT '响应结果',
  `response_status` INT DEFAULT NULL COMMENT '响应状态',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  `execution_time` BIGINT DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_module` (`module`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 消息记录表 (如果不存在)
CREATE TABLE IF NOT EXISTS `message_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `message_type` VARCHAR(20) DEFAULT NULL COMMENT '消息类型: SMS/EMAIL',
  `recipient` VARCHAR(100) DEFAULT NULL COMMENT '接收者',
  `subject` VARCHAR(200) DEFAULT NULL COMMENT '邮件主题',
  `content` TEXT COMMENT '消息内容',
  `status` VARCHAR(20) DEFAULT NULL COMMENT '状态: PENDING/SENT/FAILED',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_recipient` (`recipient`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';

-- 佣金记录表 (如果不存在)
CREATE TABLE IF NOT EXISTS `commission_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `policy_no` VARCHAR(50) DEFAULT NULL COMMENT '保单号',
  `application_no` VARCHAR(50) DEFAULT NULL COMMENT '投保单号',
  `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品代码',
  `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
  `agent_id` VARCHAR(50) DEFAULT NULL COMMENT '代理人ID',
  `agent_name` VARCHAR(50) DEFAULT NULL COMMENT '代理人姓名',
  `agent_level` VARCHAR(20) DEFAULT NULL COMMENT '代理人级别',
  `premium` DECIMAL(15,2) DEFAULT NULL COMMENT '保费',
  `commission_rate` DECIMAL(5,4) DEFAULT NULL COMMENT '佣金比例',
  `commission_amount` DECIMAL(15,2) DEFAULT NULL COMMENT '佣金金额',
  `commission_status` VARCHAR(20) DEFAULT NULL COMMENT '佣金状态',
  `commission_date` DATETIME DEFAULT NULL COMMENT '佣金计算时间',
  `payment_batch_no` VARCHAR(50) DEFAULT NULL COMMENT '支付批次号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_policy_no` (`policy_no`),
  KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金记录表';

SELECT '数据库初始化完成!' AS result;
