-- 老人信息表字段补充脚本
-- 创建时间: 2026-02-08

-- 添加年龄字段
ALTER TABLE elderly ADD COLUMN age INT DEFAULT 0 COMMENT '年龄' AFTER gender;

-- 添加出生日期字段
ALTER TABLE elderly ADD COLUMN birthday DATE COMMENT '出生日期' AFTER age;

-- 添加家庭住址字段
ALTER TABLE elderly ADD COLUMN address VARCHAR(200) DEFAULT '' COMMENT '家庭住址' AFTER phone;

-- 添加紧急联系人字段
ALTER TABLE elderly ADD COLUMN emergency_contact VARCHAR(50) DEFAULT '' COMMENT '紧急联系人' AFTER address;

-- 添加紧急联系电话字段
ALTER TABLE elderly ADD COLUMN emergency_phone VARCHAR(20) DEFAULT '' COMMENT '紧急联系电话' AFTER emergency_contact;

-- 添加入住日期字段
ALTER TABLE elderly ADD COLUMN check_in_date DATE COMMENT '入住日期' AFTER room_id;

-- 添加民族字段
ALTER TABLE elderly ADD COLUMN nationality VARCHAR(20) DEFAULT '汉族' COMMENT '民族' AFTER birthday;

-- 更新现有数据：根据身份证号计算出生日期和年龄
UPDATE elderly SET birthday = CONCAT(
    SUBSTRING(id_card, 7, 4), '-',
    SUBSTRING(id_card, 11, 2), '-',
    SUBSTRING(id_card, 13, 2)
) WHERE id_card IS NOT NULL AND LENGTH(id_card) = 18;

-- 更新年龄（根据出生日期计算）
UPDATE elderly SET age = TIMESTAMPDIFF(YEAR, birthday, CURDATE()) WHERE birthday IS NOT NULL;

-- 更新现有数据：设置默认入住日期为创建时间
UPDATE elderly SET check_in_date = DATE(create_time) WHERE check_in_date IS NULL;
