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
