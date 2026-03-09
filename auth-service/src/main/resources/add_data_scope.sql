ALTER TABLE sys_role ADD COLUMN data_scope VARCHAR(50) DEFAULT 'ALL' COMMENT '数据权限: ALL-全部, DEPT_ONLY-本部门, DEPT_AND_BELOW-本部门及下属, SELF-仅本人';

UPDATE sys_role SET data_scope = 'ALL' WHERE data_scope IS NULL;

ALTER TABLE sys_role ADD COLUMN role_type VARCHAR(50) DEFAULT 'USER' COMMENT '角色类型: SYSTEM-系统, BUSINESS-业务, USER-普通';
