package com.yasp.dto;

import lombok.Data;

@Data
public class PaymentCallback {
    private String paymentId;      // 本系统的支付ID
    private Long bookingId;        // 房间预订ID
    private String status;         // 支付状态 (SUCCESS / FAILED)
    private int amount;            // 支付金额（单位：分）
    private String transactionId;  // 第三方支付网关生成的交易流水号
}