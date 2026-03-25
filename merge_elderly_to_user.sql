-- ============================================
-- 合并 elderly 和 elderly_user 表数据到 user 表
-- ============================================

USE elderly_apartment_management;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 开始事务
START TRANSACTION;

-- ============================================
-- 第一步：为没有user_id的elderly记录创建user账号
-- ============================================

-- 插入没有user_id的老人到user表
-- 使用elderly的phone作为username，默认密码123456
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`, `create_time`, `update_time`, `deleted`)
SELECT 
    CONCAT('elderly_', e.id) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH' as password, -- 123456
    e.name as real_name,
    e.phone,
    CONCAT('elderly', e.id, '@example.com') as email,
    1 as status,
    NOW() as create_time,
    NOW() as update_time,
    0 as deleted
FROM `elderly` e
WHERE e.deleted = 0 
  AND (e.user_id IS NULL OR e.user_id = '');

-- ============================================
-- 第二步：更新elderly表的user_id关联
-- ============================================

-- 更新刚创建的user_id到elderly表
UPDATE `elderly` e
JOIN `user` u ON u.username = CONCAT('elderly_', e.id)
SET e.user_id = u.id
WHERE e.deleted = 0 
  AND (e.user_id IS NULL OR e.user_id = '');

-- ============================================
-- 第三步：检查合并结果
-- ============================================

-- 查看user表中所有老人账号
SELECT '=== User表中所有老人账号 ===' as info;
SELECT id, username, real_name, phone FROM `user` 
WHERE username LIKE 'elderly_%' OR username LIKE '%zhangdaye%' OR username LIKE '%linainai%' OR username LIKE '%wangyeye%' OR username LIKE '%zhaonainai%' OR username LIKE '%liudaye%'
ORDER BY id;

-- 查看elderly表的user_id关联情况
SELECT '=== Elderly表user_id关联情况 ===' as info;
SELECT e.id, e.name, e.user_id, u.username, u.real_name 
FROM `elderly` e
LEFT JOIN `user` u ON e.user_id = u.id
WHERE e.deleted = 0;

-- 提交事务
COMMIT;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '合并完成！' as result;
