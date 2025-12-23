package com.yasp.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaseContract {
    private Long id;                 // 数据库主键
    private String contractNo;       // 合同编号

    private Long bookingId;          // 来源预订单（可空）
    private Long roomId;             // 房间ID
    private Long userId;             // 用户ID

    private LocalDate startDate;     // 起租日
    private LocalDate endDate;       // 到期日

    private Integer rentCent;        // 月租（单位：分）
    private Integer depositCent;     // 押金（单位：分）
    private Integer payCycle;           // 付费周期（1=月付，3=季付）

    private Byte status;             // 状态（0=draft，1=signed，...）
    private LocalDateTime signedAt;  // 签约时间
    private LocalDateTime terminatedAt; // 终止时间
    private String terminateReason;  // 终止原因

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}