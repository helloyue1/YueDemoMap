-- 删除已存在的表（如果结构不完整）
DROP TABLE IF EXISTS `care_booking`;

-- 重新创建护理预约表
CREATE TABLE `care_booking` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
    `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
    `room_number` VARCHAR(20) DEFAULT NULL COMMENT '房间号',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `service_id` BIGINT DEFAULT NULL COMMENT '服务项ID',
    `service_name` VARCHAR(100) DEFAULT NULL COMMENT '服务名称',
    `service_type` VARCHAR(50) DEFAULT NULL COMMENT '服务类型',
    `appointment_date` DATE DEFAULT NULL COMMENT '预约日期',
    `appointment_time` VARCHAR(50) DEFAULT NULL COMMENT '预约时段',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    `payment_status` VARCHAR(20) DEFAULT 'pending' COMMENT '支付状态：pending-待支付，paid-已支付，refunded-已退款',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态：pending-待确认，confirmed-已确认，in_progress-进行中，completed-已完成，cancelled-已取消',
    `nurse_id` BIGINT DEFAULT NULL COMMENT '护理员ID',
    `nurse_name` VARCHAR(50) DEFAULT NULL COMMENT '护理员姓名',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `is_package` TINYINT(1) DEFAULT 0 COMMENT '是否套餐：0-否，1-是',
    `family_name` VARCHAR(50) DEFAULT NULL COMMENT '家属姓名',
    `family_phone` VARCHAR(20) DEFAULT NULL COMMENT '家属电话',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_elderly_id` (`elderly_id`),
    KEY `idx_service_id` (`service_id`),
    KEY `idx_nurse_id` (`nurse_id`),
    KEY `idx_status` (`status`),
    KEY `idx_appointment_date` (`appointment_date`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理预约表';
