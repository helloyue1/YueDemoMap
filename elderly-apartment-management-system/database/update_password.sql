-- 更新管理员密码为 password 的 BCrypt 哈希
USE elderly_apartment_management;

UPDATE `user` SET `password` = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE `username` = 'admin';

-- 验证更新
SELECT id, username, password, real_name FROM `user` WHERE `username` = 'admin';
