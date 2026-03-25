package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 老人账号关联实体
 */
@Data
@TableName("elderly_user")
public class ElderlyUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer elderlyId;

    private Integer userId;

    private String username;

    private String passwordHint;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
