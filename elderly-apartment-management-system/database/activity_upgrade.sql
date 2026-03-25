-- ============================================
-- 活动管理模块数据库升级脚本
-- 优化活动相关表结构，增加完整功能支持
-- ============================================

USE elderly_apartment_management;

-- ============================================
-- 1. 优化活动表结构
-- ============================================
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `description` TEXT COMMENT '活动描述',
  `activity_type` TINYINT DEFAULT 1 COMMENT '活动类型(1:文化娱乐,2:体育健身,3:知识讲座,4:手工艺术,5:节日庆祝,6:社交活动,7:其他)',
  `activity_type_name` VARCHAR(50) COMMENT '活动类型名称',
  `activity_date` DATE COMMENT '活动日期',
  `start_time` TIME COMMENT '开始时间',
  `end_time` TIME COMMENT '结束时间',
  `location` VARCHAR(100) COMMENT '活动地点',
  `max_participants` INT DEFAULT 0 COMMENT '最大参与人数(0表示不限)',
  `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
  `checked_in_count` INT DEFAULT 0 COMMENT '实际签到人数',
  `min_participants` INT DEFAULT 1 COMMENT '最小成团人数',
  `organizer` VARCHAR(50) COMMENT '组织者',
  `organizer_phone` VARCHAR(20) COMMENT '组织者电话',
  `organizer_id` INT COMMENT '组织者ID',
  `image_url` VARCHAR(255) COMMENT '活动封面图片',
  `image_urls` TEXT COMMENT '活动图片列表(JSON数组)',
  `requirements` VARCHAR(255) COMMENT '参与要求/注意事项',
  `suitable_for` VARCHAR(255) COMMENT '适合人群',
  `materials_needed` VARCHAR(255) COMMENT '需要准备的材料',
  `registration_start_time` DATETIME COMMENT '报名开始时间',
  `registration_end_time` DATETIME COMMENT '报名截止时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:草稿,1:报名中,2:即将开始,3:进行中,4:已结束,5:已取消)',
  `is_recommended` TINYINT(1) DEFAULT 0 COMMENT '是否推荐(1:是,0:否)',
  `is_public` TINYINT(1) DEFAULT 1 COMMENT '是否公开(1:公开,0:内部)',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `notes` VARCHAR(500) COMMENT '备注',
  `create_by` INT COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_activity_date` (`activity_date`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_status` (`status`),
  KEY `idx_is_recommended` (`is_recommended`),
  KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- ============================================
-- 2. 优化活动报名表结构
-- ============================================
DROP TABLE IF EXISTS `activity_signup`;
CREATE TABLE `activity_signup` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` INT NOT NULL COMMENT '活动ID',
  `activity_title` VARCHAR(200) COMMENT '活动标题',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `elderly_phone` VARCHAR(20) COMMENT '老人联系电话',
  `room_number` VARCHAR(20) COMMENT '房间号',
  `emergency_contact_name` VARCHAR(50) COMMENT '紧急联系人姓名',
  `emergency_contact_phone` VARCHAR(20) COMMENT '紧急联系人电话',
  `health_status` VARCHAR(255) COMMENT '健康状况说明',
  `special_requirements` VARCHAR(255) COMMENT '特殊需求/注意事项',
  `signup_source` TINYINT DEFAULT 1 COMMENT '报名来源(1:本人报名,2:家属代报,3:工作人员代报)',
  `signup_time` DATETIME COMMENT '报名时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:待审核,1:已报名,2:已签到,3:已取消,4:已拒绝,5:候补中)',
  `cancel_reason` VARCHAR(255) COMMENT '取消原因',
  `reject_reason` VARCHAR(255) COMMENT '拒绝原因',
  `checkin_time` DATETIME COMMENT '签到时间',
  `checkin_method` TINYINT COMMENT '签到方式(1:扫码签到,2:手动签到,3:系统自动)',
  `checked_in_by` INT COMMENT '签到操作人ID',
  `is_waitlist` TINYINT(1) DEFAULT 0 COMMENT '是否候补(1:是,0:否)',
  `waitlist_order` INT DEFAULT 0 COMMENT '候补序号',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_by` INT COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_elderly` (`activity_id`, `elderly_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  KEY `idx_signup_time` (`signup_time`),
  CONSTRAINT `fk_signup_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_signup_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- ============================================
-- 3. 优化活动反馈表结构
-- ============================================
DROP TABLE IF EXISTS `activity_feedback`;
CREATE TABLE `activity_feedback` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `activity_id` INT NOT NULL COMMENT '活动ID',
  `activity_title` VARCHAR(200) COMMENT '活动标题',
  `signup_id` INT COMMENT '关联报名ID',
  `elderly_id` INT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) COMMENT '老人姓名',
  `satisfaction_score` TINYINT COMMENT '总体满意度(1-5分)',
  `organization_score` TINYINT COMMENT '组织评分(1-5分)',
  `content_score` TINYINT COMMENT '内容评分(1-5分)',
  `staff_score` TINYINT COMMENT '工作人员评分(1-5分)',
  `overall_score` DECIMAL(3,2) COMMENT '综合评分',
  `is_would_recommend` TINYINT(1) COMMENT '是否愿意推荐(1:是,0:否)',
  `feedback_content` TEXT COMMENT '反馈内容',
  `improvement_suggestions` TEXT COMMENT '改进建议',
  `favorite_part` VARCHAR(255) COMMENT '最喜欢的环节',
  `feedback_time` DATETIME COMMENT '反馈时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0:待处理,1:已处理,2:已回复)',
  `handler_id` INT COMMENT '处理人ID',
  `handler_name` VARCHAR(50) COMMENT '处理人姓名',
  `handler_reply` TEXT COMMENT '处理人回复',
  `reply_time` DATETIME COMMENT '回复时间',
  `notes` VARCHAR(255) COMMENT '备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_elderly_feedback` (`activity_id`, `elderly_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  KEY `idx_satisfaction_score` (`satisfaction_score`),
  CONSTRAINT `fk_feedback_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_feedback_elderly` FOREIGN KEY (`elderly_id`) REFERENCES `elderly` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动反馈表';

-- ============================================
-- 4. 新增活动分类表
-- ============================================
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

-- ============================================
-- 5. 新增活动提醒记录表
-- ============================================
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

-- ============================================
-- 6. 新增活动统计视图
-- ============================================
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

-- ============================================
-- 7. 初始化活动分类数据
-- ============================================
INSERT INTO `activity_category` (`name`, `code`, `description`, `icon`, `color`, `sort_order`) VALUES
('文化娱乐', 'CULTURE_ENT', '唱歌、跳舞、戏曲、观影等文化娱乐活动', 'Film', '#FF6B6B', 1),
('体育健身', 'SPORTS_FIT', '太极拳、健身操、散步、球类运动等', 'Basketball', '#4ECDC4', 2),
('知识讲座', 'KNOWLEDGE_LEC', '健康讲座、法律讲座、智能设备使用培训等', 'Reading', '#45B7D1', 3),
('手工艺术', 'HANDICRAFT', '书法、绘画、剪纸、编织等手工艺术活动', 'Brush', '#96CEB4', 4),
('节日庆祝', 'FESTIVAL_CEL', '传统节日庆祝、生日会等', 'Calendar', '#FFEAA7', 5),
('社交活动', 'SOCIAL_ACT', '茶话会、座谈会、交友活动等', 'User', '#DDA0DD', 6),
('其他活动', 'OTHER', '其他类型的活动', 'More', '#B0C4DE', 7);

-- ============================================
-- 8. 创建索引优化查询性能
-- ============================================
-- 活动表复合索引
CREATE INDEX `idx_activity_date_status` ON `activity` (`activity_date`, `status`);
CREATE INDEX `idx_activity_type_status` ON `activity` (`activity_type`, `status`);

-- 报名表复合索引
CREATE INDEX `idx_signup_activity_status` ON `activity_signup` (`activity_id`, `status`);
CREATE INDEX `idx_signup_elderly_status` ON `activity_signup` (`elderly_id`, `status`);

-- 反馈表复合索引
CREATE INDEX `idx_feedback_activity_score` ON `activity_feedback` (`activity_id`, `satisfaction_score`);

-- ============================================
-- 9. 创建触发器自动更新统计数据
-- ============================================
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

-- ============================================
-- 升级完成
-- ============================================
SELECT '活动管理模块数据库升级完成' AS message;
