package com.yasp.controller;

import com.yasp.dto.PaymentCallback;
import com.yasp.dto.PaymentRequest;
import com.yasp.dto.Response;
import com.yasp.entity.Payments;
import com.yasp.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/prepare")//创建支付订单
    public ResponseEntity<Response<Object>> preparePayment(@RequestBody PaymentRequest request) {
        Response<Object> response = paymentService.preparePayment(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/callback")//支付回调
    public ResponseEntity<Response<Object>> handlePaymentCallback(@RequestBody PaymentCallback callback) {
        Response<Object> response = paymentService.handlePaymentCallback(callback);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/status/{paymentId}")//查询支付订单状态
    public ResponseEntity<Response<Object>> getPaymentStatus(@PathVariable String paymentId) {
        Response<Object> response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Response<Payments>> getPayments(
            @PathVariable String paymentId
    ) {
        Response<Payments> resp = paymentService.getPayment(paymentId);
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
