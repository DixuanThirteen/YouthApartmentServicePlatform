package com.yasp.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long bookingId;        // 房间预订ID
    private String paymentMethod;  // 支付方式（如支付宝、微信支付、PayPal）
}
