package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_bill")
public class FeeBill implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String billNo;

    private Integer elderlyId;

    private String billMonth;

    private LocalDate billStartDate;

    private LocalDate billEndDate;

    private BigDecimal roomFee;

    private BigDecimal careFee;

    private BigDecimal mealFee;

    private BigDecimal medicalFee;

    private BigDecimal otherFee;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal payableAmount;

    private BigDecimal paidAmount;

    private Integer status;

    private LocalDate dueDate;

    private LocalDateTime paidTime;

    private String remark;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String roomNumber;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
