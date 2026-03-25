package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_invoice")
public class FeeInvoice implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String invoiceNo;

    private Long paymentId;

    private Integer elderlyId;

    private Integer invoiceType;

    private String invoiceTitle;

    private String taxNo;

    private BigDecimal invoiceAmount;

    private String invoiceContent;

    private Integer status;

    private LocalDateTime invoiceTime;

    private Long operatorId;

    private String remark;

    @TableField(exist = false)
    private String elderlyName;

    @TableField(exist = false)
    private String paymentNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
