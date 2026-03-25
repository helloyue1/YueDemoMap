-- 轮播图表
CREATE TABLE IF NOT EXISTS banner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '轮播图ID',
    title VARCHAR(200) NOT NULL COMMENT '轮播图标题',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    link_url VARCHAR(500) COMMENT '跳转链接',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    position VARCHAR(50) DEFAULT 'home' COMMENT '位置：home-首页',
    description VARCHAR(500) COMMENT '描述',
    create_by BIGINT COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_position (position),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 插入默认轮播图数据
INSERT INTO banner (title, image_url, link_url, sort_order, status, position, description) VALUES
('元宵快乐', '/uploads/banner/banner1.jpg', '', 1, 1, 'home', '团圆美满 幸福安康'),
('春季养生', '/uploads/banner/banner2.jpg', '', 2, 1, 'home', '健康从饮食开始'),
('健康讲座', '/uploads/banner/banner3.jpg', '', 3, 1, 'home', '每周三下午2点，专家讲座');
