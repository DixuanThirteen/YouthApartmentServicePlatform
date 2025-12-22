package com.yasp.service;

import com.yasp.dto.PaymentRequest;
import com.yasp.dto.PaymentCallback;
import com.yasp.dto.Response;
import com.yasp.entity.Payments;

public interface PaymentService {
    Response<Object> preparePayment(PaymentRequest request);        // 准备支付
    Response<Object> handlePaymentCallback(PaymentCallback callback); // 支付完成回调
    Response<Object> getPaymentStatus(String paymentId);            // 查询支付状态
    Response<Payments> getPayment(String paymentId);
}
