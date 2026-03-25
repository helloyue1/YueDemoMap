-- 修复所有引用 elderly 表的外键约束，改为引用 user 表
-- 错误 121 表示外键名称冲突，使用新的外键名称

-- 1. fee_account 表
ALTER TABLE fee_account 
    ADD CONSTRAINT fk_fee_account_user
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 2. fee_bill 表
ALTER TABLE fee_bill 
    ADD CONSTRAINT fk_fee_bill_user
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 3. fee_payment 表
ALTER TABLE fee_payment 
    ADD CONSTRAINT fk_fee_payment_user
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 4. fee_invoice 表
ALTER TABLE fee_invoice 
    ADD CONSTRAINT fk_fee_invoice_user
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 5. health_data 表
ALTER TABLE health_data 
    ADD CONSTRAINT fk_health_data_user
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 6. health_record 表
ALTER TABLE health_record 
    ADD CONSTRAINT fk_health_record_user
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;
