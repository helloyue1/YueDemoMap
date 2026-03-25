-- 更新房间表，添加缺失的设施字段
USE elderly_apartment_management;

-- 添加设施相关字段
ALTER TABLE `room`
    ADD COLUMN `has_wifi` TINYINT(1) DEFAULT 0 COMMENT '是否有WiFi(1:有,0:无)',
    ADD COLUMN `has_tv` TINYINT(1) DEFAULT 0 COMMENT '是否有电视(1:有,0:无)',
    ADD COLUMN `has_ac` TINYINT(1) DEFAULT 0 COMMENT '是否有空调(1:有,0:无)',
    ADD COLUMN `has_bathroom` TINYINT(1) DEFAULT 0 COMMENT '是否有独立卫生间(1:有,0:无)',
    ADD COLUMN `has_balcony` TINYINT(1) DEFAULT 0 COMMENT '是否有阳台(1:有,0:无)',
    ADD COLUMN `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注';
