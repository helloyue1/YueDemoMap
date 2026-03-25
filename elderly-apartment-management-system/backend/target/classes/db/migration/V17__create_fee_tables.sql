-- ============================================
-- 费用管理模块数据库表
-- ============================================

-- 费用类型表
CREATE TABLE IF NOT EXISTS fee_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '费用类型ID',
    name VARCHAR(50) NOT NULL COMMENT '类型名称（如：住宿费、护理费、餐饮费）',
    code VARCHAR(50) NOT NULL COMMENT '类型编码',
    description VARCHAR(255) COMMENT '类型说明',
    is_fixed TINYINT DEFAULT 0 COMMENT '是否固定费用：0-否 1-是',
    billing_cycle TINYINT DEFAULT 1 COMMENT '计费周期：1-月 2-季度 3-年',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用类型表';

-- 费用标准表（用于设置各类费用的收费标准）
CREATE TABLE IF NOT EXISTS fee_standard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标准ID',
    fee_type_id BIGINT NOT NULL COMMENT '费用类型ID',
    name VARCHAR(100) NOT NULL COMMENT '标准名称',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    room_type TINYINT COMMENT '适用房间类型：1-单人间 2-双人间 3-套房（住宿费用）',
    care_level TINYINT COMMENT '护理等级：1-一级 2-二级 3-三级（护理费用）',
    description VARCHAR(500) COMMENT '说明',
    effective_date DATE NOT NULL COMMENT '生效日期',
    expiry_date DATE COMMENT '失效日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失效 1-生效',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_fee_type_id (fee_type_id),
    INDEX idx_status (status),
    INDEX idx_effective_date (effective_date),
    CONSTRAINT fk_fee_standard_type FOREIGN KEY (fee_type_id) REFERENCES fee_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用标准表';

