-- 为订餐记录表添加支付相关字段
-- 使用存储过程来检查列是否存在（兼容MySQL 5.7+）

-- 添加 payment_method 字段
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns 
  WHERE table_schema = DATABASE() AND table_name = 'meal_order' AND column_name = 'payment_method');
SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE meal_order ADD COLUMN payment_method VARCHAR(50) COMMENT ''支付方式：balance-余额 wechat-微信 alipay-支付宝''', 
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 payment_time 字段
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns 
  WHERE table_schema = DATABASE() AND table_name = 'meal_order' AND column_name = 'payment_time');
SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE meal_order ADD COLUMN payment_time DATETIME COMMENT ''支付时间''', 
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 total_price 字段
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns 
  WHERE table_schema = DATABASE() AND table_name = 'meal_order' AND column_name = 'total_price');
SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE meal_order ADD COLUMN total_price DECIMAL(10,2) COMMENT ''订单总价''', 
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新状态字段注释
ALTER TABLE meal_order MODIFY COLUMN status TINYINT DEFAULT 0 COMMENT '状态：0-待支付 1-已支付/待配送 2-已配送 3-已取消';
