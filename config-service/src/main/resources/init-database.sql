CREATE DATABASE IF NOT EXISTS insurance_config DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE insurance_config;

DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value VARCHAR(500) COMMENT '配置值',
    config_type VARCHAR(50) COMMENT '配置类型: SYSTEM-系统, BUSINESS-业务',
    description VARCHAR(255) COMMENT '描述',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

INSERT INTO sys_config (config_key, config_value, config_type, description) VALUES
('system.name', '保险核心系统', 'SYSTEM', '系统名称'),
('system.version', '1.0.0', 'SYSTEM', '系统版本'),
('policy.max_amount', '1000000', 'BUSINESS', '最大保额'),
('claim.max_days', '30', 'BUSINESS', '理赔最大天数');
