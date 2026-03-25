-- ============================================
-- 养老公寓管理系统 - 特定用户关联测试数据集
-- 以特定老人（张建国，ID=1）为核心，展示与其他实体的完整关联关系
-- 生成日期: 2026-02-22
-- ============================================

USE elderly_apartment_management;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 说明：本数据集以老人"张建国"（ID=1）为核心
-- 展示其在系统中的完整数据关联关系
-- ============================================

-- ============================================
-- 1. 核心用户信息
-- ============================================

-- 用户账户（老人作为用户登录系统）
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(101, 'zhangjianguo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张建国', '13800138001', 'zhangjianguo@elderly.com', 1, '2026-01-15 09:00:00', '2026-02-22 10:00:00', 0)
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

-- 用户角色关联（老人用户角色）
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(101, 6)
ON DUPLICATE KEY UPDATE `user_id` = VALUES(`user_id`);

-- ============================================
-- 2. 老人核心信息（张建国）
-- ============================================

-- 老人基本信息
INSERT INTO `elderly` (`id`, `name`, `gender`, `age`, `birthday`, `id_card`, `phone`, `address`, `emergency_contact`, `emergency_phone`, `room_id`, `room_number`, `check_in_date`, `nationality`, `health_status`, `status`, `notes`, `create_time`, `update_time`, `deleted`) VALUES
(101, '张建国', 1, 78, '1947-03-15', '110101194703151234', '13800138001', '北京市朝阳区建国路100号', '张伟', '13900139001', 101, 'A101', '2026-01-15', '汉族', '高血压，轻度糖尿病', 1, '性格开朗，喜欢下棋和太极拳，需定期监测血压', '2026-01-15 09:00:00', '2026-02-22 10:00:00', 0)
ON DUPLICATE KEY UPDATE `id_card` = VALUES(`id_card`);

-- 关联房间信息
INSERT INTO `room` (`id`, `room_number`, `floor`, `type`, `capacity`, `current_occupancy`, `status`, `price`, `has_wifi`, `has_tv`, `has_ac`, `has_bathroom`, `has_balcony`, `facilities`, `cover_image`, `images`, `remark`, `create_time`, `update_time`, `deleted`) VALUES
(101, 'A101', 1, 1, 1, 1, 2, 3500.00, 1, 1, 1, 1, 0, '空调,电视,独立卫生间,24小时热水,紧急呼叫系统', 'http://localhost:3001/uploads/rooms/A101_cover.jpg', '["http://localhost:3001/uploads/rooms/A101_1.jpg","http://localhost:3001/uploads/rooms/A101_2.jpg","http://localhost:3001/uploads/rooms/A101_3.jpg"]', '单人间，朝阳，靠近护士站，适合需要照护的老人', '2026-01-01 08:00:00', '2026-02-22 10:00:00', 0)
ON DUPLICATE KEY UPDATE `room_number` = VALUES(`room_number`);

-- ============================================
-- 3. 健康管理关联数据
-- ============================================

-- 健康档案（多条记录展示历史变化）
INSERT INTO `health_record` (`id`, `elderly_id`, `check_date`, `height`, `weight`, `systolic_pressure`, `diastolic_pressure`, `blood_sugar`, `heart_rate`, `temperature`, `health_status`, `diagnosis`, `doctor`, `notes`, `create_time`, `update_time`, `deleted`) VALUES
(101, 101, '2026-01-15', 170.5, 70.0, 145, 95, 7.2, 75, 36.6, 2, '高血压二级，血糖偏高', '李医生', '入院体检，血压偏高需关注', '2026-01-15 10:00:00', '2026-01-15 10:00:00', 0),
(102, 101, '2026-01-30', 170.5, 69.5, 142, 92, 7.0, 73, 36.5, 2, '血压有所下降', '李医生', '服药后血压改善', '2026-01-30 09:00:00', '2026-01-30 09:00:00', 0),
(103, 101, '2026-02-15', 170.5, 69.0, 138, 88, 6.8, 72, 36.4, 1, '血压控制良好', '李医生', '继续当前治疗方案', '2026-02-15 09:00:00', '2026-02-15 09:00:00', 0),
(104, 101, '2026-02-22', 170.5, 68.5, 135, 85, 6.5, 70, 36.5, 1, '各项指标稳定', '李医生', '健康状况良好', '2026-02-22 08:00:00', '2026-02-22 08:00:00', 0)
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `check_date` = VALUES(`check_date`);

