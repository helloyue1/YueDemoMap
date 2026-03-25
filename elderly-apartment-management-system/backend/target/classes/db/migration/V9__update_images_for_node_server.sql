-- 更新图片路径为相对路径格式（去掉 /uploads/ 前缀）
-- 这样前端可以通过 getImageUrl 函数动态生成完整URL

-- 将 /uploads/ 开头的路径转换为相对路径
UPDATE `room` 
SET `images` = SUBSTRING(`images`, 10) 
WHERE `images` LIKE '/uploads/%';

-- 将旧的绝对URL转换为相对路径
UPDATE `room` 
SET `images` = SUBSTRING(`images`, LOCATE('/uploads/', `images`) + 9) 
WHERE `images` LIKE 'http://localhost:8080/uploads/%';

-- 更新活动表中的图片路径
UPDATE `activity` 
SET `image` = SUBSTRING(`image`, 10) 
WHERE `image` LIKE '/uploads/%';

UPDATE `activity` 
SET `image` = SUBSTRING(`image`, LOCATE('/uploads/', `image`) + 9) 
WHERE `image` LIKE 'http://localhost:8080/uploads/%';
