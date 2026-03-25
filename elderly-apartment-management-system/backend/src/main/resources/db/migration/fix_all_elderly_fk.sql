-- 修复所有引用 elderly 表的外键约束，改为引用 user 表

-- 1. fee_account 表
-- ALTER TABLE fee_account DROP FOREIGN KEY IF EXISTS fk_fee_account_elderly;
ALTER TABLE fee_account 
    ADD CONSTRAINT fk_fee_account_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 2. fee_bill 表
-- ALTER TABLE fee_bill DROP FOREIGN KEY IF EXISTS fk_fee_bill_elderly;
ALTER TABLE fee_bill 
    ADD CONSTRAINT fk_fee_bill_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 3. fee_payment 表
-- ALTER TABLE fee_payment DROP FOREIGN KEY IF EXISTS fk_fee_payment_elderly;
ALTER TABLE fee_payment 
    ADD CONSTRAINT fk_fee_payment_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 4. fee_invoice 表
-- ALTER TABLE fee_invoice DROP FOREIGN KEY IF EXISTS fk_fee_invoice_elderly;
ALTER TABLE fee_invoice 
    ADD CONSTRAINT fk_fee_invoice_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 5. health_data 表
-- ALTER TABLE health_data DROP FOREIGN KEY IF EXISTS fk_data_elderly;
ALTER TABLE health_data 
    ADD CONSTRAINT fk_data_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 6. health_record 表
-- ALTER TABLE health_record DROP FOREIGN KEY IF EXISTS fk_record_elderly;
ALTER TABLE health_record 
    ADD CONSTRAINT fk_record_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 7. family_member 表（如果有）
-- ALTER TABLE family_member DROP FOREIGN KEY IF EXISTS fk_family_elderly;
-- ALTER TABLE family_member 
--     ADD CONSTRAINT fk_family_elderly 
--     FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 8. meal_order 表（如果有）
-- ALTER TABLE meal_order DROP FOREIGN KEY IF EXISTS fk_meal_order_elderly;
-- ALTER TABLE meal_order 
--     ADD CONSTRAINT fk_meal_order_elderly 
--     FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;