-- 健康报告（月度健康评估）
INSERT INTO `health_report` (`id`, `elderly_id`, `report_type`, `report_date`, `start_date`, `end_date`, `health_score`, `overall_assessment`, `recommendations`, `doctor_name`, `doctor_id`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(101, 101, 1, '2026-01-31', '2026-01-15', '2026-01-31', 75, '入院初期血压偏高，经治疗后有所改善', '继续低盐饮食，按时服药，适量运动', '李医生', 3, 1, '2026-01-31 16:00:00', '2026-01-31 16:00:00', 0),
(102, 101, 1, '2026-02-22', '2026-02-01', '2026-02-22', 82, '整体健康状况良好，血压控制稳定', '保持当前治疗方案，定期监测', '李医生', 3, 1, '2026-02-22 17:00:00', '2026-02-22 17:00:00', 0)
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `report_date` = VALUES(`report_date`);

-- 健康报告详情
INSERT INTO `health_report_detail` (`id`, `report_id`, `check_item`, `check_value`, `reference_range`, `unit`, `result`, `trend`, `remark`) VALUES
(101, 101, '收缩压', '138', '90-140', 'mmHg', 1, 3, '正常高值'),
(102, 101, '舒张压', '88', '60-90', 'mmHg', 1, 3, '正常'),
(103, 101, '空腹血糖', '6.8', '3.9-6.1', 'mmol/L', 2, 3, '偏高'),
(104, 101, '心率', '72', '60-100', '次/分', 1, 3, '正常'),
(105, 102, '收缩压', '135', '90-140', 'mmHg', 1, 3, '正常高值'),
(106, 102, '舒张压', '85', '60-90', 'mmHg', 1, 3, '正常'),
(107, 102, '空腹血糖', '6.5', '3.9-6.1', 'mmol/L', 2, 3, '偏高'),
(108, 102, 'BMI', '23.5', '18.5-24', 'kg/m²', 1, 3, '正常')
ON DUPLICATE KEY UPDATE `report_id` = VALUES(`report_id`), `check_item` = VALUES(`check_item`);

-- 日常健康监测数据（展示多日监测记录）
INSERT INTO `health_data` (`id`, `elderly_id`, `record_type`, `systolic_pressure`, `diastolic_pressure`, `blood_pressure`, `heart_rate`, `body_temperature`, `blood_sugar`, `blood_oxygen`, `weight`, `record_content`, `health_note`, `record_time`, `recorder_id`, `recorder_name`, `create_time`, `update_time`, `deleted`) VALUES
(101, 101, 1, 138, 88, '138/88', NULL, NULL, NULL, NULL, NULL, '晨间血压测量', '正常范围', '2026-02-20 07:00:00', 4, '张护士', '2026-02-20 07:00:00', '2026-02-20 07:00:00', 0),
(102, 101, 2, NULL, NULL, NULL, NULL, NULL, 6.8, NULL, NULL, '空腹血糖监测', '略高', '2026-02-20 07:30:00', 4, '张护士', '2026-02-20 07:30:00', '2026-02-20 07:30:00', 0),
(103, 101, 1, 136, 86, '136/86', NULL, NULL, NULL, NULL, NULL, '晨间血压测量', '正常', '2026-02-21 07:00:00', 4, '张护士', '2026-02-21 07:00:00', '2026-02-21 07:00:00', 0),
(104, 101, 2, NULL, NULL, NULL, NULL, NULL, 6.6, NULL, NULL, '空腹血糖监测', '正常', '2026-02-21 07:30:00', 4, '张护士', '2026-02-21 07:30:00', '2026-02-21 07:30:00', 0),
(105, 101, 1, 135, 85, '135/85', NULL, NULL, NULL, NULL, NULL, '晨间血压测量', '稳定', '2026-02-22 07:00:00', 4, '张护士', '2026-02-22 07:00:00', '2026-02-22 07:00:00', 0),
(106, 101, 2, NULL, NULL, NULL, NULL, NULL, 6.5, NULL, NULL, '空腹血糖监测', '正常', '2026-02-22 07:30:00', 4, '张护士', '2026-02-22 07:30:00', '2026-02-22 07:30:00', 0),
(107, 101, 4, NULL, NULL, NULL, 70, NULL, NULL, NULL, NULL, '心率监测', '正常', '2026-02-22 08:00:00', 4, '张护士', '2026-02-22 08:00:00', '2026-02-22 08:00:00', 0),
(108, 101, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 68.5, '体重测量', '稳定', '2026-02-22 06:00:00', 4, '张护士', '2026-02-22 06:00:00', '2026-02-22 06:00:00', 0)
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `record_time` = VALUES(`record_time`);

-- ============================================
-- 4. 护理管理关联数据
-- ============================================

-- 护理计划（针对不同健康问题的护理方案）
INSERT INTO `care_plan` (`id`, `elderly_id`, `elderly_name`, `plan_type`, `title`, `content`, `care_items`, `frequency`, `caregiver_id`, `caregiver_name`, `start_date`, `end_date`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(101, 101, '张建国', 1, '高血压日常护理计划', '每日监测血压，按时服用降压药，低盐饮食，适量运动', '血压监测、用药提醒、饮食监督、运动指导', '每日', 4, '张护士', '2026-01-15', '2026-07-15', 1, '2026-01-15 10:00:00', '2026-02-22 09:00:00', 0),
(102, 101, '张建国', 4, '糖尿病饮食管理计划', '控制糖分摄入，监测血糖，定时定量进餐', '血糖监测、饮食控制、营养指导', '每日', 5, '刘护士', '2026-01-15', '2026-07-15', 1, '2026-01-15 10:30:00', '2026-02-22 09:00:00', 0)
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `title` = VALUES(`title`);

-- 护理记录（展示日常护理服务）
INSERT INTO `care_record` (`id`, `elderly_id`, `elderly_name`, `room_number`, `care_plan_id`, `caregiver_id`, `caregiver_name`, `care_type`, `care_content`, `care_duration`, `care_effect`, `elderly_condition`, `abnormal_situation`, `notes`, `care_time`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(101, 101, '张建国', 'A101', 101, 4, '张护士', 1, '协助洗漱、整理床铺、测量血压', 30, 1, '良好', NULL, '血压138/88', '2026-02-20 07:00:00', 1, '2026-02-20 07:30:00', '2026-02-20 07:30:00', 0),
(102, 101, '张建国', 'A101', 101, 4, '张护士', 2, '提醒并协助服用降压药（氨氯地平）', 10, 1, '已服药', NULL, '按时服药，无不适', '2026-02-20 08:00:00', 1, '2026-02-20 08:10:00', '2026-02-20 08:10:00', 0),
(103, 101, '张建国', 'A101', 102, 5, '刘护士', 5, '监督早餐饮食，确认无糖食品', 20, 1, '良好', NULL, '饮食控制良好', '2026-02-20 08:30:00', 1, '2026-02-20 08:50:00', '2026-02-20 08:50:00', 0),
(104, 101, '张建国', 'A101', 101, 4, '张护士', 1, '协助洗漱、整理床铺、测量血压', 30, 1, '良好', NULL, '血压136/86', '2026-02-21 07:00:00', 1, '2026-02-21 07:30:00', '2026-02-21 07:30:00', 0),
(105, 101, '张建国', 'A101', 101, 4, '张护士', 2, '提醒并协助服用降压药', 10, 1, '已服药', NULL, '按时服药', '2026-02-21 08:00:00', 1, '2026-02-21 08:10:00', '2026-02-21 08:10:00', 0),
(106, 101, '张建国', 'A101', 101, 4, '张护士', 1, '协助洗漱、测量血压', 30, 1, '良好', NULL, '血压135/85，稳定', '2026-02-22 07:00:00', 1, '2026-02-22 07:30:00', '2026-02-22 07:30:00', 0),
(107, 101, '张建国', 'A101', 101, 4, '张护士', 2, '提醒并协助服用降压药', 10, 1, '已服药', NULL, '服药后无不适', '2026-02-22 08:00:00', 1, '2026-02-22 08:10:00', '2026-02-22 08:10:00', 0),
(108, 101, '张建国', 'A101', 102, 5, '刘护士', 5, '监督午餐饮食，低盐低糖', 20, 1, '良好', NULL, '饮食控制良好', '2026-02-22 12:00:00', 1, '2026-02-22 12:20:00', '2026-02-22 12:20:00', 0)
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `care_time` = VALUES(`care_time`);

-- 护理评估（ADL评估和专项评估）
INSERT INTO `care_assessment` (`id`, `elderly_id`, `elderly_name`, `assessment_type`, `assessment_date`, `assessor_id`, `assessor_name`, `bathing_score`, `dressing_score`, `toileting_score`, `mobility_score`, `eating_score`, `adl_total_score`, `care_level`, `mental_state`, `cognitive_state`, `physical_state`, `assessment_result`, `care_suggestions`, `next_assessment_date`, `create_time`, `update_time`, `deleted`) VALUES
(101, 101, '张建国', 1, '2026-01-15', 3, '李医生', 4, 5, 5, 4, 5, 23, 1, '良好', '正常', '良好', '入院ADL评估：良好，可独立完成大部分日常活动，高血压需关注', '定期监测血压，低盐饮食，适量运动', '2026-02-15', '2026-01-15 14:00:00', '2026-01-15 14:00:00', 0),
(102, 101, '张建国', 1, '2026-02-15', 3, '李医生', 5, 5, 5, 5, 5, 25, 1, '良好', '正常', '良好', '月度ADL评估：优秀，身体状况改善，血压控制良好', '继续保持，可适当增加运动量', '2026-03-15', '2026-02-15 14:00:00', '2026-02-15 14:00:00', 0),
(103, 101, '张建国', 2, '2026-01-15', 3, '李医生', 5, 5, 5, 4, 5, 24, 1, '良好', '正常', '良好', '跌倒风险评估：低风险，行动稳健', '注意防滑，建议浴室安装扶手', '2026-04-15', '2026-01-15 15:00:00', '2026-01-15 15:00:00', 0),
(104, 101, '张建国', 5, '2026-02-15', 3, '李医生', 5, 5, 5, 5, 5, 25, 1, '良好', '正常', '良好', '营养评估：良好，体重控制得当', '继续保持均衡饮食', '2026-05-15', '2026-02-15 15:00:00', '2026-02-15 15:00:00', 0)
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `assessment_date` = VALUES(`assessment_date`);

-- ============================================
-- 5. 活动管理关联数据
-- ============================================

-- 活动报名记录（展示参与的活动）
INSERT INTO `activity_signup` (`id`, `activity_id`, `activity_title`, `elderly_id`, `elderly_name`, `elderly_phone`, `room_number`, `emergency_contact_name`, `emergency_contact_phone`, `health_status`, `special_requirements`, `signup_source`, `signup_time`, `status`, `cancel_reason`, `reject_reason`, `checkin_time`, `checkin_method`, `checked_in_by`, `is_waitlist`, `waitlist_order`, `notes`, `create_by`, `create_time`, `update_time`, `deleted`) VALUES
(101, 1, '太极拳晨练', 101, '张建国', '13800138001', 'A101', '张伟', '13900139001', '高血压', '动作幅度小一点，避免剧烈运动', 1, '2026-01-20 09:00:00', 1, NULL, NULL, NULL, NULL, NULL, 0, 0, '每周参加', 101, '2026-01-20 09:00:00', '2026-01-20 09:00:00', 0),
(102, 6, '象棋比赛', 101, '张建国', '13800138001', 'A101', '张伟', '13900139001', '高血压', '无', 1, '2026-02-15 10:00:00', 1, NULL, NULL, NULL, NULL, NULL, 0, 0, '期待比赛', 101, '2026-02-15 10:00:00', '2026-02-15 10:00:00', 0),
(103, 2, '书法绘画班', 101, '张建国', '13800138001', 'A101', '张伟', '13900139001', '高血压', '需要桌椅调整', 1, '2026-02-10 14:00:00', 1, NULL, NULL, NULL, NULL, NULL, 0, 0, '喜欢书法', 101, '2026-02-10 14:00:00', '2026-02-10 14:00:00', 0),
(104, 8, '春节联欢会', 101, '张建国', '13800138001', 'A101', '张伟', '13900139001', '高血压', '无', 1, '2026-02-05 10:00:00', 4, NULL, NULL, '2026-02-10 18:05:00', 2, 4, 0, 0, '已参加，活动精彩', 101, '2026-02-05 10:00:00', '2026-02-10 18:05:00', 0)
ON DUPLICATE KEY UPDATE `activity_id` = VALUES(`activity_id`), `elderly_id` = VALUES(`elderly_id`);

-- 活动反馈记录
INSERT INTO `activity_feedback` (`id`, `activity_id`, `activity_title`, `signup_id`, `elderly_id`, `elderly_name`, `satisfaction_score`, `organization_score`, `content_score`, `staff_score`, `overall_score`, `is_would_recommend`, `feedback_content`, `favorite_part`, `improvement_suggestions`, `feedback_time`, `status`, `handler_id`, `handler_name`, `handler_reply`, `reply_time`, `notes`, `create_time`, `update_time`, `deleted`) VALUES
(101, 8, '春节联欢会', 104, 101, '张建国', 5, 5, 5, 5, 5.00, 1, '春节联欢会办得非常好，节目精彩，饭菜可口，感受到了家的温暖', '小品表演和抽奖环节', '希望明年增加更多互动游戏', '2026-02-11 10:00:00', 2, 2, '王经理', '感谢您的参与和建议，我们会继续改进', '2026-02-12 09:00:00', '已处理', '2026-02-11 10:00:00', '2026-02-12 09:00:00', 0),
(102, 1, '太极拳晨练', NULL, 101, '张建国', 4, 5, 4, 5, 4.50, 1, '太极拳活动很好，老师很专业，但对我来说动作有点快', '老师的示范动作', '希望能有适合初学者的慢节奏班级', '2026-02-15 09:00:00', 1, NULL, NULL, NULL, NULL, '待处理', '2026-02-15 09:00:00', '2026-02-15 09:00:00', 0)
ON DUPLICATE KEY UPDATE `activity_id` = VALUES(`activity_id`), `elderly_id` = VALUES(`elderly_id`);

-- ============================================
-- 6. 餐饮管理关联数据
-- ============================================

-- 订餐记录（展示多日订餐）
INSERT INTO `meal_order` (`id`, `elderly_id`, `order_date`, `meal_type`, `menu_items`, `special_requirements`, `status`, `delivery_time`, `remark`, `create_time`, `update_time`) VALUES
(101, 101, '2026-02-20', 1, '[1,2,4]', '低盐、无糖', 2, '2026-02-20 07:30:00', '早餐', '2026-02-19 18:00:00', '2026-02-20 07:30:00'),
(102, 101, '2026-02-20', 2, '[6,5,9]', '低盐、少油、无糖', 2, '2026-02-20 12:00:00', '午餐', '2026-02-19 18:00:00', '2026-02-20 12:00:00'),
(103, 101, '2026-02-20', 3, '[11,12]', '无糖', 2, '2026-02-20 18:00:00', '晚餐', '2026-02-19 18:00:00', '2026-02-20 18:00:00'),
(104, 101, '2026-02-21', 1, '[13,2,4]', '低盐、无糖', 2, '2026-02-21 07:30:00', '早餐', '2026-02-20 18:00:00', '2026-02-21 07:30:00'),
(105, 101, '2026-02-21', 2, '[7,5,8,9]', '低盐、无糖', 2, '2026-02-21 12:00:00', '午餐', '2026-02-20 18:00:00', '2026-02-21 12:00:00'),
(106, 101, '2026-02-21', 3, '[11]', '无糖', 2, '2026-02-21 18:00:00', '晚餐', '2026-02-20 18:00:00', '2026-02-21 18:00:00'),
(107, 101, '2026-02-22', 1, '[1,2,4]', '低盐、无糖', 2, '2026-02-22 07:30:00', '早餐', '2026-02-21 18:00:00', '2026-02-22 07:30:00'),
(108, 101, '2026-02-22', 2, '[6,5,9]', '低盐、少油、无糖', 2, '2026-02-22 12:00:00', '午餐', '2026-02-21 18:00:00', '2026-02-22 12:00:00'),
(109, 101, '2026-02-22', 3, '[11,12]', '无糖', 1, NULL, '晚餐', '2026-02-21 18:00:00', '2026-02-21 18:00:00')
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`), `order_date` = VALUES(`order_date`), `meal_type` = VALUES(`meal_type`);

-- 送餐记录
INSERT INTO `meal_delivery` (`id`, `order_id`, `elderly_id`, `delivery_date`, `meal_type`, `delivery_status`, `delivery_time`, `receiver`, `remark`, `create_time`, `update_time`) VALUES
(101, 101, 101, '2026-02-20', 1, 1, '2026-02-20 07:30:00', '张建国', '已送达，老人满意', '2026-02-20 07:30:00', '2026-02-20 07:30:00'),
(102, 102, 101, '2026-02-20', 2, 1, '2026-02-20 12:00:00', '张建国', '已送达', '2026-02-20 12:00:00', '2026-02-20 12:00:00'),
(103, 103, 101, '2026-02-20', 3, 1, '2026-02-20 18:00:00', '张建国', '已送达', '2026-02-20 18:00:00', '2026-02-20 18:00:00'),
(104, 104, 101, '2026-02-21', 1, 1, '2026-02-21 07:30:00', '张建国', '已送达', '2026-02-21 07:30:00', '2026-02-21 07:30:00'),
(105, 105, 101, '2026-02-21', 2, 1, '2026-02-21 12:00:00', '张建国', '已送达', '2026-02-21 12:00:00', '2026-02-21 12:00:00'),
(106, 106, 101, '2026-02-21', 3, 1, '2026-02-21 18:00:00', '张建国', '已送达', '2026-02-21 18:00:00', '2026-02-21 18:00:00'),
(107, 107, 101, '2026-02-22', 1, 1, '2026-02-22 07:30:00', '张建国', '已送达', '2026-02-22 07:30:00', '2026-02-22 07:30:00'),
(108, 108, 101, '2026-02-22', 2, 1, '2026-02-22 12:00:00', '张建国', '已送达', '2026-02-22 12:00:00', '2026-02-22 12:00:00')
ON DUPLICATE KEY UPDATE `order_id` = VALUES(`order_id`);

-- ============================================
-- 7. 费用管理关联数据
-- ============================================

-- 老人费用账户
INSERT INTO `fee_account` (`id`, `elderly_id`, `balance`, `total_paid`, `total_consumed`, `status`, `create_time`, `update_time`) VALUES
(101, 101, 2500.00, 12200.00, 9700.00, 1, '2026-01-15 09:00:00', '2026-02-22 10:00:00')
ON DUPLICATE KEY UPDATE `elderly_id` = VALUES(`elderly_id`);

-- 费用账单（多月账单）
INSERT INTO `fee_bill` (`id`, `bill_no`, `elderly_id`, `bill_month`, `bill_start_date`, `bill_end_date`, `room_fee`, `care_fee`, `meal_fee`, `medical_fee`, `other_fee`, `total_amount`, `discount_amount`, `payable_amount`, `paid_amount`, `status`, `due_date`, `paid_time`, `remark`, `create_time`, `update_time`) VALUES
(101, 'BILL202601101', 101, '2026-01', '2026-01-15', '2026-01-31', 1925.00, 825.00, 495.00, 0.00, 100.00, 3345.00, 0.00, 3345.00, 3345.00, 2, '2026-01-25', '2026-01-20 10:00:00', '1月入住半月账单', '2026-01-15 10:00:00', '2026-01-20 10:00:00'),
(102, 'BILL202602101', 101, '2026-02', '2026-02-01', '2026-02-28', 3500.00, 1500.00, 900.00, 0.00, 200.00, 6100.00, 0.00, 6100.00, 6100.00, 2, '2026-02-10', '2026-02-05 10:00:00', '2月账单', '2026-02-01 08:00:00', '2026-02-05 10:00:00'),
(103, 'BILL202603101', 101, '2026-03', '2026-03-01', '2026-03-31', 3500.00, 1500.00, 900.00, 0.00, 200.00, 6100.00, 0.00, 6100.00, 0.00, 0, '2026-03-10', NULL, '3月待缴费账单', '2026-03-01 08:00:00', '2026-03-01 08:00:00')
ON DUPLICATE KEY UPDATE `bill_no` = VALUES(`bill_no`);

-- 费用明细
INSERT INTO `fee_detail` (`id`, `bill_id`, `fee_type_id`, `fee_name`, `amount`, `quantity`, `unit_price`, `fee_date`, `description`, `source_id`, `source_type`, `create_time`) VALUES
(101, 102, 1, '单人间住宿费', 3500.00, 1.00, 3500.00, '2026-02-01', '2月住宿费', NULL, NULL, '2026-02-01 08:00:00'),
(102, 102, 2, '一级护理费', 1500.00, 1.00, 1500.00, '2026-02-01', '2月护理费', NULL, NULL, '2026-02-01 08:00:00'),
(103, 102, 3, '餐饮费', 900.00, 1.00, 900.00, '2026-02-01', '2月餐费', NULL, NULL, '2026-02-01 08:00:00'),
(104, 102, 6, '其他费用', 200.00, 1.00, 200.00, '2026-02-01', '水电杂费', NULL, NULL, '2026-02-01 08:00:00')
ON DUPLICATE KEY UPDATE `bill_id` = VALUES(`bill_id`), `fee_name` = VALUES(`fee_name`);

-- 缴费记录
INSERT INTO `fee_payment` (`id`, `payment_no`, `bill_id`, `elderly_id`, `payment_amount`, `payment_method`, `payment_type`, `transaction_no`, `payer_name`, `payer_phone`, `receipt_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
(101, 'PAY202601101', 101, 101, 3345.00, 3, 1, 'WX20260120001', '张伟', '13900139001', 'REC202601101', 1, '1月半月费用-微信支付', '2026-01-20 10:00:00', '2026-01-20 10:00:00'),
(102, 'PAY202602101', 102, 101, 6100.00, 3, 1, 'WX20260205001', '张伟', '13900139001', 'REC202602101', 1, '2月费用-微信支付', '2026-02-05 10:00:00', '2026-02-05 10:00:00')
ON DUPLICATE KEY UPDATE `payment_no` = VALUES(`payment_no`);

-- 发票记录
INSERT INTO `fee_invoice` (`id`, `invoice_no`, `payment_id`, `elderly_id`, `invoice_type`, `invoice_title`, `tax_no`, `invoice_amount`, `invoice_content`, `status`, `invoice_time`, `operator_id`, `remark`, `create_time`, `update_time`) VALUES
(101, 'INV202601101', 101, 101, 1, '张建国', NULL, 3345.00, '养老公寓服务费', 1, '2026-01-20 10:30:00', 10, '1月发票', '2026-01-20 10:30:00', '2026-01-20 10:30:00'),
(102, 'INV202602101', 102, 101, 1, '张建国', NULL, 6100.00, '养老公寓服务费', 1, '2026-02-05 10:30:00', 10, '2月发票', '2026-02-05 10:30:00', '2026-02-05 10:30:00')
ON DUPLICATE KEY UPDATE `invoice_no` = VALUES(`invoice_no`);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 数据关联关系查询示例
-- ============================================

-- 查询张国建的完整信息汇总
SELECT 
    e.name AS '老人姓名',
    e.phone AS '联系电话',
    e.room_number AS '房间号',
    e.health_status AS '健康状况',
    r.price AS '房间价格',
    fa.balance AS '账户余额',
    (SELECT COUNT(*) FROM health_record WHERE elderly_id = 101) AS '健康档案数',
    (SELECT COUNT(*) FROM care_record WHERE elderly_id = 101) AS '护理记录数',
    (SELECT COUNT(*) FROM activity_signup WHERE elderly_id = 101) AS '活动报名数',
    (SELECT COUNT(*) FROM meal_order WHERE elderly_id = 101) AS '订餐记录数',
    (SELECT COUNT(*) FROM fee_bill WHERE elderly_id = 101) AS '费用账单数'
FROM elderly e
LEFT JOIN room r ON e.room_id = r.id
LEFT JOIN fee_account fa ON e.id = fa.elderly_id
WHERE e.id = 101;

-- ============================================
-- 测试数据插入完成
-- ============================================
SELECT '张建国用户关联测试数据集插入完成！' AS message;