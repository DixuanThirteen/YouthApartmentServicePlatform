package com.yasp.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Payments {
    private Long id;                  // 数据库主键
    private String paymentId;         // 支付交易编号
    private Long bookingId;           // 关联的预订单 ID
    private int amountCent;           // 支付金额（分）
    private String status;            // 支付状态
    private String gatewayResponse;   // 支付网关返回信息
    private String transactionId;     // 网关交易流水号
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
}