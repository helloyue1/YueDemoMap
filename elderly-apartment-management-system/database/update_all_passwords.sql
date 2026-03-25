-- ============================================
-- 更新所有用户密码为 123456 的 BCrypt 哈希
-- ============================================

USE elderly_apartment_management;

-- 123456 的 BCrypt 哈希值
UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH';

-- 验证更新结果
SELECT id, username, real_name, password FROM `user`;
