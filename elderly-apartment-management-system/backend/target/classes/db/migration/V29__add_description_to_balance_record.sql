-- 为 balance_record 表添加 description 字段
ALTER TABLE balance_record
ADD COLUMN description TEXT DEFAULT NULL COMMENT '消费详情描述' AFTER title;
