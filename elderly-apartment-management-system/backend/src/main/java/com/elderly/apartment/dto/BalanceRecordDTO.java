package com.elderly.apartment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BalanceRecordDTO {
    private Long id;
    private String title;
    private String description; // 消费详情描述
    private BigDecimal amount;
    private String type; // recharge, consume, withdraw, refund
    private String paymentMethod; // wechat, alipay, balance
    private String createTime;
}
