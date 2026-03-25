-- 插入活动测试数据
INSERT INTO `activity` (title, description, activity_type, activity_type_name, activity_date, start_time, end_time, location, max_participants, current_participants, checked_in_count, min_participants, organizer, organizer_phone, organizer_id, image_url, status, deleted, create_time, update_time) VALUES
('太极拳晨练活动', '每天早晨的太极拳锻炼活动，适合所有老年人参与，有助于身体健康和心理健康。', 2, '体育健身', '2026-02-15', '07:00:00', '08:00:00', '小区广场', 30, 15, 10, 5, '张教练', '13800138001', 1, 'http://localhost:3001/uploads/activities/20260209/1234567890abcdef.jpg', 1, 0, NOW(), NOW()),
('健康知识讲座', '邀请专业医生讲解老年人常见疾病的预防和保健知识。', 3, '知识讲座', '2026-02-16', '14:00:00', '16:00:00', '社区活动中心', 50, 35, 20, 10, '李医生', '13900139001', 2, 'http://localhost:3001/uploads/activities/20260209/abcdef1234567890.jpg', 1, 0, NOW(), NOW()),
('书法绘画兴趣班', '为爱好书法和绘画的老年人提供学习交流的平台。', 4, '手工艺术', '2026-02-17', '09:00:00', '11:00:00', '文化活动室', 20, 18, 15, 8, '王老师', '13700137001', 3, 'http://localhost:3001/uploads/activities/20260209/fedcba0987654321.jpg', 1, 0, NOW(), NOW()),
('春节联欢会', '庆祝春节的文艺演出和互动游戏活动。', 5, '节日庆祝', '2026-02-18', '18:00:00', '21:00:00', '大礼堂', 100, 80, 60, 20, '社区居委会', '13600136001', 4, 'http://localhost:3001/uploads/activities/20260209/9876543210fedcba.jpg', 1, 0, NOW(), NOW()),
('茶话会', '老年人之间的交流座谈，分享生活经验和趣事。', 6, '社交活动', '2026-02-19', '15:00:00', '17:00:00', '茶室', 25, 20, 18, 10, '赵阿姨', '13500135001', 5, 'http://localhost:3001/uploads/activities/20260209/5432167890abcdef.jpg', 1, 0, NOW(), NOW());

-- 插入活动报名开始和结束时间
UPDATE `activity` SET registration_start_time = '2026-02-10T00:00:00', registration_end_time = '2026-02-14T23:59:59' WHERE id = 1;
UPDATE `activity` SET registration_start_time = '2026-02-11T00:00:00', registration_end_time = '2026-02-15T23:59:59' WHERE id = 2;
UPDATE `activity` SET registration_start_time = '2026-02-12T00:00:00', registration_end_time = '2026-02-16T23:59:59' WHERE id = 3;
UPDATE `activity` SET registration_start_time = '2026-02-13T00:00:00', registration_end_time = '2026-02-17T23:59:59' WHERE id = 4;
UPDATE `activity` SET registration_start_time = '2026-02-14T00:00:00', registration_end_time = '2026-02-18T23:59:59' WHERE id = 5;
