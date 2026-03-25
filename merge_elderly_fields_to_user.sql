-- ============================================
-- 将elderly表所有字段迁移到user表
-- ============================================

USE elderly_apartment_management;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 开始事务
START TRANSACTION;

-- ============================================
-- 第一步：为user表添加elderly相关字段
-- ============================================

-- 添加老人专属字段到user表
ALTER TABLE `user`
-- 基本信息
ADD COLUMN `gender` tinyint(4) DEFAULT NULL COMMENT '性别(1:男,2:女)',
ADD COLUMN `age` int(11) DEFAULT 0 COMMENT '年龄',
ADD COLUMN `birthday` date DEFAULT NULL COMMENT '出生日期',
ADD COLUMN `nationality` varchar(20) DEFAULT '汉族' COMMENT '民族',
ADD COLUMN `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
ADD COLUMN `address` varchar(255) DEFAULT NULL COMMENT '家庭住址',

-- 健康信息
ADD COLUMN `health_status` varchar(255) DEFAULT NULL COMMENT '健康状况',

-- 紧急联系人
ADD COLUMN `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
ADD COLUMN `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',

-- 房间信息
ADD COLUMN `room_id` int(11) DEFAULT NULL COMMENT '房间ID',
ADD COLUMN `room_number` varchar(20) DEFAULT NULL COMMENT '房间号',

-- 入住信息
ADD COLUMN `check_in_date` date DEFAULT NULL COMMENT '入住日期',
ADD COLUMN `check_in_time` datetime DEFAULT NULL COMMENT '入住时间',
ADD COLUMN `check_out_time` datetime DEFAULT NULL COMMENT '退住时间',

-- 账户信息
ADD COLUMN `balance` decimal(10,2) DEFAULT 0.00 COMMENT '账户余额',
ADD COLUMN `notes` varchar(500) DEFAULT NULL COMMENT '备注',

-- 用户类型标识
ADD COLUMN `user_type` tinyint(4) DEFAULT 1 COMMENT '用户类型(1:员工,2:老人)';

-- ============================================
-- 第二步：将elderly表数据更新到user表
-- ============================================

-- 更新已有老人账号的详细信息
UPDATE `user` u
JOIN `elderly` e ON u.id = e.user_id
SET 
    u.gender = e.gender,
    u.age = e.age,
    u.birthday = e.birthday,
    u.nationality = e.nationality,
    u.id_card = e.id_card,
    u.address = e.address,
    u.health_status = e.health_status,
    u.emergency_contact = e.emergency_contact,
    u.emergency_phone = e.emergency_phone,
    u.room_id = e.room_id,
    u.room_number = e.room_number,
    u.check_in_date = e.check_in_date,
    u.check_in_time = e.check_in_time,
    u.check_out_time = e.check_out_time,
    u.balance = e.balance,
    u.notes = e.notes,
    u.user_type = 2  -- 标记为老人类型
WHERE e.deleted = 0;

-- ============================================
-- 第三步：查看迁移结果
-- ============================================

SELECT '=== User表中老人账号的详细信息 ===' as info;
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.phone,
    u.gender,
    u.age,
    u.birthday,
    u.id_card,
    u.room_number,
    u.balance,
    u.user_type
FROM `user` u
WHERE u.user_type = 2 OR u.id IN (SELECT user_id FROM `elderly` WHERE deleted = 0);

-- 提交事务
COMMIT;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '字段迁移完成！' as result;
