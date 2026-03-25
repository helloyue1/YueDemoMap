-- 删除 meal_order 表的唯一索引，允许同一老人同一天同一餐次下多个订单
-- 先检查索引是否存在，如果存在则删除

SET @table_name = 'meal_order';
SET @index_name = 'uk_elderly_date_type';

SET @sql = IF(
  EXISTS(
    SELECT 1 FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = @table_name 
    AND INDEX_NAME = @index_name
  ),
  CONCAT('ALTER TABLE ', @table_name, ' DROP INDEX ', @index_name),
  'SELECT "Index does not exist" as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
