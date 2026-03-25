package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_record")
public class BalanceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer elderlyId;

    private BigDecimal amount;

    private String type; // recharge, consume, withdraw, refund

    private String title;

    private String description; // 消费详情描述

    private String paymentMethod;

    private LocalDateTime createTime;
}
