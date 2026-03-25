-- 更新所有用户密码为 123456 的 BCrypt 哈希
-- 使用在线工具生成的正确哈希值
USE elderly_apartment_management;

-- 123456 的正确 BCrypt 哈希 (使用 cost 10)
UPDATE `user` SET `password` = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi';

-- 验证更新结果
SELECT id, username, real_name, password FROM `user`;
