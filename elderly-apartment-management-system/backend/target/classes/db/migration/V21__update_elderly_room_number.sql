-- 更新老人表的房间号字段
-- 修复 V17 插入的老人数据没有 room_number 的问题

-- 同步现有数据：根据 room_id 更新 room_number
UPDATE `elderly` e
SET `room_number` = (
    SELECT `room_number` 
    FROM `room` r 
    WHERE r.id = e.room_id
)
WHERE `room_id` IS NOT NULL AND (`room_number` IS NULL OR `room_number` = '');

-- 更新没有房间号的老人，设置默认值
UPDATE `elderly` 
SET `room_number` = '未分配' 
WHERE (`room_number` IS NULL OR `room_number` = '') AND `status` = 1;
