package com.elderly.apartment.dto;

import lombok.Data;

@Data
public class RoomStatsDTO {
    private Integer total;        // 总房间数
    private Integer empty;        // 空闲房间数
    private Integer partial;      // 部分入住
    private Integer full;         // 已满房间数
    private Integer maintenance;  // 维修中房间数
}
