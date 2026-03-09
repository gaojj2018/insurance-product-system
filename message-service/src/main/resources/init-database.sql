CREATE DATABASE IF NOT EXISTS insurance_message DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE insurance_message;

DROP TABLE IF EXISTS message_record;
CREATE TABLE message_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型: SMS-短信, EMAIL-邮件',
    recipient VARCHAR(100) NOT NULL COMMENT '接收人',
    subject VARCHAR(200) COMMENT '主题(邮件)',
    content TEXT COMMENT '内容',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待发送, SENT-已发送, FAILED-失败',
    send_time DATETIME COMMENT '发送时间',
    error_msg VARCHAR(500) COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_recipient (recipient),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';
