USE elderly_apartment_management;
SELECT COUNT(*) as total_users FROM user;
SELECT id, username, real_name, LEFT(password, 30) as password_prefix, status FROM user;
