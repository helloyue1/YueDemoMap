-- ============================================
-- 修复护理预约表的elderly_id数据
-- 将user_id转换为正确的elderly_id
-- ============================================

-- 更新care_booking表中的elderly_id，将101-105映射到1-5
UPDATE care_booking SET elderly_id = 1 WHERE elderly_id = 101;
UPDATE care_booking SET elderly_id = 2 WHERE elderly_id = 102;
UPDATE care_booking SET elderly_id = 3 WHERE elderly_id = 103;
UPDATE care_booking SET elderly_id = 4 WHERE elderly_id = 104;
UPDATE care_booking SET elderly_id = 5 WHERE elderly_id = 105;

-- 确保elderly_name和room_number字段有值（根据elderly表更新）
UPDATE care_booking cb
SET 
    cb.elderly_name = (SELECT e.name FROM elderly e WHERE e.id = cb.elderly_id),
    cb.room_number = (SELECT e.room_number FROM elderly e WHERE e.id = cb.elderly_id),
    cb.phone = (SELECT e.phone FROM elderly e WHERE e.id = cb.elderly_id)
WHERE cb.elderly_id IS NOT NULL;
