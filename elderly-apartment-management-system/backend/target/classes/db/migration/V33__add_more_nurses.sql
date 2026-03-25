-- ============================================
-- 添加更多护理人员到user表
-- ============================================

-- 插入新的护理人员（密码都是123456的加密形式）
INSERT INTO user (id, username, password, real_name, phone, email, status, create_time, update_time, deleted) VALUES
(11, 'nurse03', '$2a$10$N.zmdr9k7uOCQ1iXHmJhQOq8fPYxGzX7q8fPYxGzX7q8fPYxGzX7q', '王护士', '13800138011', 'nurse03@elderly.com', 1, '2026-01-03 09:00:00', '2026-02-22 10:00:00', 0),
(12, 'nurse04', '$2a$10$N.zmdr9k7uOCQ1iXHmJhQOq8fPYxGzX7q8fPYxGzX7q8fPYxGzX7q', '李护士', '13800138012', 'nurse04@elderly.com', 1, '2026-01-03 09:30:00', '2026-02-22 10:30:00', 0),
(13, 'nurse05', '$2a$10$N.zmdr9k7uOCQ1iXHmJhQOq8fPYxGzX7q8fPYxGzX7q8fPYxGzX7q', '赵护士', '13800138013', 'nurse05@elderly.com', 1, '2026-01-04 08:00:00', '2026-02-23 09:00:00', 0)
ON DUPLICATE KEY UPDATE
    real_name = VALUES(real_name),
    phone = VALUES(phone),
    email = VALUES(email),
    status = VALUES(status),
    update_time = VALUES(update_time);

-- 为新的护理人员分配NURSE角色（role_id=4是护士角色）
INSERT INTO user_role (user_id, role_id) VALUES
(11, 4),
(12, 4),
(13, 4)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 确保护理员角色关联正确（id=4,5,6也应该关联NURSE或CAREGIVER角色）
-- id=4 nurse01 已经是NURSE角色
-- id=5 nurse02 已经是NURSE角色  
-- id=6 caregiver01 是CAREGIVER角色
INSERT INTO user_role (user_id, role_id) VALUES
(6, 5)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
