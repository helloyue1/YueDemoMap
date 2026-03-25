-- 呼叫护工表
CREATE TABLE IF NOT EXISTS `call_caregiver` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `elderly_id` INT DEFAULT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `room_number` VARCHAR(20) DEFAULT NULL COMMENT '房间号',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `call_type` VARCHAR(20) DEFAULT NULL COMMENT '呼叫类型: emergency-紧急呼叫, urgent-紧急, normal-普通, consult-咨询',
  `call_reason` TEXT COMMENT '呼叫原因',
  `urgency_level` TINYINT(1) DEFAULT 2 COMMENT '紧急程度: 1-低, 2-中, 3-高, 4-紧急',
  `assigned_nurse_id` INT DEFAULT NULL COMMENT '分配护士ID',
  `assigned_nurse_name` VARCHAR(50) DEFAULT NULL COMMENT '分配护士姓名',
  `response_time` DATETIME DEFAULT NULL COMMENT '响应时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 1-待响应, 2-已响应, 3-处理中, 4-已完成, 5-已取消',
  `remark` TEXT COMMENT '备注',
  `create_by` INT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`),
  KEY `idx_status` (`status`),
  KEY `idx_assigned_nurse_id` (`assigned_nurse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='呼叫护工表';

-- 插入测试数据
INSERT INTO `call_caregiver` (`elderly_id`, `elderly_name`, `room_number`, `phone`, `call_type`, `call_reason`, `urgency_level`, `status`, `create_time`, `update_time`) VALUES
(1, '张三', '101', '13800138001', 'normal', '需要帮助整理床铺', 2, 1, NOW(), NOW()),
(2, '李四', '102', '13800138002', 'urgent', '身体不适，需要护士检查', 3, 2, NOW(), NOW()),
(3, '王五', '103', '13800138003', 'emergency', '突发状况，需要紧急处理', 4, 4, NOW(), NOW());
