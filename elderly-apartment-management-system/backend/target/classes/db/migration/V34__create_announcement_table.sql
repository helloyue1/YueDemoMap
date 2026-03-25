-- 公告表
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    type TINYINT DEFAULT 1 COMMENT '公告类型：1-普通公告 2-重要公告 3-紧急公告',
    status TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已下架',
    publisher_id BIGINT COMMENT '发布人ID',
    publisher_name VARCHAR(50) COMMENT '发布人姓名',
    publish_time DATETIME COMMENT '发布时间',
    top TINYINT DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
    top_end_time DATETIME COMMENT '置顶结束时间',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_top (top),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 公告阅读记录表
CREATE TABLE IF NOT EXISTS announcement_read (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    announcement_id BIGINT NOT NULL COMMENT '公告ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type TINYINT DEFAULT 1 COMMENT '用户类型：1-管理员 2-护工 3-住客',
    read_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    UNIQUE KEY uk_announcement_user (announcement_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告阅读记录表';
