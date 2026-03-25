-- 老人表添加房间号字段
-- 创建时间: 2026-02-08

-- 添加房间号字段
ALTER TABLE `elderly` 
ADD COLUMN `room_number` VARCHAR(20) DEFAULT NULL COMMENT '房间号' AFTER `room_id`;

-- 创建索引
CREATE INDEX `idx_room_number` ON `elderly`(`room_number`);

-- 同步现有数据：根据 room_id 更新 room_number
UPDATE `elderly` e
SET `room_number` = (
    SELECT `room_number` 
    FROM `room` r 
    WHERE r.id = e.room_id
)
WHERE `room_id` IS NOT NULL;

-- 更新没有房间号的老人，设置默认值
UPDATE `elderly` 
SET `room_number` = '未分配' 
WHERE `room_number` IS NULL AND `status` = 1;
