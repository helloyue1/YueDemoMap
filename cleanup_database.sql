-- ============================================
-- 养老公寓管理系统数据库清理脚本
-- 删除不需要的空表
-- ============================================

USE elderly_apartment_management;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除空表（无数据）
-- 这些表当前都没有数据，可以安全删除

-- 1. 活动提醒表 - 0条数据
DROP TABLE IF EXISTS `activity_reminder`;

-- 2. 活动统计表 - 0条数据
DROP TABLE IF EXISTS `activity_statistics`;

-- 3. 老人餐饮计划关联表 - 0条数据
DROP TABLE IF EXISTS `elderly_meal_plan`;

-- 4. 旧版费用记录表 - 0条数据（新版使用fee_bill等表）
DROP TABLE IF EXISTS `fee_record`;

-- 5. 护士排班表 - 0条数据（新版使用schedule表）
DROP TABLE IF EXISTS `nurse_schedule`;

-- 6. 药品发放表 - 0条数据（无实体类）
DROP TABLE IF EXISTS `medicine_distribution`;

-- 7. 任务表 - 0条数据（无实体类）
DROP TABLE IF EXISTS `task`;

-- 8. 出入记录表 - 0条数据（无实体类）
DROP TABLE IF EXISTS `access_record`;

-- 9. 紧急呼叫表 - 0条数据（无实体类）
DROP TABLE IF EXISTS `emergency_call`;

-- 10. 健康预警表 - 0条数据（无实体类）
DROP TABLE IF EXISTS `health_alert`;

-- 11. 健康档案表 - 0条数据（无实体类）
DROP TABLE IF EXISTS `health_archive`;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示清理后的表列表
SHOW TABLES;
