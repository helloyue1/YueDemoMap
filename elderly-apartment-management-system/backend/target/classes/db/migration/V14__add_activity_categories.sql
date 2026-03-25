-- 更新现有分类的 is_active 为 1
UPDATE `activity_category` SET is_active = 1 WHERE deleted = 0;
