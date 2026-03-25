-- 养老公寓管理系统数据库初始化脚本
-- Database: elderly_apartment_management
-- Created: 2026-02-08

-- 创建数据库
CREATE DATABASE IF NOT EXISTS elderly_apartment_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE elderly_apartment_management;

-- 禁用外键检查，以便删除表
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 用户相关表
-- ============================================

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:启用,0:禁用)',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(255) COMMENT '角色描述',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` INT NOT NULL COMMENT '用户ID',
  `role_id` INT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `url` VARCHAR(255) COMMENT '权限URL',
  `method` VARCHAR(10) COMMENT '请求方法',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_id` INT NOT NULL COMMENT '角色ID',
  `permission_id` INT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================
-- 老人管理相关表
-- ============================================

-- 房间表
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
  `type` TINYINT COMMENT '房间类型(1:单人间,2:双人间,3:套房)',
  `capacity` INT COMMENT '房间容量',
  `current_occupancy` INT DEFAULT 0 COMMENT '当前入住人数',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:可用,2:已入住,3:维护中,4:已预订)',
  `price` DECIMAL(10,2) COMMENT '房间价格',
  `has_wifi` TINYINT(1) DEFAULT 0 COMMENT '是否有WiFi(1:有,0:无)',
  `has_tv` TINYINT(1) DEFAULT 0 COMMENT '是否有电视(1:有,0:无)',
  `has_ac` TINYINT(1) DEFAULT 0 COMMENT '是否有空调(1:有,0:无)',
  `has_bathroom` TINYINT(1) DEFAULT 0 COMMENT '是否有独立卫生间(1:有,0:无)',
  `has_balcony` TINYINT(1) DEFAULT 0 COMMENT '是否有阳台(1:有,0:无)',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_number` (`room_number`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 老人信息表
DROP TABLE IF EXISTS `elderly`;
CREATE TABLE `elderly` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '老人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT COMMENT '性别(1:男,2:女)',
  `birthday` DATE COMMENT '出生日期',
  `id_card` VARCHAR(18) COMMENT '身份证号',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `address` VARCHAR(255) COMMENT '家庭住址',
  `health_status` VARCHAR(255) COMMENT '健康状况',
  `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) COMMENT '紧急联系电话',
  `room_id` INT COMMENT '房间ID',
  `check_in_time` DATETIME COMMENT '入住时间',
  `check_out_time` DATETIME COMMENT '退住时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:在住,2:已退住,3:请假)',
  `notes` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_elderly_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人信息表';

-- ============================================
-- 活动管理相关表
-- ============================================

-- 活动表
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `description` TEXT COMMENT '活动描述',
  `activity_type` TINYINT COMMENT '活动类型(1:文化娱乐,2:体育健身,3:知识讲座,4:手工艺术,5:节日庆祝,6:其他)',
  `activity_type_name` VARCHAR(50) COMMENT '活动类型名称',
  `activity_date` DATE COMMENT '活动日期',
  `start_time` TIME COMMENT '开始时间',
  `end_time` TIME COMMENT '结束时间',
  `location` VARCHAR(100) COMMENT '活动地点',
  `max_participants` INT COMMENT '最大参与人数',
  `current_participants` INT DEFAULT 0 COMMENT '当前参与人数',
  `organizer` VARCHAR(50) COMMENT '组织者',
  `organizer_phone` VARCHAR(20) COMMENT '组织者电话',
  `image_url` VARCHAR(255) COMMENT '活动图片URL',
  `requirements` VARCHAR(255) COMMENT '要求/注意事项',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:草稿,1:报名中,2:进行中,3:已完成,4:已取消)',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_activity_date` (`activity_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 活动报名表
DROP TABLE IF EXISTS `activity_signup`;
CREATE TABLE `activity_signup` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` INT NOT NULL COMMENT '活动ID',
  `activity_title` VARCHAR(200) COMMENT '活动标题',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `emergency_contact_name` VARCHAR(50) COMMENT '紧急联系人姓名',
  `emergency_contact_phone` VARCHAR(20) COMMENT '紧急联系人电话',
  `health_status` VARCHAR(255) COMMENT '健康状况',
  `special_requirements` VARCHAR(255) COMMENT '特殊要求',
  `signup_time` DATETIME COMMENT '报名时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:待审核,1:已报名,2:已取消,3:已拒绝,4:已签到)',
  `cancel_reason` VARCHAR(255) COMMENT '取消原因',
  `reject_reason` VARCHAR(255) COMMENT '拒绝原因',
  `checkin_time` DATETIME COMMENT '签到时间',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_signup_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_signup_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- 活动反馈表
DROP TABLE IF EXISTS `activity_feedback`;
CREATE TABLE `activity_feedback` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `activity_id` INT NOT NULL COMMENT '活动ID',
  `activity_title` VARCHAR(200) COMMENT '活动标题',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `satisfaction_score` TINYINT COMMENT '总体满意度(1-5分)',
  `organization_score` TINYINT COMMENT '组织评分(1-5分)',
  `content_score` TINYINT COMMENT '内容评分(1-5分)',
  `overall_score` DECIMAL(3,2) COMMENT '综合评分',
  `feedback_content` TEXT COMMENT '反馈内容',
  `improvement_suggestions` TEXT COMMENT '改进建议',
  `feedback_time` DATETIME COMMENT '反馈时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:待处理,1:已处理)',
  `handler_id` INT COMMENT '处理人ID',
  `handler_name` VARCHAR(50) COMMENT '处理人姓名',
  `handler_reply` TEXT COMMENT '处理人回复',
  `reply_time` DATETIME COMMENT '回复时间',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_feedback_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_feedback_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动反馈表';

-- ============================================
-- 健康管理相关表
-- ============================================

-- 健康档案表
DROP TABLE IF EXISTS `health_archive`;
CREATE TABLE `health_archive` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `medical_history` TEXT COMMENT '病史',
  `chronic_disease` VARCHAR(255) COMMENT '慢性病',
  `allergy_info` VARCHAR(255) COMMENT '过敏信息',
  `medication_history` TEXT COMMENT '用药史',
  `blood_type` VARCHAR(10) COMMENT '血型',
  `height` DECIMAL(5,2) COMMENT '身高(cm)',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `checkup_report` VARCHAR(255) COMMENT '体检报告文件路径',
  `emergency_medicine` VARCHAR(255) COMMENT '急救药品',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_elderly_id` (`elderly_id`),
  CONSTRAINT `fk_archive_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康档案表';

-- 健康记录表
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `health_archive_id` INT NOT NULL COMMENT '健康档案ID',
  `record_type` VARCHAR(50) COMMENT '记录类型',
  `blood_pressure` VARCHAR(20) COMMENT '血压',
  `heart_rate` INT COMMENT '心率',
  `body_temperature` DECIMAL(4,2) COMMENT '体温',
  `blood_sugar` DECIMAL(5,2) COMMENT '血糖',
  `spo2` INT COMMENT '血氧饱和度',
  `weight` DECIMAL(5,2) COMMENT '体重',
  `description` TEXT COMMENT '描述',
  `attachment` VARCHAR(255) COMMENT '附件文件路径',
  `record_time` DATETIME COMMENT '记录时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_health_archive_id` (`health_archive_id`),
  KEY `idx_record_time` (`record_time`),
  CONSTRAINT `fk_record_archive` FOREIGN KEY (`health_archive_id`) REFERENCES `health_archive` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康记录表';

-- 健康数据表（日常健康监测记录）
DROP TABLE IF EXISTS `health_data`;
CREATE TABLE `health_data` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '健康数据ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `record_type` TINYINT DEFAULT 1 COMMENT '记录类型(1:血压测量,2:血糖监测,3:体重测量,4:心率测量,5:体温测量,6:血氧测量,7:综合记录)',
  `systolic_pressure` INT COMMENT '收缩压(高压)mmHg',
  `diastolic_pressure` INT COMMENT '舒张压(低压)mmHg',
  `blood_pressure` VARCHAR(20) COMMENT '血压(格式:收缩压/舒张压)',
  `heart_rate` INT COMMENT '心率(次/分钟)',
  `body_temperature` DECIMAL(4,2) COMMENT '体温(℃)',
  `blood_sugar` DECIMAL(5,2) COMMENT '血糖(mmol/L)',
  `blood_oxygen` INT COMMENT '血氧饱和度(%)',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `record_content` VARCHAR(500) COMMENT '记录内容摘要',
  `health_note` VARCHAR(500) COMMENT '健康备注',
  `recorder_id` INT COMMENT '记录人ID',
  `recorder_name` VARCHAR(50) COMMENT '记录人姓名',
  `record_time` DATETIME COMMENT '记录时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_record_type` (`record_type`),
  KEY `idx_record_time` (`record_time`),
  KEY `idx_recorder_id` (`recorder_id`),
  CONSTRAINT `fk_data_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康数据表';

-- 健康预警表
DROP TABLE IF EXISTS `health_alert`;
CREATE TABLE `health_alert` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `alert_type` TINYINT COMMENT '预警类型(1:血压偏高,2:血压偏低,3:心率偏高,4:心率偏低,5:体温偏高,6:血糖异常)',
  `alert_level` TINYINT COMMENT '预警级别(1:警告,2:警报,3:严重)',
  `alert_title` VARCHAR(100) COMMENT '预警标题',
  `alert_description` TEXT COMMENT '预警描述',
  `abnormal_value` VARCHAR(50) COMMENT '异常值',
  `normal_range` VARCHAR(50) COMMENT '正常范围',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:待处理,2:已确认,3:已解决)',
  `health_data_id` INT COMMENT '关联健康数据ID',
  `handled_by` INT COMMENT '处理人ID',
  `handled_time` DATETIME COMMENT '处理时间',
  `handling_notes` VARCHAR(255) COMMENT '处理备注',
  `alert_time` DATETIME COMMENT '预警时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  KEY `idx_alert_time` (`alert_time`),
  CONSTRAINT `fk_alert_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康预警表';

-- ============================================
-- 药品管理相关表
-- ============================================

-- 药品表
DROP TABLE IF EXISTS `medicine`;
CREATE TABLE `medicine` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `medicine_code` VARCHAR(50) NOT NULL COMMENT '药品编码',
  `medicine_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
  `medicine_type` TINYINT COMMENT '药品类型(1:口服药,2:注射药,3:外用药,4:医疗器械)',
  `medicine_type_name` VARCHAR(50) COMMENT '药品类型名称',
  `specification` VARCHAR(50) COMMENT '规格',
  `unit` VARCHAR(20) COMMENT '单位',
  `manufacturer` VARCHAR(100) COMMENT '生产厂家',
  `stock_quantity` INT DEFAULT 0 COMMENT '当前库存数量',
  `min_stock_level` INT COMMENT '最小库存量',
  `unit_price` DECIMAL(10,2) COMMENT '单价',
  `production_date` DATE COMMENT '生产日期',
  `expiry_date` DATE COMMENT '有效期',
  `storage_location` VARCHAR(50) COMMENT '存储位置',
  `usage_instructions` VARCHAR(255) COMMENT '使用说明',
  `precautions` VARCHAR(255) COMMENT '注意事项',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:正常,1:库存不足,2:即将过期,3:已过期,4:已停用)',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_medicine_code` (`medicine_code`),
  KEY `idx_status` (`status`),
  KEY `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品表';

-- 药品发放记录表
DROP TABLE IF EXISTS `medicine_distribution`;
CREATE TABLE `medicine_distribution` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '发放ID',
  `medicine_id` INT NOT NULL COMMENT '药品ID',
  `medicine_name` VARCHAR(100) COMMENT '药品名称',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `dosage` VARCHAR(50) COMMENT '剂量',
  `frequency` VARCHAR(50) COMMENT '频次',
  `distribution_time` DATETIME COMMENT '发放时间',
  `distributor_id` INT COMMENT '发放人ID',
  `distributor_name` VARCHAR(50) COMMENT '发放人姓名',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:待发放,1:已发放,2:已拒绝,3:已取消)',
  `refuse_reason` VARCHAR(255) COMMENT '拒绝原因',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_medicine_id` (`medicine_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_distribution_time` (`distribution_time`),
  CONSTRAINT `fk_distribution_medicine` FOREIGN KEY (`medicine_id`) REFERENCES `medicine` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_distribution_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品发放记录表';

-- ============================================
-- 护理管理相关表
-- ============================================

-- 护理计划表
DROP TABLE IF EXISTS `care_plan`;
CREATE TABLE `care_plan` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '护理计划ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `plan_type` TINYINT DEFAULT 1 COMMENT '计划类型(1:日常护理,2:康复护理,3:特殊护理,4:医疗护理)',
  `title` VARCHAR(200) NOT NULL COMMENT '计划标题',
  `content` TEXT COMMENT '计划内容',
  `care_items` VARCHAR(500) COMMENT '护理项目清单(JSON格式)',
  `frequency` VARCHAR(50) COMMENT '执行频率',
  `caregiver_id` INT COMMENT '负责护理员ID',
  `caregiver_name` VARCHAR(50) COMMENT '负责护理员姓名',
  `start_date` DATE COMMENT '开始日期',
  `end_date` DATE COMMENT '结束日期',
  `priority` TINYINT DEFAULT 2 COMMENT '优先级(1:低,2:中,3:高)',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:未开始,2:进行中,3:已完成,4:已取消)',
  `create_by` INT COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_caregiver_id` (`caregiver_id`),
  KEY `idx_status` (`status`),
  KEY `idx_plan_type` (`plan_type`),
  CONSTRAINT `fk_care_plan_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理计划表';

-- 护理记录表
DROP TABLE IF EXISTS `care_record`;
CREATE TABLE `care_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '护理记录ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `room_number` VARCHAR(20) COMMENT '房间号',
  `care_plan_id` INT COMMENT '护理计划ID',
  `caregiver_id` INT COMMENT '护理员ID',
  `caregiver_name` VARCHAR(50) COMMENT '护理员姓名',
  `care_type` TINYINT COMMENT '护理类型(1:晨间护理,2:晚间护理,3:饮食护理,4:康复训练,5:用药协助,6:其他)',
  `care_content` TEXT COMMENT '护理内容',
  `care_duration` INT COMMENT '护理时长(分钟)',
  `care_effect` TINYINT COMMENT '护理效果(1:良好,2:一般,3:较差)',
  `elderly_condition` VARCHAR(255) COMMENT '老人状况',
  `abnormal_situation` TEXT COMMENT '异常情况',
  `notes` VARCHAR(500) COMMENT '备注',
  `care_time` DATETIME COMMENT '护理时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:待执行,2:执行中,3:已完成,4:已取消)',
  `image_urls` VARCHAR(500) COMMENT '图片记录',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_care_plan_id` (`care_plan_id`),
  KEY `idx_caregiver_id` (`caregiver_id`),
  KEY `idx_care_time` (`care_time`),
  KEY `idx_status` (`status`),
  KEY `idx_care_type` (`care_type`),
  CONSTRAINT `fk_care_record_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_care_record_plan` FOREIGN KEY (`care_plan_id`) REFERENCES `care_plan` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_care_record_user` FOREIGN KEY (`caregiver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理记录表';

-- 护理评估表
DROP TABLE IF EXISTS `care_assessment`;
CREATE TABLE `care_assessment` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '评估ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `assessment_type` TINYINT COMMENT '评估类型(1:入院评估,2:定期评估,3:专项评估)',
  `assessment_date` DATE COMMENT '评估日期',
  `assessor_id` INT COMMENT '评估人ID',
  `assessor_name` VARCHAR(50) COMMENT '评估人姓名',
  `bathing_score` INT COMMENT '洗澡能力(0-5分)',
  `dressing_score` INT COMMENT '穿衣能力(0-5分)',
  `toileting_score` INT COMMENT '如厕能力(0-5分)',
  `mobility_score` INT COMMENT '移动能力(0-5分)',
  `eating_score` INT COMMENT '进食能力(0-5分)',
  `adl_total_score` INT COMMENT 'ADL总分(0-25分)',
  `care_level` TINYINT COMMENT '护理等级(1:一级护理,2:二级护理,3:三级护理,4:特级护理)',
  `mental_state` VARCHAR(100) COMMENT '精神状态',
  `cognitive_state` VARCHAR(100) COMMENT '认知状态',
  `physical_state` VARCHAR(100) COMMENT '身体状况',
  `assessment_result` TEXT COMMENT '评估结果',
  `care_suggestions` TEXT COMMENT '护理建议',
  `next_assessment_date` DATE COMMENT '下次评估日期',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_assessment_date` (`assessment_date`),
  KEY `idx_assessor_id` (`assessor_id`),
  KEY `idx_care_level` (`care_level`),
  CONSTRAINT `fk_assessment_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理评估表';

-- ============================================
-- 餐饮管理相关表
-- ============================================

-- 餐饮计划表
DROP TABLE IF EXISTS `meal_plan`;
CREATE TABLE `meal_plan` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '餐饮计划ID',
  `name` VARCHAR(100) NOT NULL COMMENT '计划名称',
  `plan_date` DATE NOT NULL COMMENT '计划日期',
  `breakfast` VARCHAR(255) COMMENT '早餐',
  `lunch` VARCHAR(255) COMMENT '午餐',
  `dinner` VARCHAR(255) COMMENT '晚餐',
  `snack` VARCHAR(255) COMMENT '点心',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_date` (`plan_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='餐饮计划表';

-- 老人餐饮计划关联表
DROP TABLE IF EXISTS `elderly_meal_plan`;
CREATE TABLE `elderly_meal_plan` (
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `meal_plan_id` INT NOT NULL COMMENT '餐饮计划ID',
  `special_requirement` VARCHAR(255) COMMENT '特殊要求',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`elderly_id`, `meal_plan_id`),
  KEY `idx_meal_plan_id` (`meal_plan_id`),
  CONSTRAINT `fk_elderly_meal_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_elderly_meal_plan` FOREIGN KEY (`meal_plan_id`) REFERENCES `meal_plan` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人餐饮计划关联表';

-- ============================================
-- 其他管理相关表
-- ============================================

-- 紧急呼叫表
DROP TABLE IF EXISTS `emergency_call`;
CREATE TABLE `emergency_call` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '紧急呼叫ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `call_type` TINYINT COMMENT '呼叫类型(1:医疗,2:火灾,3:跌倒,4:其他)',
  `emergency_level` TINYINT COMMENT '紧急程度(1:低,2:中,3:高,4:严重)',
  `description` TEXT COMMENT '描述',
  `location` VARCHAR(100) COMMENT '位置',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:待处理,2:已派遣,3:处理中,4:已解决,5:已取消)',
  `assigned_caregiver_id` INT COMMENT '分配的护理员ID',
  `assigned_caregiver_name` VARCHAR(50) COMMENT '分配的护理员姓名',
  `response_time` DATETIME COMMENT '响应时间',
  `resolution_time` DATETIME COMMENT '解决时间',
  `resolution_description` TEXT COMMENT '解决描述',
  `image_url` VARCHAR(255) COMMENT '图片URL',
  `call_time` DATETIME COMMENT '呼叫时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  KEY `idx_call_time` (`call_time`),
  CONSTRAINT `fk_emergency_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='紧急呼叫表';

-- 出入记录表
DROP TABLE IF EXISTS `access_record`;
CREATE TABLE `access_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '出入记录ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `user_id` INT COMMENT '记录人ID',
  `access_type` TINYINT COMMENT '出入类型(1:进入,2:离开)',
  `access_location` VARCHAR(100) COMMENT '出入地点',
  `access_time` DATETIME COMMENT '出入时间',
  `description` VARCHAR(255) COMMENT '描述',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_access_time` (`access_time`),
  CONSTRAINT `fk_access_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_access_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出入记录表';

-- 费用记录表
DROP TABLE IF EXISTS `fee_record`;
CREATE TABLE `fee_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '费用记录ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `fee_type` TINYINT COMMENT '费用类型(1:住宿费,2:护理费,3:餐饮费,4:水电费,5:其他)',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `payment_status` TINYINT DEFAULT 1 COMMENT '支付状态(1:未支付,2:已支付,3:部分支付)',
  `due_date` DATE COMMENT '到期日期',
  `payment_date` DATETIME COMMENT '支付日期',
  `payment_method` TINYINT COMMENT '支付方式(1:现金,2:银行转账,3:微信支付,4:支付宝)',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_due_date` (`due_date`),
  CONSTRAINT `fk_fee_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='费用记录表';

-- 护士排班表
DROP TABLE IF EXISTS `nurse_schedule`;
CREATE TABLE `nurse_schedule` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `nurse_id` INT NOT NULL COMMENT '护士ID',
  `nurse_name` VARCHAR(50) COMMENT '护士姓名',
  `elderly_id` INT COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `schedule_date` DATE NOT NULL COMMENT '排班日期',
  `shift_type` TINYINT COMMENT '班次类型(1:早班,2:中班,3:晚班)',
  `shift_name` VARCHAR(20) COMMENT '班次名称',
  `start_time` TIME COMMENT '开始时间',
  `end_time` TIME COMMENT '结束时间',
  `task_content` VARCHAR(255) COMMENT '任务内容',
  `task_status` TINYINT DEFAULT 0 COMMENT '任务状态(0:待执行,1:进行中,2:已完成,3:已取消)',
  `completion_time` DATETIME COMMENT '完成时间',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_nurse_id` (`nurse_id`),
  KEY `idx_schedule_date` (`schedule_date`),
  KEY `idx_elderly_id` (`elderly_id`),
  CONSTRAINT `fk_schedule_nurse` FOREIGN KEY (`nurse_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_schedule_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护士排班表';

-- 任务表
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
  `description` TEXT COMMENT '任务描述',
  `task_type` TINYINT COMMENT '任务类型(1:护理,2:医疗,3:餐饮,4:活动,5:其他)',
  `priority` TINYINT COMMENT '优先级(1:低,2:中,3:高,4:紧急)',
  `elderly_id` INT COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `room_number` VARCHAR(20) COMMENT '房间号',
  `caregiver_id` INT COMMENT '护理员ID',
  `caregiver_name` VARCHAR(50) COMMENT '护理员姓名',
  `scheduled_date` DATE COMMENT '计划日期',
  `scheduled_time` VARCHAR(50) COMMENT '计划时间',
  `estimated_duration` INT COMMENT '预计时长(分钟)',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1:待处理,2:进行中,3:已完成,4:已取消)',
  `actual_start_time` DATETIME COMMENT '实际开始时间',
  `actual_end_time` DATETIME COMMENT '实际结束时间',
  `completion_notes` VARCHAR(255) COMMENT '完成备注',
  `is_recurring` BOOLEAN DEFAULT FALSE COMMENT '是否循环',
  `recurrence_pattern` VARCHAR(50) COMMENT '循环模式(daily,weekly,monthly)',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_caregiver_id` (`caregiver_id`),
  KEY `idx_scheduled_date` (`scheduled_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_task_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_task_caregiver` FOREIGN KEY (`caregiver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入角色数据
INSERT INTO `role` (`name`, `code`, `description`, `create_time`, `update_time`) VALUES
('超级管理员', 'ADMIN', '系统超级管理员，拥有所有权限', NOW(), NOW()),
('管理员', 'MANAGER', '系统管理员，拥有大部分管理权限', NOW(), NOW()),
('医生', 'DOCTOR', '医生角色，负责医疗相关事务', NOW(), NOW()),
('护士', 'NURSE', '护士角色，负责护理工作', NOW(), NOW()),
('护理员', 'CAREGIVER', '护理员角色，负责日常护理', NOW(), NOW()),
('前台', 'RECEPTIONIST', '前台角色，负责接待和基础管理', NOW(), NOW());

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`, `create_time`, `update_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800138000', 'admin@example.com', 1, NOW(), NOW());

-- 为管理员分配角色
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 插入权限数据
INSERT INTO `permission` (`name`, `code`, `url`, `method`, `create_time`, `update_time`) VALUES
('用户管理', 'user:manage', '/api/user/**', '*', NOW(), NOW()),
('角色管理', 'role:manage', '/api/role/**', '*', NOW(), NOW()),
('权限管理', 'permission:manage', '/api/permission/**', '*', NOW(), NOW()),
('老人管理', 'elderly:manage', '/api/elderly/**', '*', NOW(), NOW()),
('房间管理', 'room:manage', '/api/room/**', '*', NOW(), NOW()),
('活动管理', 'activity:manage', '/api/activity/**', '*', NOW(), NOW()),
('健康档案管理', 'health:archive:manage', '/api/health/archive/**', '*', NOW(), NOW()),
('健康记录管理', 'health:record:manage', '/api/health/record/**', '*', NOW(), NOW()),
('健康数据管理', 'health:data:manage', '/api/health/data/**', '*', NOW(), NOW()),
('健康预警管理', 'health:alert:manage', '/api/health/alert/**', '*', NOW(), NOW()),
('药品管理', 'medicine:manage', '/api/medicine/**', '*', NOW(), NOW()),
('药品发放管理', 'medicine:distribution:manage', '/api/medicine/distribution/**', '*', NOW(), NOW()),
('护理计划管理', 'care:plan:manage', '/api/care/plan/**', '*', NOW(), NOW()),
('护理记录管理', 'care:record:manage', '/api/care/record/**', '*', NOW(), NOW()),
('餐饮计划管理', 'meal:plan:manage', '/api/meal/plan/**', '*', NOW(), NOW()),
('紧急呼叫管理', 'emergency:manage', '/api/emergency/**', '*', NOW(), NOW()),
('出入记录管理', 'access:manage', '/api/access/**', '*', NOW(), NOW()),
('费用记录管理', 'fee:manage', '/api/fee/**', '*', NOW(), NOW()),
('护士排班管理', 'nurse:schedule:manage', '/api/nurse/schedule/**', '*', NOW(), NOW()),
('任务管理', 'task:manage', '/api/task/**', '*', NOW(), NOW()),
('仪表盘查看', 'dashboard:view', '/api/dashboard/**', 'GET', NOW(), NOW());

-- 为超级管理员角色分配所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

-- 插入示例房间数据
INSERT INTO `room` (`room_number`, `type`, `capacity`, `current_occupancy`, `status`, `price`, `create_time`, `update_time`) VALUES
('101', 1, 1, 0, 1, 3000.00, NOW(), NOW()),
('102', 1, 1, 0, 1, 3000.00, NOW(), NOW()),
('201', 2, 2, 0, 1, 4500.00, NOW(), NOW()),
('202', 2, 2, 0, 1, 4500.00, NOW(), NOW()),
('301', 3, 3, 0, 1, 6000.00, NOW(), NOW()),
('302', 3, 3, 0, 1, 6000.00, NOW(), NOW());

-- 插入示例药品数据
INSERT INTO `medicine` (`medicine_code`, `medicine_name`, `medicine_type`, `medicine_type_name`, `specification`, `unit`, `manufacturer`, `stock_quantity`, `min_stock_level`, `unit_price`, `production_date`, `expiry_date`, `storage_location`, `usage_instructions`, `precautions`, `status`, `create_time`, `update_time`) VALUES
('MED001', '阿司匹林肠溶片', 1, '口服药', '100mg*30片', '盒', '拜耳医药', 100, 20, 25.00, '2024-01-01', '2026-01-01', 'A区-1-1', '口服，一次1片，一日3次', '孕妇禁用', 0, NOW(), NOW()),
('MED002', '布洛芬缓释胶囊', 1, '口服药', '300mg*20粒', '盒', '芬必得', 80, 15, 30.00, '2024-02-01', '2026-02-01', 'A区-1-2', '口服，一次1粒，一日2次', '饭后服用', 0, NOW(), NOW()),
('MED003', '胰岛素注射液', 2, '注射药', '400IU/10ml', '支', '诺和诺德', 50, 10, 120.00, '2024-03-01', '2025-12-01', 'B区-2-1', '皮下注射，遵医嘱', '冷藏保存', 0, NOW(), NOW());

-- 插入示例餐饮计划数据
INSERT INTO `meal_plan` (`name`, `plan_date`, `breakfast`, `lunch`, `dinner`, `snack`, `create_time`, `update_time`) VALUES
('标准餐饮计划', CURDATE(), '小米粥、鸡蛋、馒头', '红烧肉、青菜、米饭', '鱼汤、蔬菜沙拉、馒头', '苹果、酸奶', NOW(), NOW()),
('清淡餐饮计划', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '白粥、鸡蛋、馒头', '清蒸鱼、青菜、米饭', '蔬菜汤、水果沙拉、馒头', '香蕉、牛奶', NOW(), NOW()),
('糖尿病餐饮计划', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '燕麦粥、鸡蛋、全麦面包', '清蒸鱼、西兰花、糙米饭', '蔬菜汤、鸡胸肉沙拉、全麦面包', '黄瓜、无糖酸奶', NOW(), NOW());

-- 完成
SELECT '数据库初始化完成！' AS message;

-- 重新启用外键检查
-- ============================================
-- 活动管理模块表结构升级
-- ============================================

-- 优化activity表结构
ALTER TABLE `activity`
ADD COLUMN `checked_in_count` INT DEFAULT 0 COMMENT '实际签到人数' AFTER `current_participants`,
ADD COLUMN `min_participants` INT DEFAULT 1 COMMENT '最小成团人数' AFTER `max_participants`,
ADD COLUMN `organizer_id` INT COMMENT '组织者ID' AFTER `organizer_phone`,
ADD COLUMN `image_urls` TEXT COMMENT '活动图片列表(JSON数组)' AFTER `image_url`,
ADD COLUMN `suitable_for` VARCHAR(255) COMMENT '适合人群' AFTER `requirements`,
ADD COLUMN `materials_needed` VARCHAR(255) COMMENT '需要准备的材料' AFTER `suitable_for`,
ADD COLUMN `registration_start_time` DATETIME COMMENT '报名开始时间' AFTER `materials_needed`,
ADD COLUMN `registration_end_time` DATETIME COMMENT '报名截止时间' AFTER `registration_start_time`,
ADD COLUMN `is_recommended` TINYINT(1) DEFAULT 0 COMMENT '是否推荐(1:是,0:否)' AFTER `status`,
ADD COLUMN `is_public` TINYINT(1) DEFAULT 1 COMMENT '是否公开(1:公开,0:内部)' AFTER `is_recommended`,
ADD COLUMN `view_count` INT DEFAULT 0 COMMENT '浏览次数' AFTER `is_public`,
ADD COLUMN `notes` VARCHAR(500) COMMENT '备注' AFTER `view_count`,
ADD COLUMN `create_by` INT COMMENT '创建人ID' AFTER `notes`;

-- 添加activity表索引
ALTER TABLE `activity`
ADD INDEX `idx_activity_type` (`activity_type`),
ADD INDEX `idx_is_recommended` (`is_recommended`),
ADD INDEX `idx_create_by` (`create_by`),
ADD INDEX `idx_activity_date_status` (`activity_date`, `status`),
ADD INDEX `idx_activity_type_status` (`activity_type`, `status`);

-- 优化activity_signup表结构
ALTER TABLE `activity_signup`
ADD COLUMN `elderly_phone` VARCHAR(20) COMMENT '老人联系电话' AFTER `elderly_name`,
ADD COLUMN `room_number` VARCHAR(20) COMMENT '房间号' AFTER `elderly_phone`,
ADD COLUMN `signup_source` TINYINT DEFAULT 1 COMMENT '报名来源(1:本人报名,2:家属代报,3:工作人员代报)' AFTER `emergency_contact_phone`,
ADD COLUMN `checkin_method` TINYINT COMMENT '签到方式(1:扫码签到,2:手动签到,3:系统自动)' AFTER `checkin_time`,
ADD COLUMN `checked_in_by` INT COMMENT '签到操作人ID' AFTER `checkin_method`,
ADD COLUMN `is_waitlist` TINYINT(1) DEFAULT 0 COMMENT '是否候补(1:是,0:否)' AFTER `checked_in_by`,
ADD COLUMN `waitlist_order` INT DEFAULT 0 COMMENT '候补序号' AFTER `is_waitlist`,
ADD COLUMN `create_by` INT COMMENT '创建人ID' AFTER `notes`;

-- 添加activity_signup表索引
ALTER TABLE `activity_signup`
ADD UNIQUE KEY `uk_activity_elderly` (`activity_id`, `elderly_id`),
ADD INDEX `idx_signup_time` (`signup_time`),
ADD INDEX `idx_signup_activity_status` (`activity_id`, `status`),
ADD INDEX `idx_signup_elderly_status` (`elderly_id`, `status`);

-- 优化activity_feedback表结构
ALTER TABLE `activity_feedback`
ADD COLUMN `signup_id` INT COMMENT '关联报名ID' AFTER `activity_title`,
ADD COLUMN `staff_score` TINYINT COMMENT '工作人员评分(1-5分)' AFTER `content_score`,
ADD COLUMN `is_would_recommend` TINYINT(1) COMMENT '是否愿意推荐(1:是,0:否)' AFTER `overall_score`,
ADD COLUMN `favorite_part` VARCHAR(255) COMMENT '最喜欢的环节' AFTER `improvement_suggestions`,
MODIFY COLUMN `status` TINYINT DEFAULT 0 COMMENT '状态(0:待处理,1:已处理,2:已回复)';

-- 添加activity_feedback表索引
ALTER TABLE `activity_feedback`
ADD UNIQUE KEY `uk_activity_elderly_feedback` (`activity_id`, `elderly_id`),
ADD INDEX `idx_satisfaction_score` (`satisfaction_score`),
ADD INDEX `idx_feedback_activity_score` (`activity_id`, `satisfaction_score`);

-- 新增activity_category表
DROP TABLE IF EXISTS `activity_category`;
CREATE TABLE `activity_category` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `code` VARCHAR(50) NOT NULL COMMENT '分类编码',
  `description` VARCHAR(255) COMMENT '分类描述',
  `icon` VARCHAR(100) COMMENT '图标',
  `color` VARCHAR(20) COMMENT '颜色标识',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用(1:是,0:否)',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动分类表';

-- 新增activity_reminder表
DROP TABLE IF EXISTS `activity_reminder`;
CREATE TABLE `activity_reminder` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
  `activity_id` INT NOT NULL COMMENT '活动ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `reminder_type` TINYINT COMMENT '提醒类型(1:活动开始前,2:报名截止前,3:活动变更)',
  `reminder_time` DATETIME COMMENT '提醒时间',
  `reminder_content` VARCHAR(255) COMMENT '提醒内容',
  `send_channel` TINYINT COMMENT '发送渠道(1:APP推送,2:短信,3:电话,4:站内信)',
  `is_sent` TINYINT(1) DEFAULT 0 COMMENT '是否已发送(1:是,0:否)',
  `sent_time` DATETIME COMMENT '发送时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_is_sent` (`is_sent`),
  CONSTRAINT `fk_reminder_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reminder_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动提醒记录表';

-- 新增activity_statistics表
DROP TABLE IF EXISTS `activity_statistics`;
CREATE TABLE `activity_statistics` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `activity_id` INT NOT NULL COMMENT '活动ID',
  `stat_date` DATE COMMENT '统计日期',
  `signup_count` INT DEFAULT 0 COMMENT '报名人数',
  `checkin_count` INT DEFAULT 0 COMMENT '签到人数',
  `cancel_count` INT DEFAULT 0 COMMENT '取消人数',
  `no_show_count` INT DEFAULT 0 COMMENT '未出席人数',
  `avg_satisfaction` DECIMAL(3,2) COMMENT '平均满意度',
  `feedback_count` INT DEFAULT 0 COMMENT '反馈数量',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_date` (`activity_id`, `stat_date`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_stat_date` (`stat_date`),
  CONSTRAINT `fk_stats_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动统计表';

-- 初始化活动分类数据
INSERT INTO `activity_category` (`name`, `code`, `description`, `icon`, `color`, `sort_order`) VALUES
('文化娱乐', 'CULTURE_ENT', '唱歌、跳舞、戏曲、观影等文化娱乐活动', 'Film', '#FF6B6B', 1),
('体育健身', 'SPORTS_FIT', '太极拳、健身操、散步、球类运动等', 'Basketball', '#4ECDC4', 2),
('知识讲座', 'KNOWLEDGE_LEC', '健康讲座、法律讲座、智能设备使用培训等', 'Reading', '#45B7D1', 3),
('手工艺术', 'HANDICRAFT', '书法、绘画、剪纸、编织等手工艺术活动', 'Brush', '#96CEB4', 4),
('节日庆祝', 'FESTIVAL_CEL', '传统节日庆祝、生日会等', 'Calendar', '#FFEAA7', 5),
('社交活动', 'SOCIAL_ACT', '茶话会、座谈会、交友活动等', 'User', '#DDA0DD', 6),
('其他活动', 'OTHER', '其他类型的活动', 'More', '#B0C4DE', 7);

-- 创建触发器自动更新统计数据
DELIMITER //

-- 报名状态变更时更新活动统计
CREATE TRIGGER `trg_after_signup_update`
AFTER UPDATE ON `activity_signup`
FOR EACH ROW
BEGIN
  -- 更新活动当前报名人数
  UPDATE `activity` 
  SET `current_participants` = (
    SELECT COUNT(*) FROM `activity_signup` 
    WHERE `activity_id` = NEW.activity_id AND `status` IN (1, 2) AND `deleted` = 0
  ),
  `checked_in_count` = (
    SELECT COUNT(*) FROM `activity_signup` 
    WHERE `activity_id` = NEW.activity_id AND `status` = 2 AND `deleted` = 0
  )
  WHERE `id` = NEW.activity_id;
END//

-- 新增报名时更新活动统计
CREATE TRIGGER `trg_after_signup_insert`
AFTER INSERT ON `activity_signup`
FOR EACH ROW
BEGIN
  IF NEW.status IN (1, 2) THEN
    UPDATE `activity` 
    SET `current_participants` = `current_participants` + 1
    WHERE `id` = NEW.activity_id;
  END IF;
END//

-- 删除报名时更新活动统计
CREATE TRIGGER `trg_after_signup_delete`
AFTER DELETE ON `activity_signup`
FOR EACH ROW
BEGIN
  IF OLD.status IN (1, 2) THEN
    UPDATE `activity` 
    SET `current_participants` = GREATEST(0, `current_participants` - 1)
    WHERE `id` = OLD.activity_id;
  END IF;
END//

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;
