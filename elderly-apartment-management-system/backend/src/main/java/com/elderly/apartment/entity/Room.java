package com.elderly.apartment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("room")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("room_number")
    private String roomNumber;

    @TableField("floor")
    private Integer floor;

    @TableField("type")
    private Integer type;

    @TableField("capacity")
    private Integer capacity;

    @TableField("current_occupancy")
    private Integer currentOccupancy;

    @TableField("status")
    private Integer status;

    @TableField("price")
    private BigDecimal price;

    @TableField("has_wifi")
    private Boolean hasWifi;

    @TableField("has_tv")
    private Boolean hasTv;

    @TableField("has_ac")
    private Boolean hasAc;

    @TableField("has_bathroom")
    private Boolean hasBathroom;

    @TableField("has_balcony")
    private Boolean hasBalcony;

    @TableField("facilities")
    private String facilities;

    @TableField("cover_image")
    private String coverImage;

    @TableField("images")
    private String images;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
