package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_payment")
public class FeePayment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paymentNo;

    private Long billId;

    private Integer elderlyId;

    private BigDecimal paymentAmount;

    private Integer paymentMethod;

    private Integer paymentType;

    private String transactionNo;

    private String payerName;

    private String payerPhone;

    private String receiptNo;

    private Integer status;

    private String remark;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String billNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
