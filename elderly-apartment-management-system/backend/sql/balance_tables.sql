-- 余额功能数据库表结构

-- 1. 在 elderly 表中添加 balance 字段（如果不存在）
SET @dbname = DATABASE();
SET @tablename = "elderly";
SET @columnname = "balance";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = @tablename
    AND COLUMN_NAME = @columnname
  ) > 0,
  "SELECT 'Column already exists'",
  "ALTER TABLE elderly ADD COLUMN balance DECIMAL(10, 2) DEFAULT 0.00 COMMENT '账户余额' AFTER deleted"
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. 创建余额交易记录表
CREATE TABLE IF NOT EXISTS balance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    elderly_id INT NOT NULL COMMENT '老人ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '交易金额',
    type VARCHAR(20) NOT NULL COMMENT '交易类型: recharge-充值, consume-消费, withdraw-提现, refund-退款',
    title VARCHAR(100) NOT NULL COMMENT '交易标题',
    payment_method VARCHAR(20) DEFAULT NULL COMMENT '支付方式: wechat-微信, alipay-支付宝, balance-余额',
    order_id BIGINT DEFAULT NULL COMMENT '关联订单ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_elderly_id (elderly_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额交易记录表';

-- 3. 初始化现有老人的余额为0
UPDATE elderly SET balance = 0.00 WHERE balance IS NULL;
