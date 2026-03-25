-- ============================================
-- 为老人账号分配角色并整合elderly_user信息
-- ============================================

USE elderly_apartment_management;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 第一步：获取老人角色ID
-- ============================================

-- 创建老人角色（如果不存在）
INSERT IGNORE INTO `role` (`name`, `code`, `description`, `create_time`, `update_time`)
VALUES ('老人', 'ELDERLY', '老人/住户角色', NOW(), NOW());

-- 获取老人角色ID
SET @elderly_role_id = (SELECT id FROM `role` WHERE code = 'ELDERLY' LIMIT 1);

-- ============================================
-- 第二步：为所有老人账号分配老人角色
-- ============================================

-- 为user_id在elderly表中的用户分配老人角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`)
SELECT DISTINCT e.user_id, @elderly_role_id
FROM `elderly` e
WHERE e.user_id IS NOT NULL 
  AND e.deleted = 0;

-- ============================================
-- 第三步：查看合并后的结果
-- ============================================

SELECT '=== 所有老人账号及其角色 ===' as info;
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.phone,
    r.name as role_name
FROM `user` u
JOIN `user_role` ur ON u.id = ur.user_id
JOIN `role` r ON ur.role_id = r.id
WHERE u.id IN (SELECT user_id FROM `elderly` WHERE deleted = 0)
ORDER BY u.id;

-- ============================================
-- 第四步：查看elderly_user表是否需要删除
-- ============================================

SELECT '=== elderly_user表数据（可以删除） ===' as info;
SELECT * FROM `elderly_user`;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '老人账号角色分配完成！' as result;
