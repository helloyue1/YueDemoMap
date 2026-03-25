-- 服务分类表
CREATE TABLE IF NOT EXISTS `service_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

-- 服务项表
CREATE TABLE IF NOT EXISTS `service_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_id` BIGINT NOT NULL COMMENT '所属分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '服务编码',
    `description` TEXT DEFAULT NULL COMMENT '服务描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `unit` VARCHAR(20) DEFAULT '次' COMMENT '计价单位',
    `duration` INT DEFAULT NULL COMMENT '服务时长（分钟）',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    CONSTRAINT `fk_service_item_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项表';

-- 插入默认服务分类数据
INSERT INTO `service_category` (`name`, `code`, `icon`, `description`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
('生活护理', 'life_care', 'HomeFilled', '日常生活照料服务', 1, 1, NOW(), NOW()),
('医疗护理', 'medical_care', 'FirstAidKit', '专业医疗护理服务', 2, 1, NOW(), NOW()),
('康复训练', 'rehab_training', 'UserFilled', '康复训练指导服务', 3, 1, NOW(), NOW()),
('陪诊服务', 'escort_service', 'Guide', '就医陪同服务', 4, 1, NOW(), NOW());

-- 插入生活护理服务项
INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '助浴服务', 'bath_service', '专业助浴，安全舒适', 80.00, '次', 60, 1, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'life_care';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '理发美容', 'grooming_service', '上门理发、修面、美甲服务', 50.00, '次', 45, 2, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'life_care';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '衣物清洗', 'laundry_service', '衣物收集、清洗、送回服务', 30.00, '次', 30, 3, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'life_care';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '房间清洁', 'cleaning_service', '房间日常清洁整理', 40.00, '次', 60, 4, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'life_care';

-- 插入医疗护理服务项
INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '伤口换药', 'wound_dressing', '专业伤口处理与换药', 60.00, '次', 30, 1, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'medical_care';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '血压监测', 'bp_monitoring', '定期血压测量与记录', 20.00, '次', 15, 2, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'medical_care';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '血糖检测', 'bg_testing', '血糖监测与饮食建议', 25.00, '次', 15, 3, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'medical_care';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '导尿护理', 'catheter_care', '导尿管护理与更换', 100.00, '次', 45, 4, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'medical_care';

-- 插入康复训练服务项
INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '肢体康复', 'limb_rehab', '肢体功能康复训练', 120.00, '次', 60, 1, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'rehab_training';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '认知训练', 'cognitive_training', '认知功能训练与评估', 100.00, '次', 45, 2, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'rehab_training';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '语言康复', 'speech_therapy', '语言功能康复训练', 150.00, '次', 60, 3, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'rehab_training';

-- 插入陪诊服务服务项
INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '半日陪诊', 'half_day_escort', '4小时全程陪同，含挂号、就诊、取药等', 120.00, '4小时', 240, 1, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'escort_service';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '全日陪诊', 'full_day_escort', '8小时全程陪同，含挂号、就诊、取药、检查等', 200.00, '8小时', 480, 2, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'escort_service';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '代取药品', 'medicine_pickup', '代取代送药品服务', 50.00, '次', 120, 3, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'escort_service';

INSERT INTO `service_item` (`category_id`, `name`, `code`, `description`, `price`, `unit`, `duration`, `sort_order`, `status`, `create_time`, `update_time`) 
SELECT 
    id, '检查陪同', 'exam_escort', '各类检查项目陪同', 80.00, '次', 180, 4, 1, NOW(), NOW()
FROM `service_category` WHERE `code` = 'escort_service';
