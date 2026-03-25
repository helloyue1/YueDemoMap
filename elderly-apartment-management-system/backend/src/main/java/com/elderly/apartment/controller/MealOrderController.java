package com.elderly.apartment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.elderly.apartment.common.Result;
import com.elderly.apartment.entity.MealOrder;
import com.elderly.apartment.entity.User;
import com.elderly.apartment.security.CustomUserDetails;
import com.elderly.apartment.service.MealOrderService;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/meal/order")
public class MealOrderController {

    @Autowired
    private MealOrderService mealOrderService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<IPage<MealOrder>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long elderlyId,
            @RequestParam(required = false) String elderlyName,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String orderDate,
            @RequestParam(required = false) Integer mealType,
            @RequestParam(required = false) Integer status) {
        // 获取当前登录用户
        Long currentUserId = null;
        boolean isAdmin = false;
        boolean isDelivery = false;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            // 使用用户ID（user.id）而不是老人ID（user.elderlyId）
            // 因为订单表中的elderly_id字段存储的是创建订单时的用户ID
            if (user.getId() != null) {
                currentUserId = user.getId().longValue();
            }
            // 检查是否是管理员、员工或配送员角色
            isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_STAFF"));
            isDelivery = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_DELIVERY"));
        }

        // 如果前端传了elderlyId，优先使用前端传的
        // 否则，只有非管理员/非配送员才使用当前登录用户的ID来查询订单
        // 管理员和配送员可以查看所有订单（不传elderlyId条件）
        Long finalElderlyId = elderlyId;
        if (finalElderlyId == null && currentUserId != null && !isAdmin && !isDelivery) {
            finalElderlyId = currentUserId;
        }

        IPage<MealOrder> result = mealOrderService.getOrderList(page, size, finalElderlyId, elderlyName, roomNumber, orderDate, mealType, status);
        return Result.success(result);
    }

    @GetMapping("/date")
    public Result<List<MealOrder>> getByDateAndType(
            @RequestParam String date,
            @RequestParam Integer mealType) {
        return Result.success(mealOrderService.getByDateAndType(LocalDate.parse(date), mealType));
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<List<MealOrder>> getByElderlyId(@PathVariable Long elderlyId) {
        return Result.success(mealOrderService.getByElderlyId(elderlyId));
    }

    @GetMapping("/{id}")
    public Result<MealOrder> getById(@PathVariable Long id) {
        return Result.success(mealOrderService.getOrderById(id));
    }

    @PostMapping
    public Result<Long> save(@RequestBody MealOrder order) {
        order.setStatus(0);  // 0-待支付（创建订单后需要支付）
        
        System.out.println("DEBUG: 创建订单 - elderlyId=" + order.getElderlyId() + ", menuItems=" + order.getMenuItems() + ", totalPrice=" + order.getTotalPrice());
        
        // 如果 totalPrice 为空，尝试从 menuItems 计算
        if (order.getTotalPrice() == null || order.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal calculatedPrice = calculateTotalPrice(order.getMenuItems());
            System.out.println("DEBUG: 计算后的价格=" + calculatedPrice);
            order.setTotalPrice(calculatedPrice);
        }
        
        mealOrderService.save(order);
        System.out.println("DEBUG: 订单已保存 - id=" + order.getId() + ", totalPrice=" + order.getTotalPrice());
        return Result.success(order.getId());
    }
    
    /**
     * 从 menuItems 计算订单总价
     * 支持两种格式：
     * 1. ID数组: [1,2,3]
     * 2. 对象数组: [{"id":4,"name":"全麦馒头","quantity":2,"price":1.5}]
     */
    private BigDecimal calculateTotalPrice(String menuItems) {
        if (menuItems == null || menuItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // 尝试解析为对象数组（App端格式）
            if (menuItems.trim().startsWith("[") && menuItems.contains("\"price\"")) {
                // 对象数组格式
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.List<java.util.Map<String, Object>> items = mapper.readValue(menuItems, 
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {});
                
                BigDecimal total = BigDecimal.ZERO;
                for (java.util.Map<String, Object> item : items) {
                    Object priceObj = item.get("price");
                    Object qtyObj = item.get("quantity");
                    
                    BigDecimal price = BigDecimal.ZERO;
                    int quantity = 1;
                    
                    if (priceObj != null) {
                        if (priceObj instanceof Number) {
                            price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                        } else {
                            price = new BigDecimal(priceObj.toString());
                        }
                    }
                    
                    if (qtyObj != null) {
                        if (qtyObj instanceof Number) {
                            quantity = ((Number) qtyObj).intValue();
                        } else {
                            quantity = Integer.parseInt(qtyObj.toString());
                        }
                    }
                    
                    total = total.add(price.multiply(BigDecimal.valueOf(quantity)));
                }
                return total;
            }
        } catch (Exception e) {
            // 解析失败，返回0
            System.err.println("计算订单总价失败: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    @PostMapping("/{id}/pay")
    public Result<MealOrder> pay(@PathVariable Long id, @RequestBody MealOrder paymentInfo) {
        MealOrder order = mealOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确，无法支付");
        }

        // 获取订单金额
        BigDecimal orderAmount = order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO;
        
        System.out.println("DEBUG: 支付订单 - orderId=" + id + ", orderAmount=" + orderAmount + ", paymentMethod=" + paymentInfo.getPaymentMethod());

        // 如果是余额支付，扣除用户余额
        if ("balance".equals(paymentInfo.getPaymentMethod()) && orderAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 订单中存储的 elderlyId 实际上是 userId
            Integer userId = order.getElderlyId() != null ? order.getElderlyId().intValue() : null;
            if (userId == null) {
                return Result.error("订单未关联用户信息，无法使用余额支付");
            }

            // 直接通过 userId 查询用户信息
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("未找到关联的用户信息，无法使用余额支付");
            }

            // 检查余额是否充足
            BigDecimal currentBalance = userService.getBalance(userId);
            System.out.println("DEBUG: 检查余额 - userId=" + userId + ", currentBalance=" + currentBalance + ", orderAmount=" + orderAmount);
            if (currentBalance.compareTo(orderAmount) < 0) {
                return Result.error("余额不足，请充值或选择其他支付方式");
            }

            // 扣除余额（使用deductBalance方法，记录为消费）
            System.out.println("DEBUG: 扣除余额 - userId=" + userId + ", amount=" + orderAmount);
            // 构建消费描述：订单号 + 餐次类型 + 菜品信息
            String mealTypeStr = getMealTypeText(order.getMealType());
            String description = String.format("订单号：%d | %s | %s", order.getId(), mealTypeStr, order.getMenuItems());
            userService.deductBalance(userId, orderAmount, "订餐消费", description);
        }

        // 更新订单状态为已接单(1)
        order.setStatus(1);
        order.setPaymentMethod(paymentInfo.getPaymentMethod());
        order.setPaymentTime(LocalDateTime.now());
        mealOrderService.updateById(order);
        return Result.success(order);
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody MealOrder order) {
        order.setId(id);
        return Result.success(mealOrderService.updateById(order));
    }

    @PutMapping("/{id}/deliver")
    public Result<Boolean> deliver(@PathVariable Long id) {
        MealOrder order = mealOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确，无法配送");
        }
        order.setStatus(2);  // 2-配送中
        order.setDeliveryTime(LocalDateTime.now());
        return Result.success(mealOrderService.updateById(order));
    }

    @PutMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id) {
        MealOrder order = mealOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error("订单状态不正确，无法完成配送");
        }
        order.setStatus(4);  // 4-已送达
        return Result.success(mealOrderService.updateById(order));
    }

    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id) {
        MealOrder order = new MealOrder();
        order.setId(id);
        order.setStatus(3);  // 3-已取消
        return Result.success(mealOrderService.updateById(order));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(mealOrderService.removeById(id));
    }

    /**
     * 获取餐次类型文本
     */
    private String getMealTypeText(Integer mealType) {
        if (mealType == null) return "未知餐次";
        switch (mealType) {
            case 1: return "早餐";
            case 2: return "午餐";
            case 3: return "晚餐";
            default: return "其他";
        }
    }
}
