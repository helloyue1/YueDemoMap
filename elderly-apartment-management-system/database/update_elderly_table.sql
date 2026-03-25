-- 更新老人信息表，添加缺失的 notes 字段
USE elderly_apartment_management;

-- 添加备注字段
ALTER TABLE `elderly`
    ADD COLUMN `notes` VARCHAR(500) DEFAULT NULL COMMENT '备注';
