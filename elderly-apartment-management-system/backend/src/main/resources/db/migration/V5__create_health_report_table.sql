-- 体检报告表创建脚本
-- 创建时间: 2026-02-08

-- 删除已存在的表
DROP TABLE IF EXISTS `health_report_detail`;
DROP TABLE IF EXISTS `health_report`;

-- 创建体检报告主表
CREATE TABLE `health_report` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `elderly_id` INT NOT NULL COMMENT '老人ID',
    `report_type` INT NOT NULL COMMENT '报告类型：1-月度，2-季度，3-年度',
    `report_date` DATE NOT NULL COMMENT '报告日期',
    `start_date` DATE NOT NULL COMMENT '统计开始日期',
    `end_date` DATE NOT NULL COMMENT '统计结束日期',
    `health_score` INT COMMENT '健康评分(0-100)',
    `overall_assessment` TEXT COMMENT '总体评估',
    `recommendations` TEXT COMMENT '健康建议',
    `doctor_id` INT COMMENT '审核医生ID',
    `doctor_name` VARCHAR(50) COMMENT '审核医生姓名',
    `audit_time` DATETIME COMMENT '审核时间',
    `audit_opinion` TEXT COMMENT '审核意见',
    `status` INT DEFAULT 0 COMMENT '状态：0-待审核，1-已审核',
    `file_url` VARCHAR(500) COMMENT 'PDF文件URL',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    -- 添加索引
    INDEX `idx_elderly_id` (`elderly_id`),
    INDEX `idx_report_type` (`report_type`),
    INDEX `idx_report_date` (`report_date`),
    INDEX `idx_status` (`status`),
    INDEX `idx_deleted` (`deleted`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体检报告主表';

-- 创建体检报告明细表
CREATE TABLE `health_report_detail` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `report_id` INT NOT NULL COMMENT '报告ID',
    `check_item` VARCHAR(50) NOT NULL COMMENT '检查项目',
    `check_value` VARCHAR(50) COMMENT '检查值',
    `unit` VARCHAR(20) COMMENT '单位',
    `reference_range` VARCHAR(100) COMMENT '参考范围',
    `result` INT COMMENT '结果：1-正常，2-异常',
    `trend` INT COMMENT '趋势：1-上升，2-下降，3-平稳',
    `remark` VARCHAR(200) COMMENT '备注',
    
    -- 添加索引
    INDEX `idx_report_id` (`report_id`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体检报告明细表';
