package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("meal_order")
public class MealOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("elderly_id")
    private Long elderlyId;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("meal_type")
    private Integer mealType;

    @TableField("menu_items")
    private String menuItems;

    @TableField("special_requirements")
    private String specialRequirements;

    @TableField("status")
    private Integer status;

    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("payment_time")
    private LocalDateTime paymentTime;

    @TableField("total_price")
    private java.math.BigDecimal totalPrice;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String realName;

    @TableField(exist = false)
    private String roomNumber;
}
