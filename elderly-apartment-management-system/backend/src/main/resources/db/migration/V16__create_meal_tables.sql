-- 餐饮管理模块数据库表

-- 菜品表
CREATE TABLE IF NOT EXISTS meal_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜品ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '菜品类型：1-早餐 2-午餐 3-晚餐 4-通用',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    ingredients VARCHAR(500) COMMENT '主要食材',
    nutrition VARCHAR(500) COMMENT '营养成分',
    suitable VARCHAR(500) COMMENT '适宜人群（如：糖尿病患者、高血压患者等）',
    image VARCHAR(500) COMMENT '菜品图片',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 每周食谱表
CREATE TABLE IF NOT EXISTS meal_weekly_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '计划ID',
    plan_date DATE NOT NULL COMMENT '计划日期',
    meal_type TINYINT NOT NULL COMMENT '餐别：1-早餐 2-午餐 3-晚餐',
    menu_items VARCHAR(1000) NOT NULL COMMENT '菜品列表（JSON格式存储菜品ID数组）',
    menu_names VARCHAR(1000) COMMENT '菜品名称（用于展示）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-草稿 1-已发布',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_date_type (plan_date, meal_type),
    INDEX idx_plan_date (plan_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每周食谱表';

-- 订餐记录表
CREATE TABLE IF NOT EXISTS meal_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    elderly_id BIGINT NOT NULL COMMENT '老人ID',
    order_date DATE NOT NULL COMMENT '订餐日期',
    meal_type TINYINT NOT NULL COMMENT '餐别：1-早餐 2-午餐 3-晚餐',
    menu_items VARCHAR(1000) COMMENT '预订菜品（JSON格式）',
    special_requirements VARCHAR(500) COMMENT '特殊要求（如：少盐、软烂等）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待配送 1-已配送 2-已取消',
    delivery_time DATETIME COMMENT '实际配送时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_order_date (order_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订餐记录表';

-- 送餐记录表
CREATE TABLE IF NOT EXISTS meal_delivery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    elderly_id BIGINT NOT NULL COMMENT '老人ID',
    delivery_date DATE NOT NULL COMMENT '送餐日期',
    meal_type TINYINT NOT NULL COMMENT '餐别：1-早餐 2-午餐 3-晚餐',
    delivery_status TINYINT DEFAULT 0 COMMENT '送餐状态：0-待送 1-已送 2-拒收',
    delivery_time DATETIME COMMENT '实际送餐时间',
    receiver VARCHAR(100) COMMENT '接收人',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_delivery_date (delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='送餐记录表';

-- 插入示例菜品数据
INSERT INTO meal_menu (name, type, price, ingredients, nutrition, suitable, status, create_time) VALUES
('小米粥', 1, 3.00, '小米、水', '碳水化合物、维生素B', '适合所有老人，易消化', 1, NOW()),
('鸡蛋', 1, 2.00, '鸡蛋', '蛋白质、维生素D', '适合所有老人', 1, NOW()),
('豆浆', 1, 2.50, '黄豆、水', '植物蛋白、钙', '适合所有老人', 1, NOW()),
('馒头', 1, 1.50, '面粉', '碳水化合物', '适合所有老人', 1, NOW()),
('清炒时蔬', 4, 8.00, '当季蔬菜', '维生素、膳食纤维', '适合所有老人', 1, NOW()),
('红烧肉', 2, 25.00, '猪肉、酱油、糖', '蛋白质、脂肪', '适合身体健康的老人', 1, NOW()),
('清蒸鱼', 2, 28.00, '鲈鱼、葱、姜', '优质蛋白、不饱和脂肪酸', '适合所有老人，易消化', 1, NOW()),
('番茄炒蛋', 2, 12.00, '番茄、鸡蛋', '蛋白质、维生素C', '适合所有老人', 1, NOW()),
('紫菜蛋花汤', 4, 6.00, '紫菜、鸡蛋', '碘、蛋白质', '适合所有老人', 1, NOW()),
('白米饭', 4, 2.00, '大米', '碳水化合物', '适合所有老人', 1, NOW()),
('杂粮粥', 3, 5.00, '小米、燕麦、红豆', '膳食纤维、B族维生素', '适合糖尿病患者', 1, NOW()),
('蒸南瓜', 3, 6.00, '南瓜', '胡萝卜素、膳食纤维', '适合所有老人', 1, NOW()),
('豆腐脑', 1, 4.00, '黄豆', '植物蛋白、钙', '适合所有老人', 1, NOW()),
('面条', 2, 8.00, '面粉、青菜', '碳水化合物、维生素', '适合所有老人', 1, NOW()),
('馄饨', 2, 15.00, '面粉、猪肉', '碳水化合物、蛋白质', '适合所有老人', 1, NOW());
