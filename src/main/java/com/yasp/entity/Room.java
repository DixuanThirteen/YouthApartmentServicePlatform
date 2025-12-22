package com.yasp.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Room {
    private Long id;

    private Long apartmentId;
    private Long roomTypeId;

    private String buildingNo;
    private String unitNo;
    private Integer floorNo;
    private String roomNo;
    private String displayName;

    // generated columns (stored) - 通常不需要插入/更新，但可以保留用于查询展示
    private String buildingNoNorm;
    private String unitNoNorm;
    private String roomNoNorm;

    private BigDecimal areaSqm;
    private Integer orientation;

    private Integer rentCent;
    private Integer depositCent;

    private Integer status;
    private Integer rentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String roomCode;
}