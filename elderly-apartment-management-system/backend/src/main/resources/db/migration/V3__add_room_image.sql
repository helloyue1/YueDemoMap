-- 房间表添加图片字段
-- 创建时间: 2026-02-08

-- 添加房间图片字段（存储图片URL，多个图片用逗号分隔）
ALTER TABLE room ADD COLUMN images VARCHAR(1000) DEFAULT '' COMMENT '房间图片URL列表，多个用逗号分隔' AFTER has_balcony;

-- 添加楼层字段
ALTER TABLE room ADD COLUMN floor INT DEFAULT 1 COMMENT '楼层' AFTER room_number;

-- 添加设施描述字段
ALTER TABLE room ADD COLUMN facilities VARCHAR(500) DEFAULT '' COMMENT '设施描述' AFTER has_balcony;
