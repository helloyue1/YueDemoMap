-- 健康档案表创建脚本
-- 创建时间: 2026-02-08

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `health_record`;

-- 创建健康档案表
CREATE TABLE `health_record` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `elderly_id` INT NOT NULL COMMENT '老人ID，关联elderly表',
    `systolic_pressure` INT DEFAULT NULL COMMENT '收缩压（高压）mmHg',
    `diastolic_pressure` INT DEFAULT NULL COMMENT '舒张压（低压）mmHg',
    `blood_sugar` DECIMAL(4,2) DEFAULT NULL COMMENT '血糖 mmol/L',
    `heart_rate` INT DEFAULT NULL COMMENT '心率 次/分钟',
    `temperature` DECIMAL(4,2) DEFAULT NULL COMMENT '体温 ℃',
    `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重 kg',
    `height` DECIMAL(5,2) DEFAULT NULL COMMENT '身高 cm',
    `blood_oxygen` INT DEFAULT NULL COMMENT '血氧饱和度 %',
    `health_status` INT NOT NULL DEFAULT 1 COMMENT '健康状态：1-良好，2-一般，3-较差，4-危急',
    `medical_history` TEXT COMMENT '既往病史',
    `allergy_history` TEXT COMMENT '过敏史',
    `medication` TEXT COMMENT '用药记录',
    `diagnosis` TEXT COMMENT '诊断结果',
    `check_date` DATE NOT NULL COMMENT '检查日期',
    `doctor` VARCHAR(50) DEFAULT NULL COMMENT '检查医生',
    `notes` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    -- 添加索引
    INDEX `idx_elderly_id` (`elderly_id`),
    INDEX `idx_health_status` (`health_status`),
    INDEX `idx_check_date` (`check_date`),
    INDEX `idx_deleted` (`deleted`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康档案表';

-- 插入测试数据
INSERT INTO `health_record` (
    `elderly_id`, 
    `systolic_pressure`, 
    `diastolic_pressure`, 
    `blood_sugar`, 
    `heart_rate`, 
    `temperature`, 
    `weight`, 
    `height`, 
    `blood_oxygen`, 
    `health_status`, 
    `medical_history`, 
    `allergy_history`, 
    `medication`, 
    `diagnosis`, 
    `check_date`, 
    `doctor`, 
    `notes`,
    `create_time`,
    `update_time`
) 
SELECT 
    e.id,
    120 + FLOOR(RAND() * 40),
    70 + FLOOR(RAND() * 30),
    4.0 + RAND() * 6,
    60 + FLOOR(RAND() * 40),
    36.0 + RAND() * 2,
    50 + RAND() * 40,
    150 + FLOOR(RAND() * 30),
    95 + FLOOR(RAND() * 5),
    1 + FLOOR(RAND() * 4),
    CASE WHEN RAND() > 0.7 THEN '高血压、糖尿病' WHEN RAND() > 0.5 THEN '心脏病' ELSE NULL END,
    CASE WHEN RAND() > 0.8 THEN '青霉素过敏' ELSE NULL END,
    CASE WHEN RAND() > 0.6 THEN '降压药、降糖药' ELSE NULL END,
    '定期检查，身体状况良好',
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY),
    '张医生',
    '建议继续保持良好的生活习惯',
    NOW(),
    NOW()
FROM `elderly` e
WHERE e.deleted = 0
LIMIT 10;
