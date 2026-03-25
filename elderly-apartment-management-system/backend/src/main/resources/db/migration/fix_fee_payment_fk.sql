-- 修复 fee_payment 表的外键约束，改为引用 user 表

-- 查看当前表结构，确认外键状态
-- SHOW CREATE TABLE fee_payment;

-- 删除旧的外键约束（如果存在）
-- ALTER TABLE fee_payment DROP FOREIGN KEY fk_fee_payment_elderly;

-- 添加新的外键约束，引用 user 表
ALTER TABLE fee_payment 
    ADD CONSTRAINT fk_fee_payment_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;
