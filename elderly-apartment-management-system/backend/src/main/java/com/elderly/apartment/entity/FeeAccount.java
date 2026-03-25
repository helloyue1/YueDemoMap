package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_account")
public class FeeAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer elderlyId;

    private BigDecimal balance;

    private BigDecimal totalPaid;

    private BigDecimal totalConsumed;

    private Integer status;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String roomNumber;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
