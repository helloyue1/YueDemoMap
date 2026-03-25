-- 添加封面图片字段到房间表
ALTER TABLE `room` ADD COLUMN `cover_image` VARCHAR(500) NULL COMMENT '封面图片路径' AFTER `facilities`;

-- 更新已有数据，将第一张图片设为封面
UPDATE `room` SET `cover_image` = SUBSTRING_INDEX(`images`, ',', 1) WHERE `images` IS NOT NULL AND `images` != '';
