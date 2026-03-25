-- 添加 category_id 字段到 activity 表
ALTER TABLE `activity` ADD COLUMN `category_id` INT DEFAULT NULL COMMENT '活动分类ID' AFTER `activity_type_name`;

-- 添加外键约束（可选）
-- ALTER TABLE `activity` ADD CONSTRAINT `fk_activity_category` FOREIGN KEY (`category_id`) REFERENCES `activity_category`(`id`);

-- 创建索引
CREATE INDEX `idx_category_id` ON `activity`(`category_id`);
