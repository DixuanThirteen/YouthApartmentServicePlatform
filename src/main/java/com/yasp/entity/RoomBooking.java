package com.yasp.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RoomBooking {

    private Long id; // 预订记录 ID
    private String bookingNo; // 预订编号

    private Long roomId; // 房间 ID
    private Long userId; // 用户 ID

    private LocalDate startDate; // 计划入住日期
    private Integer leaseMonths; // 计划租期(月)
    private Integer rentCent; // 下单时的月租（分）
    private Integer depositCent; // 下单时的押金（分）

    private Byte status; // 预订状态
    private LocalDateTime holdUntil; // 锁房到期时间

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}
