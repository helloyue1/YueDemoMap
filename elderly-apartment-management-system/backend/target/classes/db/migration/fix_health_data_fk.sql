-- 修复 health_data 表的外键约束，改为引用 user 表

-- 方法1：如果外键已经不存在，直接添加新外键（可能会报错如果外键已存在）
-- 方法2：先删除所有外键约束，再添加新的

-- 对于 health_data 表
-- 先尝试删除可能存在的旧外键（忽略错误）
-- 然后添加新的外键

-- 步骤1：查看当前表结构，确认外键状态
-- SHOW CREATE TABLE health_data;
-- SHOW CREATE TABLE health_record;

-- 步骤2：如果外键已经不存在，直接执行以下语句添加新外键
-- 如果报错说外键已存在，则需要先删除

-- 为 health_data 添加外键（如果外键不存在）
ALTER TABLE health_data 
    ADD CONSTRAINT fk_data_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;

-- 为 health_record 添加外键（如果外键不存在）
ALTER TABLE health_record 
    ADD CONSTRAINT fk_record_elderly 
    FOREIGN KEY (elderly_id) REFERENCES user(id) ON DELETE CASCADE;
