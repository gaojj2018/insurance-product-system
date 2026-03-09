CREATE DATABASE IF NOT EXISTS insurance_claims DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE insurance_claims;

CREATE TABLE IF NOT EXISTS `claims` (
    `claims_id` BIGINT NOT NULL AUTO_INCREMENT,
    `claims_no` VARCHAR(32) NOT NULL,
    `policy_id` BIGINT NOT NULL,
    `policy_no` VARCHAR(32),
    `applicant_id` BIGINT,
    `applicant_name` VARCHAR(50),
    `accident_type` VARCHAR(50),
    `accident_date` DATETIME,
    `accident_location` VARCHAR(200),
    `accident_desc` VARCHAR(500),
    `claim_amount` DECIMAL(15,2),
    `approved_amount` DECIMAL(15,2),
    `status` VARCHAR(20) DEFAULT 'REPORTED',
    `handler` VARCHAR(50),
    `handle_time` DATETIME,
    `pay_account` VARCHAR(50),
    `pay_time` DATETIME,
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`claims_id`),
    UNIQUE KEY `uk_claims_no` (`claims_no`),
    KEY `idx_policy_id` (`policy_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `claims_document` (
    `document_id` BIGINT NOT NULL AUTO_INCREMENT,
    `claims_id` BIGINT NOT NULL,
    `document_type` VARCHAR(50),
    `document_name` VARCHAR(100),
    `document_url` VARCHAR(200),
    `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`document_id`),
    KEY `idx_claims_id` (`claims_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
