-- 修复 zhangjianguo 用户密码
USE elderly_apartment_management;
UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH' WHERE `id` = 101;
SELECT id, username, real_name, password FROM `user` WHERE `id` = 101;
