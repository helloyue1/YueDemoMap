-- 检查所有用户密码
USE elderly_apartment_management;
SELECT id, username, real_name, 
       CASE 
           WHEN password LIKE '$2a$%' THEN 'BCrypt格式'
           ELSE '非BCrypt格式'
       END as password_type,
       password
FROM user;
