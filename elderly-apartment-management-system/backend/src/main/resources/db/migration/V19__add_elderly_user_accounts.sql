-- ============================================
-- 为每个老人创建独立账号密码
-- 账号规则：老人姓名拼音 + 房间号
-- 密码规则：身份证后6位
-- ============================================

-- 1. 创建老人角色（如果不存在）
INSERT IGNORE INTO role (id, name, code, description, create_time, update_time) VALUES
(7, '老人', 'ELDERLY', '养老院入住老人角色，只能查看自己的数据', NOW(), NOW());

-- 2. 为现有老人创建用户账号
-- 张大爷 - 房间201
INSERT IGNORE INTO user (id, username, password, real_name, phone, email, status, create_time, update_time) VALUES
(101, 'zhangdaye_201', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '张大爷', '13800138001', 'zhangdaye@example.com', 1, NOW(), NOW());

-- 李奶奶 - 房间202
INSERT IGNORE INTO user (id, username, password, real_name, phone, email, status, create_time, update_time) VALUES
(102, 'linainai_202', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '李奶奶', '13800138002', 'linainai@example.com', 1, NOW(), NOW());

-- 王爷爷 - 房间203
INSERT IGNORE INTO user (id, username, password, real_name, phone, email, status, create_time, update_time) VALUES
(103, 'wangyeye_203', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '王爷爷', '13800138003', 'wangyeye@example.com', 1, NOW(), NOW());

-- 赵奶奶 - 房间204
INSERT IGNORE INTO user (id, username, password, real_name, phone, email, status, create_time, update_time) VALUES
(104, 'zhaonainai_204', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '赵奶奶', '13800138004', 'zhaonainai@example.com', 1, NOW(), NOW());

-- 刘大爷 - 房间205
INSERT IGNORE INTO user (id, username, password, real_name, phone, email, status, create_time, update_time) VALUES
(105, 'liudaye_205', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '刘大爷', '13800138005', 'liudaye@example.com', 1, NOW(), NOW());

-- 3. 为老人用户分配角色
INSERT IGNORE INTO user_role (user_id, role_id) VALUES
(101, 7),  -- 张大爷 -> 老人角色
(102, 7),  -- 李奶奶 -> 老人角色
(103, 7),  -- 王爷爷 -> 老人角色
(104, 7),  -- 赵奶奶 -> 老人角色
(105, 7);  -- 刘大爷 -> 老人角色

-- 4. 创建老人账号关联表（建立老人表和用户表的关联）
CREATE TABLE IF NOT EXISTS elderly_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    elderly_id INT NOT NULL COMMENT '老人ID',
    user_id INT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '登录账号',
    password_hint VARCHAR(100) COMMENT '密码提示（身份证后6位）',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_elderly_id (elderly_id),
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_username (username),
    CONSTRAINT fk_elderly_user_elderly FOREIGN KEY (elderly_id) REFERENCES elderly(id),
    CONSTRAINT fk_elderly_user_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人账号关联表';

-- 5. 插入老人账号关联数据
INSERT IGNORE INTO elderly_user (elderly_id, user_id, username, password_hint, create_time, update_time) VALUES
(1, 101, 'zhangdaye_201', '身份证后6位：51234', NOW(), NOW()),
(2, 102, 'linainai_202', '身份证后6位：21234', NOW(), NOW()),
(3, 103, 'wangyeye_203', '身份证后6位：81234', NOW(), NOW()),
(4, 104, 'zhaonainai_204', '身份证后6位：51234', NOW(), NOW()),
(5, 105, 'liudaye_205', '身份证后6位：91234', NOW(), NOW());

-- 6. 更新老人表，添加用户ID字段
ALTER TABLE elderly ADD COLUMN user_id INT DEFAULT NULL COMMENT '关联的用户账号ID' AFTER id;
ALTER TABLE elderly ADD INDEX idx_user_id (user_id);


-- 7. 更新老人表中的用户ID关联
UPDATE elderly SET user_id = 101 WHERE id = 1;
UPDATE elderly SET user_id = 102 WHERE id = 2;
UPDATE elderly SET user_id = 103 WHERE id = 3;
UPDATE elderly SET user_id = 104 WHERE id = 4;
UPDATE elderly SET user_id = 105 WHERE id = 5;

-- 8. 添加外键约束
ALTER TABLE elderly ADD CONSTRAINT fk_elderly_user_id FOREIGN KEY (user_id) REFERENCES user(id);
