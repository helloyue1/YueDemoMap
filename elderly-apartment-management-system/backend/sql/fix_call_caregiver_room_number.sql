-- 修复呼叫护工记录中缺失的房间号
-- 根据老人ID更新房间号

UPDATE call_caregiver cc
JOIN elderly e ON cc.elderly_id = e.id
SET cc.room_number = e.room_number
WHERE cc.room_number IS NULL OR cc.room_number = '';

-- 验证修复结果
SELECT cc.id, cc.elderly_name, cc.room_number, e.room_number as elderly_room_number
FROM call_caregiver cc
JOIN elderly e ON cc.elderly_id = e.id
WHERE cc.deleted = 0
ORDER BY cc.id DESC;
