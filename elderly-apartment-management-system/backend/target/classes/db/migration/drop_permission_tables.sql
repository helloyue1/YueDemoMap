-- 删除权限管理相关的数据库表
-- 注意：执行此脚本前请确保已备份数据

-- 1. 先删除外键约束（如果存在）
-- ALTER TABLE role_permission DROP FOREIGN KEY IF EXISTS fk_role_permission_role;
-- ALTER TABLE role_permission DROP FOREIGN KEY IF EXISTS fk_role_permission_permission;

-- 2. 删除角色权限关联表
DROP TABLE IF EXISTS role_permission;

-- 3. 删除权限表
DROP TABLE IF EXISTS permission;
