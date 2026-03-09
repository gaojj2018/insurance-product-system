-- 添加 org_id 字段到 sys_user 表
ALTER TABLE insurance_auth.sys_user ADD COLUMN org_id BIGINT DEFAULT NULL AFTER role;

-- 更新现有用户的 org_id
UPDATE insurance_auth.sys_user SET org_id = 1 WHERE username = 'admin';
