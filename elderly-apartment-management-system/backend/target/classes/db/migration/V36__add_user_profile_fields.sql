-- 添加用户资料相关字段到 user 表
ALTER TABLE user
ADD COLUMN nickname VARCHAR(50) NULL COMMENT '昵称' AFTER real_name,
ADD COLUMN avatar VARCHAR(500) NULL COMMENT '头像URL' AFTER nickname,
ADD COLUMN bio TEXT NULL COMMENT '个人简介' AFTER avatar;