-- 老人费用账户表
CREATE TABLE IF NOT EXISTS fee_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账户ID',
    elderly_id INT NOT NULL COMMENT '老人ID',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    total_paid DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计缴费',
    total_consumed DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费',
    status TINYINT DEFAULT 1 COMMENT '状态：0-冻结 1-正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_elderly_id (elderly_id),
    INDEX idx_status (status),
    CONSTRAINT fk_fee_account_elderly FOREIGN KEY (elderly_id) REFERENCES elderly(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人费用账户表';

-- 费用账单表
CREATE TABLE IF NOT EXISTS fee_bill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账单ID',
    bill_no VARCHAR(50) NOT NULL COMMENT '账单编号',
    elderly_id INT NOT NULL COMMENT '老人ID',
    bill_month VARCHAR(7) NOT NULL COMMENT '账单月份（格式：2024-01）',
    bill_start_date DATE NOT NULL COMMENT '账单开始日期',
    bill_end_date DATE NOT NULL COMMENT '账单结束日期',
    room_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '住宿费',
    care_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '护理费',
    meal_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '餐饮费',
    medical_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '医疗费',
    other_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '其他费用',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '账单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    payable_amount DECIMAL(10,2) NOT NULL COMMENT '应付金额',
    paid_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '已付金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待缴费 1-部分缴费 2-已缴清 3-已逾期',
    due_date DATE NOT NULL COMMENT '缴费截止日期',
    paid_time DATETIME COMMENT '实际缴费时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_bill_no (bill_no),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_bill_month (bill_month),
    INDEX idx_status (status),
    INDEX idx_due_date (due_date),
    CONSTRAINT fk_fee_bill_elderly FOREIGN KEY (elderly_id) REFERENCES elderly(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用账单表';

-- 费用明细表
CREATE TABLE IF NOT EXISTS fee_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    fee_type_id BIGINT NOT NULL COMMENT '费用类型ID',
    fee_name VARCHAR(100) NOT NULL COMMENT '费用名称',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    quantity DECIMAL(10,2) DEFAULT 1.00 COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    fee_date DATE COMMENT '费用发生日期',
    description VARCHAR(500) COMMENT '费用说明',
    source_id BIGINT COMMENT '来源ID（如订餐ID、用药记录ID等）',
    source_type VARCHAR(50) COMMENT '来源类型',
    create_time DATETIME COMMENT '创建时间',
    INDEX idx_bill_id (bill_id),
    INDEX idx_fee_type_id (fee_type_id),
    INDEX idx_fee_date (fee_date),
    CONSTRAINT fk_fee_detail_bill FOREIGN KEY (bill_id) REFERENCES fee_bill(id),
    CONSTRAINT fk_fee_detail_type FOREIGN KEY (fee_type_id) REFERENCES fee_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用明细表';

-- 缴费记录表
CREATE TABLE IF NOT EXISTS fee_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '缴费ID',
    payment_no VARCHAR(50) NOT NULL COMMENT '缴费单号',
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    elderly_id INT NOT NULL COMMENT '老人ID',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    payment_method TINYINT NOT NULL COMMENT '缴费方式：1-现金 2-银行卡 3-微信 4-支付宝 5-其他',
    payment_type TINYINT DEFAULT 1 COMMENT '缴费类型：1-正常缴费 2-预存 3-退款',
    transaction_no VARCHAR(100) COMMENT '交易流水号（第三方支付）',
    payer_name VARCHAR(50) COMMENT '缴费人姓名',
    payer_phone VARCHAR(20) COMMENT '缴费人电话',
    receipt_no VARCHAR(50) COMMENT '收据编号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-作废 1-正常',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_payment_no (payment_no),
    INDEX idx_bill_id (bill_id),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_payment_method (payment_method),
    INDEX idx_create_time (create_time),
    CONSTRAINT fk_fee_payment_bill FOREIGN KEY (bill_id) REFERENCES fee_bill(id),
    CONSTRAINT fk_fee_payment_elderly FOREIGN KEY (elderly_id) REFERENCES elderly(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';

-- 发票记录表
CREATE TABLE IF NOT EXISTS fee_invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '发票ID',
    invoice_no VARCHAR(50) NOT NULL COMMENT '发票编号',
    payment_id BIGINT NOT NULL COMMENT '缴费记录ID',
    elderly_id INT NOT NULL COMMENT '老人ID',
    invoice_type TINYINT NOT NULL COMMENT '发票类型：1-普通发票 2-增值税发票',
    invoice_title VARCHAR(200) COMMENT '发票抬头',
    tax_no VARCHAR(50) COMMENT '税号',
    invoice_amount DECIMAL(10,2) NOT NULL COMMENT '发票金额',
    invoice_content VARCHAR(500) COMMENT '发票内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待开具 1-已开具 2-已作废',
    invoice_time DATETIME COMMENT '开票时间',
    operator_id BIGINT COMMENT '操作人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_invoice_no (invoice_no),
    INDEX idx_payment_id (payment_id),
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_status (status),
    CONSTRAINT fk_fee_invoice_payment FOREIGN KEY (payment_id) REFERENCES fee_payment(id),
    CONSTRAINT fk_fee_invoice_elderly FOREIGN KEY (elderly_id) REFERENCES elderly(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票记录表';

-- 插入默认费用类型数据（如果不存在）
INSERT IGNORE INTO fee_type (name, code, description, is_fixed, billing_cycle, status, create_time) VALUES
('住宿费', 'ROOM_FEE', '房间住宿费用', 1, 1, 1, NOW()),
('护理费', 'CARE_FEE', '日常护理服务费用', 1, 1, 1, NOW()),
('餐饮费', 'MEAL_FEE', '餐食费用', 0, 1, 1, NOW()),
('医疗费', 'MEDICAL_FEE', '医疗服务费用', 0, 1, 1, NOW()),
('水电费', 'UTILITY_FEE', '水电气费用', 0, 1, 1, NOW()),
('其他费用', 'OTHER_FEE', '其他杂费', 0, 1, 1, NOW());

-- 插入默认费用标准数据（如果不存在）
INSERT IGNORE INTO fee_standard (fee_type_id, name, amount, room_type, description, effective_date, status, create_time) VALUES
(1, '单人间住宿费', 3000.00, 1, '单人间每月住宿费用', '2024-01-01', 1, NOW()),
(1, '双人间住宿费', 2000.00, 2, '双人间每月住宿费用', '2024-01-01', 1, NOW()),
(1, '套房住宿费', 5000.00, 3, '套房每月住宿费用', '2024-01-01', 1, NOW()),
(2, '一级护理费', 1500.00, NULL, '基础护理服务', '2024-01-01', 1, NOW()),
(2, '二级护理费', 2500.00, NULL, '中级护理服务', '2024-01-01', 1, NOW()),
(2, '三级护理费', 4000.00, NULL, '高级护理服务', '2024-01-01', 1, NOW());

-- 插入测试数据 - 先插入老人数据（如果不存在）
INSERT IGNORE INTO elderly (id, name, gender, birthday, id_card, phone, address, health_status, emergency_contact, emergency_phone, room_id, check_in_time, status, notes, create_time, update_time) VALUES
(1, '张大爷', 1, '1945-03-15', '110101194503151234', '13800138001', '北京市朝阳区', '高血压、糖尿病', '张小明', '13900139001', 1, '2024-01-15 10:00:00', 1, '', NOW(), NOW()),
(2, '李奶奶', 2, '1948-07-20', '110101194807201234', '13800138002', '北京市海淀区', '心脏病', '李小红', '13900139002', 2, '2024-02-01 14:00:00', 1, '', NOW(), NOW()),
(3, '王爷爷', 1, '1942-11-08', '110101194211081234', '13800138003', '北京市西城区', '关节炎', '王大伟', '13900139003', 3, '2024-01-20 09:00:00', 1, '', NOW(), NOW()),
(4, '赵奶奶', 2, '1950-05-12', '110101195005121234', '13800138004', '北京市东城区', '高血压', '赵小丽', '13900139004', 4, '2024-03-01 15:00:00', 1, '', NOW(), NOW()),
(5, '刘大爷', 1, '1943-09-25', '110101194309251234', '13800138005', '北京市丰台区', '糖尿病', '刘小强', '13900139005', 5, '2024-02-15 11:00:00', 1, '', NOW(), NOW());

-- 老人费用账户
INSERT IGNORE INTO fee_account (elderly_id, balance, total_paid, total_consumed, status, create_time, update_time) VALUES
(1, 5000.00, 15000.00, 10000.00, 1, NOW(), NOW()),
(2, 2000.00, 12000.00, 10000.00, 1, NOW(), NOW()),
(3, 0.00, 10000.00, 10000.00, 1, NOW(), NOW()),
(4, -1500.00, 8500.00, 10000.00, 1, NOW(), NOW()),
(5, 8000.00, 18000.00, 10000.00, 1, NOW(), NOW());

-- 费用账单数据
INSERT IGNORE INTO fee_bill (bill_no, elderly_id, bill_month, bill_start_date, bill_end_date, room_fee, care_fee, meal_fee, medical_fee, other_fee, total_amount, discount_amount, payable_amount, paid_amount, status, due_date, remark, create_time, update_time) VALUES
('BILL202502001', 1, '2025-02', '2025-02-01', '2025-02-28', 3000.00, 1500.00, 1200.00, 0.00, 200.00, 5900.00, 0.00, 5900.00, 5900.00, 2, '2025-02-10', '单人间+一级护理', NOW(), NOW()),
('BILL202502002', 2, '2025-02', '2025-02-01', '2025-02-28', 2000.00, 2500.00, 1200.00, 300.00, 100.00, 6100.00, 100.00, 6000.00, 6000.00, 2, '2025-02-10', '双人间+二级护理', NOW(), NOW()),
('BILL202502003', 3, '2025-02', '2025-02-01', '2025-02-28', 2000.00, 1500.00, 1200.00, 0.00, 0.00, 4700.00, 0.00, 4700.00, 3000.00, 1, '2025-02-10', '部分缴费', NOW(), NOW()),
('BILL202502004', 4, '2025-02', '2025-02-01', '2025-02-28', 5000.00, 4000.00, 1200.00, 500.00, 300.00, 11000.00, 0.00, 11000.00, 0.00, 0, '2025-02-10', '待缴费', NOW(), NOW()),
('BILL202502005', 5, '2025-02', '2025-02-01', '2025-02-28', 3000.00, 2500.00, 1200.00, 0.00, 0.00, 6700.00, 200.00, 6500.00, 6500.00, 2, '2025-02-10', '单人间+二级护理', NOW(), NOW()),
('BILL202501001', 1, '2025-01', '2025-01-01', '2025-01-31', 3000.00, 1500.00, 1200.00, 0.00, 200.00, 5900.00, 0.00, 5900.00, 5900.00, 2, '2025-01-10', '1月账单', NOW(), NOW()),
('BILL202501002', 2, '2025-01', '2025-01-01', '2025-01-31', 2000.00, 2500.00, 1200.00, 200.00, 100.00, 6000.00, 0.00, 6000.00, 6000.00, 2, '2025-01-10', '1月账单', NOW(), NOW());

-- 缴费记录数据
INSERT IGNORE INTO fee_payment (payment_no, bill_id, elderly_id, payment_amount, payment_method, payment_type, transaction_no, payer_name, payer_phone, receipt_no, status, remark, create_time, update_time) VALUES
('PAY202502001', 1, 1, 5900.00, 3, 1, 'WX20250201001', '张三', '13800138001', 'REC001', 1, '微信支付', NOW(), NOW()),
('PAY202502002', 2, 2, 6000.00, 2, 1, 'BK20250201002', '李四', '13800138002', 'REC002', 1, '银行转账', NOW(), NOW()),
('PAY202502003', 3, 3, 3000.00, 1, 1, NULL, '王五', '13800138003', 'REC003', 1, '现金缴费', NOW(), NOW()),
('PAY202502004', 5, 5, 6500.00, 4, 1, 'ZFB20250201004', '赵六', '13800138005', 'REC004', 1, '支付宝', NOW(), NOW()),
('PAY202501001', 6, 1, 5900.00, 3, 1, 'WX20250101001', '张三', '13800138001', 'REC005', 1, '1月缴费', NOW(), NOW()),
('PAY202501002', 7, 2, 6000.00, 2, 1, 'BK20250101002', '李四', '13800138002', 'REC006', 1, '1月缴费', NOW(), NOW());
