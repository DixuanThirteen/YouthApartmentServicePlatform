package com.yasp.mapper;

import com.yasp.entity.Payments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentsMapper {
    void insertPayment(Payments payments);

    void updatePaymentStatus(@Param("paymentId") String paymentId,
                             @Param("status") String status);

    String selectPaymentStatus(@Param("paymentId") String paymentId);

    Payments selectPaymentByBookingId(Long bookingId);

    Payments selectByPaymentId(String paymentId);
}
