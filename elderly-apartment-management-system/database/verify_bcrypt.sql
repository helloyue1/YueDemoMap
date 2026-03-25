USE elderly_apartment_management;

-- 检查密码哈希的格式
SELECT 
    username,
    password,
    LENGTH(password) as pwd_length,
    SUBSTRING(password, 1, 7) as bcrypt_version
FROM user 
WHERE username IN ('linainai_202', 'admin', 'liudaye_205');
