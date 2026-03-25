-- 修复 fee_bill 表的外键约束，改为引用 user 表

-- 查看当前表结构，确认外键状态
-- SHOW CREATE TABLE fee_bill;

-- 添加新的外键约束，引用 user 表
ALTER TABLE fee_bill 
    ADD CONSTRAINT fk_fee_bill_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;
