-- 检查餐饮订单数据
USE elderly_apartment_management;

-- 1. 查看所有订单数据
SELECT '=== 所有订单数据 ===' AS info;
SELECT 
    id,
    elderly_id,
    order_date,
    meal_type,
    menu_items,
    special_requirements,
    status,
    delivery_time,
    remark,
    create_time
FROM meal_order
ORDER BY order_date DESC, meal_type ASC;

-- 2. 查看今日订单统计（2026-02-23）
SELECT '=== 今日订单统计 (2026-02-23) ===' AS info;
SELECT 
    COUNT(*) AS 今日订单总数,
    SUM(CASE WHEN meal_type = 1 THEN 1 ELSE 0 END) AS 早餐订单,
    SUM(CASE WHEN meal_type = 2 THEN 1 ELSE 0 END) AS 午餐订单,
    SUM(CASE WHEN meal_type = 3 THEN 1 ELSE 0 END) AS 晚餐订单,
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS 待配送,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS 已配送,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS 已取消
FROM meal_order
WHERE order_date = '2026-02-23';

-- 3. 查看所有日期的订单统计
SELECT '=== 各日期订单统计 ===' AS info;
SELECT 
    order_date,
    COUNT(*) AS 订单数,
    SUM(CASE WHEN meal_type = 1 THEN 1 ELSE 0 END) AS 早餐,
    SUM(CASE WHEN meal_type = 2 THEN 1 ELSE 0 END) AS 午餐,
    SUM(CASE WHEN meal_type = 3 THEN 1 ELSE 0 END) AS 晚餐
FROM meal_order
GROUP BY order_date
ORDER BY order_date DESC;

-- 4. 查看订单关联的老人信息
SELECT '=== 订单关联的老人信息 ===' AS info;
SELECT 
    mo.id AS 订单ID,
    mo.order_date AS 订餐日期,
    CASE mo.meal_type 
        WHEN 1 THEN '早餐'
        WHEN 2 THEN '午餐'
        WHEN 3 THEN '晚餐'
    END AS 餐别,
    e.name AS 老人姓名,
    e.room_number AS 房间号,
    mo.menu_items AS 预订菜品,
    mo.special_requirements AS 特殊要求,
    CASE mo.status 
        WHEN 0 THEN '待配送'
        WHEN 1 THEN '已配送'
        WHEN 2 THEN '已取消'
    END AS 状态
FROM meal_order mo
LEFT JOIN elderly e ON mo.elderly_id = e.id
ORDER BY mo.order_date DESC, mo.meal_type ASC;
