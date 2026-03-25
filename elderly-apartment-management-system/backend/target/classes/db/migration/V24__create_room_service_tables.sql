-- 房间服务申请表
CREATE TABLE room_service_request (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    request_no VARCHAR(32) NOT NULL COMMENT '申请编号',
    room_id INT NOT NULL COMMENT '房间ID',
    elderly_id INT COMMENT '老人ID',
    user_id INT COMMENT '申请人用户ID',
    service_type VARCHAR(20) NOT NULL COMMENT '服务类型: CLEANING-保洁, REPAIR-维修, MAINTENANCE-设备维护, OTHER-其他',
    service_subtype VARCHAR(50) COMMENT '服务子类型',
    description TEXT NOT NULL COMMENT '服务描述',
    urgency VARCHAR(10) DEFAULT 'NORMAL' COMMENT '紧急程度: URGENT-紧急, NORMAL-一般, LOW-常规',
    preferred_time VARCHAR(50) COMMENT '期望服务时间',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待处理, ASSIGNED-已分配, IN_PROGRESS-处理中, COMPLETED-已完成, CANCELLED-已取消',
    handler_id INT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名',
    handle_notes TEXT COMMENT '处理备注',
    handle_time DATETIME COMMENT '处理时间',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    INDEX idx_room_id (room_id),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_service_type (service_type),
    INDEX idx_create_time (create_time)
) COMMENT '房间服务申请表';

-- 房间服务评价表
CREATE TABLE room_service_evaluation (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    request_id INT NOT NULL COMMENT '服务申请ID',
    room_id INT NOT NULL COMMENT '房间ID',
    elderly_id INT COMMENT '老人ID',
    user_id INT COMMENT '评价人用户ID',
    rating INT NOT NULL COMMENT '评分: 1-5星',
    content TEXT COMMENT '评价内容',
    tags VARCHAR(255) COMMENT '评价标签,逗号分隔',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名: 0-否, 1-是',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uk_request_id (request_id),
    INDEX idx_room_id (room_id),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_rating (rating),
    FOREIGN KEY (request_id) REFERENCES room_service_request(id)
) COMMENT '房间服务评价表';

-- 房间服务统计视图
CREATE VIEW room_service_stats AS
SELECT
    room_id,
    COUNT(*) as total_requests,
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN service_type = 'CLEANING' THEN 1 ELSE 0 END) as cleaning_count,
    SUM(CASE WHEN service_type = 'REPAIR' THEN 1 ELSE 0 END) as repair_count,
    SUM(CASE WHEN service_type = 'MAINTENANCE' THEN 1 ELSE 0 END) as maintenance_count,
    AVG(CASE WHEN status = 'COMPLETED' THEN DATEDIFF(complete_time, create_time) END) as avg_handle_days
FROM room_service_request
WHERE deleted = 0
GROUP BY room_id;

-- 插入测试数据
INSERT INTO room_service_request (request_no, room_id, elderly_id, service_type, service_subtype, description, urgency, preferred_time, status, handler_name, handle_notes, handle_time, complete_time, create_time, update_time) VALUES
('RS202502250001', 1, 1, 'CLEANING', '日常保洁', '房间全面清洁，包括地面、桌面、卫生间', 'NORMAL', '10:00-12:00', 'COMPLETED', '李阿姨', '已完成房间清洁，老人表示满意', '2025-02-20 10:30:00', '2025-02-20 11:30:00', '2025-02-20 09:00:00', '2025-02-20 11:30:00'),
('RS202502250002', 1, 1, 'REPAIR', '水电维修', '卫生间水龙头漏水', 'URGENT', '14:00-16:00', 'COMPLETED', '王师傅', '已更换水龙头密封圈', '2025-02-18 14:20:00', '2025-02-18 15:00:00', '2025-02-18 13:00:00', '2025-02-18 15:00:00'),
('RS202502250003', 2, 2, 'MAINTENANCE', '空调维护', '空调制冷效果不好，需要清洗', 'NORMAL', '09:00-11:00', 'IN_PROGRESS', '张师傅', '正在处理中', '2025-02-24 09:00:00', NULL, '2025-02-23 16:00:00', '2025-02-24 09:00:00'),
('RS202502250004', 1, 1, 'CLEANING', '深度清洁', '申请房间深度清洁服务', 'LOW', '14:00-16:00', 'PENDING', NULL, NULL, NULL, NULL, '2025-02-25 08:00:00', '2025-02-25 08:00:00'),
('RS202502250005', 3, 3, 'REPAIR', '电器维修', '电视机遥控器失灵', 'NORMAL', '10:00-12:00', 'ASSIGNED', '刘师傅', NULL, NULL, NULL, '2025-02-24 15:30:00', '2025-02-24 15:30:00');

INSERT INTO room_service_evaluation (request_id, room_id, elderly_id, rating, content, tags, create_time, update_time) VALUES
(1, 1, 1, 5, '李阿姨打扫得很干净，态度也很好！', '服务态度好,打扫干净', '2025-02-20 14:00:00', '2025-02-20 14:00:00'),
(2, 1, 1, 5, '维修师傅响应很快，问题解决得很彻底', '响应及时,技术专业', '2025-02-18 16:00:00', '2025-02-18 16:00:00');
