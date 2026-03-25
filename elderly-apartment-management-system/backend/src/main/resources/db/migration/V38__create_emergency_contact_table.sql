-- 紧急联系人表
CREATE TABLE IF NOT EXISTS `emergency_contact` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '联系人名称',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `contact_type` VARCHAR(20) DEFAULT 'other' COMMENT '联系人类型: front_desk-公寓前台, medical-医护人员, security-保安室, management-管理处, other-其他',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述说明',
  `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_contact_type` (`contact_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急联系人表';

-- 插入默认紧急联系人数据
INSERT INTO `emergency_contact` (`name`, `phone`, `contact_type`, `description`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`) VALUES
('公寓前台', '010-12345678', 'front_desk', '24小时服务热线', 1, 1, NOW(), NOW(), 0),
('医护人员', '010-87654321', 'medical', '医疗急救热线', 2, 1, NOW(), NOW(), 0),
('保安室', '010-11223344', 'security', '安全保卫热线', 3, 1, NOW(), NOW(), 0);
