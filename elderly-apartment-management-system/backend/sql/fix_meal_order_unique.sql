-- 检查并删除 meal_order 表的唯一索引（如果存在）
-- 这个索引可能导致同一老人同一天同一餐次只能下一个订单

-- 查看表的所有索引
SHOW INDEX FROM meal_order;

-- 如果存在 uk_elderly_date_type 或类似的唯一索引，删除它
-- ALTER TABLE meal_order DROP INDEX uk_elderly_date_type;

-- 或者，如果希望保持一天一餐只能一个订单的逻辑，可以改为更新现有订单而不是报错
-- 这需要修改业务逻辑，而不是数据库结构
