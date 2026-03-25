package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_detail")
public class FeeDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long billId;

    private Long feeTypeId;

    private String feeName;

    private BigDecimal amount;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private LocalDate feeDate;

    private String description;

    private Long sourceId;

    private String sourceType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
