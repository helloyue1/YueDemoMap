-- 修改 images 字段为 TEXT 类型，支持存储更多图片
ALTER TABLE `room` MODIFY `images` TEXT COMMENT '房间图片路径列表，多个路径用逗号分隔';
