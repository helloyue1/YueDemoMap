-- 插入配送员角色（如果不存在则插入，存在则忽略）
INSERT IGNORE INTO `role` (`name`, `code`, `description`, `create_time`, `update_time`) VALUES
('配送员', 'DELIVERY', '配送员角色，负责餐饮配送和物品送达', NOW(), NOW());
