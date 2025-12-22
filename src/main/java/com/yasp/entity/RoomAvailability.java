package com.yasp.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RoomAvailability {

    private Long id;                // 占用记录 ID
    private Long roomId;            // 房间 ID
    private LocalDate occupiedFrom; // 占用开始日期
    private LocalDate occupiedTo;   // 占用结束日期
    private String source;          // 占用来源（booking / contract）
    private Long sourceId;          // 来源 ID（对应 room_bookings 或 lease_contracts 的主键）
    private LocalDateTime createdAt;// 创建时间
    private LocalDateTime updatedAt;// 更新时间
}