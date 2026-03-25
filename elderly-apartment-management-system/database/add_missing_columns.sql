-- 添加缺失字段到护理相关表
-- 只添加不存在的字段，不删除现有数据

USE elderly_apartment_management;

-- ============================================
-- 1. care_plan 表添加缺失字段
-- ============================================

-- 检查并添加 elderly_name 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'elderly_name');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN elderly_name VARCHAR(50) COMMENT '老人姓名' AFTER elderly_id", "SELECT 'elderly_name already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 plan_type 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'plan_type');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN plan_type TINYINT DEFAULT 1 COMMENT '计划类型(1:日常护理,2:康复护理,3:特殊护理,4:医疗护理)' AFTER elderly_name", "SELECT 'plan_type already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 care_items 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'care_items');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN care_items VARCHAR(500) COMMENT '护理项目清单(JSON格式)' AFTER content", "SELECT 'care_items already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 frequency 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'frequency');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN frequency VARCHAR(50) COMMENT '执行频率' AFTER care_items", "SELECT 'frequency already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 caregiver_id 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'caregiver_id');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN caregiver_id INT COMMENT '负责护理员ID' AFTER frequency", "SELECT 'caregiver_id already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 caregiver_name 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'caregiver_name');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN caregiver_name VARCHAR(50) COMMENT '负责护理员姓名' AFTER caregiver_id", "SELECT 'caregiver_name already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 priority 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'priority');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN priority TINYINT DEFAULT 2 COMMENT '优先级(1:低,2:中,3:高)' AFTER end_date", "SELECT 'priority already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 create_by 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_plan' AND column_name = 'create_by');
SET @sql := IF(@exist = 0, "ALTER TABLE care_plan ADD COLUMN create_by INT COMMENT '创建人ID' AFTER status", "SELECT 'create_by already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引
ALTER TABLE care_plan ADD INDEX idx_caregiver_id (caregiver_id);
ALTER TABLE care_plan ADD INDEX idx_plan_type (plan_type);

-- ============================================
-- 2. care_record 表添加缺失字段
-- ============================================

-- 检查并添加 elderly_name 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'elderly_name');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN elderly_name VARCHAR(50) COMMENT '老人姓名' AFTER elderly_id", "SELECT 'elderly_name already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 room_number 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'room_number');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN room_number VARCHAR(20) COMMENT '房间号' AFTER elderly_name", "SELECT 'room_number already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 caregiver_id 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'caregiver_id');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN caregiver_id INT COMMENT '护理员ID' AFTER care_plan_id", "SELECT 'caregiver_id already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 caregiver_name 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'caregiver_name');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN caregiver_name VARCHAR(50) COMMENT '护理员姓名' AFTER caregiver_id", "SELECT 'caregiver_name already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 care_type 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'care_type');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN care_type TINYINT COMMENT '护理类型(1:晨间护理,2:晚间护理,3:饮食护理,4:康复训练,5:用药协助,6:其他)' AFTER caregiver_name", "SELECT 'care_type already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 care_content 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'care_content');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN care_content TEXT COMMENT '护理内容' AFTER care_type", "SELECT 'care_content already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 care_duration 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'care_duration');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN care_duration INT COMMENT '护理时长(分钟)' AFTER care_content", "SELECT 'care_duration already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 care_effect 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'care_effect');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN care_effect TINYINT COMMENT '护理效果(1:良好,2:一般,3:较差)' AFTER care_duration", "SELECT 'care_effect already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 elderly_condition 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'elderly_condition');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN elderly_condition VARCHAR(255) COMMENT '老人状况' AFTER care_effect", "SELECT 'elderly_condition already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 abnormal_situation 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'abnormal_situation');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN abnormal_situation TEXT COMMENT '异常情况' AFTER elderly_condition", "SELECT 'abnormal_situation already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 notes 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'notes');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN notes VARCHAR(500) COMMENT '备注' AFTER abnormal_situation", "SELECT 'notes already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 care_time 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'care_time');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN care_time DATETIME COMMENT '护理时间' AFTER notes", "SELECT 'care_time already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 image_urls 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'image_urls');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN image_urls VARCHAR(500) COMMENT '图片记录' AFTER status", "SELECT 'image_urls already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 update_time 字段
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_schema = 'elderly_apartment_management' 
               AND table_name = 'care_record' AND column_name = 'update_time');
SET @sql := IF(@exist = 0, "ALTER TABLE care_record ADD COLUMN update_time DATETIME COMMENT '更新时间' AFTER create_time", "SELECT 'update_time already exists'");
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引
ALTER TABLE care_record ADD INDEX idx_care_type (care_type);
ALTER TABLE care_record ADD INDEX idx_care_time (care_time);

-- ============================================
-- 3. 创建 care_assessment 表（如果不存在）
-- ============================================
CREATE TABLE IF NOT EXISTS care_assessment (
  id INT NOT NULL AUTO_INCREMENT COMMENT '评估ID',
  elderly_id INT NOT NULL COMMENT '老人ID',
  elderly_name VARCHAR(50) COMMENT '老人姓名',
  assessment_type TINYINT COMMENT '评估类型(1:入院评估,2:定期评估,3:专项评估)',
  assessment_date DATE COMMENT '评估日期',
  assessor_id INT COMMENT '评估人ID',
  assessor_name VARCHAR(50) COMMENT '评估人姓名',
  bathing_score INT COMMENT '洗澡能力(0-5分)',
  dressing_score INT COMMENT '穿衣能力(0-5分)',
  toileting_score INT COMMENT '如厕能力(0-5分)',
  mobility_score INT COMMENT '移动能力(0-5分)',
  eating_score INT COMMENT '进食能力(0-5分)',
  adl_total_score INT COMMENT 'ADL总分(0-25分)',
  care_level TINYINT COMMENT '护理等级(1:一级护理,2:二级护理,3:三级护理,4:特级护理)',
  mental_state VARCHAR(100) COMMENT '精神状态',
  cognitive_state VARCHAR(100) COMMENT '认知状态',
  physical_state VARCHAR(100) COMMENT '身体状况',
  assessment_result TEXT COMMENT '评估结果',
  care_suggestions TEXT COMMENT '护理建议',
  next_assessment_date DATE COMMENT '下次评估日期',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  deleted TINYINT DEFAULT 0 COMMENT '删除标记(1:已删除,0:未删除)',
  PRIMARY KEY (id),
  KEY idx_elderly_id (elderly_id),
  KEY idx_assessment_date (assessment_date),
  KEY idx_assessor_id (assessor_id),
  KEY idx_care_level (care_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理评估表';

SELECT '字段添加完成！' AS message;
