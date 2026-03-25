package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_standard")
public class FeeStandard implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long feeTypeId;

    private String name;

    private BigDecimal amount;

    private Integer roomType;

    private Integer careLevel;

    private String description;

    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    private Integer status;

    @TableField(exist = false)
    private String feeTypeName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
