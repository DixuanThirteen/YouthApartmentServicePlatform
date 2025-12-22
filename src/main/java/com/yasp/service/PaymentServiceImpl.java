package com.yasp.service;

import com.yasp.dto.PaymentRequest;
import com.yasp.dto.PaymentCallback;
import com.yasp.dto.Response;
import com.yasp.entity.Payments;
import com.yasp.entity.RoomBooking;
import com.yasp.mapper.PaymentsMapper;
import com.yasp.mapper.RoomAvailabilityMapper;
import com.yasp.mapper.RoomBookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentsMapper paymentsMapper;

    @Autowired
    private RoomBookingMapper roomBookingMapper;
    @Autowired
    private RoomAvailabilityMapper roomAvailabilityMapper;

    // 生成支付请求
    @Override
    public Response<Object> preparePayment(PaymentRequest request) {
        Response<Object> response = new Response<>(null);
        Payments payments = new Payments();

        // 查找预订单
        RoomBooking booking = roomBookingMapper.selectById(request.getBookingId());
        if (booking == null) {
            response.setCode(400);
            response.setMessage("预订单不存在");
            return response;
        }

        if (booking.getStatus() != 0) { // PENDING 状态
            response.setCode(400);
            response.setMessage("订单已支付或不可支付");
            return response;
        }
        Payments payment = paymentsMapper.selectPaymentByBookingId(request.getBookingId());
        if ( payment != null){
            response.setCode(200);
            response.setMessage("已有支付订单存在，请前往支付。");
            response.setData(payment);
            return response;
        }

        // 计算支付总额（房租+押金）
        int totalAmount = booking.getRentCent() + booking.getDepositCent();

        // 生成支付记录
        String paymentId = "PAY-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();

        payments.setPaymentId(paymentId);
        payments.setBookingId(booking.getId());
        payments.setAmountCent(totalAmount);
        paymentsMapper.insertPayment(payments);


        // 返回支付信息
        response.setCode(200);
        response.setMessage("支付请求生成成功");
        response.setData(Map.of(
                "paymentId", paymentId,
                "totalAmount", totalAmount,
                "expiresAt", LocalDateTime.now().plusMinutes(15)
        ));
        return response;
    }

    // 处理支付回调
    @Override
    public Response<Object> handlePaymentCallback(PaymentCallback callback) {
        Response<Object> response = new Response<>(null);

        // 验证支付记录
        RoomBooking booking = roomBookingMapper.selectById(callback.getBookingId());
        if (booking == null) {
            response.setCode(400);
            response.setMessage("预订单不存在");
            return response;
        }

        // 处理支付结果
        if ("SUCCESS".equals(callback.getStatus())) {
            // 更新支付状态为成功
            paymentsMapper.updatePaymentStatus(callback.getPaymentId(), "SUCCESS");

            // 更新订单状态为确认支付
            roomBookingMapper.updateBookingStatus(callback.getBookingId(), (byte) 1); // CONFIRMED

            response.setCode(200);
            response.setMessage("支付成功");
        } else {
            // 更新支付状态为失败
            paymentsMapper.updatePaymentStatus(callback.getPaymentId(), "CANCEL");

            // 更新订单状态为取消
            roomBookingMapper.updateBookingStatus(callback.getBookingId(), (byte) 2); // CONFIRMED

            roomAvailabilityMapper.deleteBySource("booking", callback.getBookingId());

            response.setCode(400);
            response.setMessage("支付失败");
        }

        return response;
    }

    // 查询支付状态
    @Override
    public Response<Object> getPaymentStatus(String paymentId) {
        Response<Object> response = new Response<>(null);

        // 查询支付记录
        String status = paymentsMapper.selectPaymentStatus(paymentId);
        if (status == null) {
            response.setCode(404);
            response.setMessage("支付记录不存在");
            return response;
        }

        response.setCode(200);
        response.setMessage("支付状态查询成功");
        response.setData(Map.of("paymentId", paymentId, "status", status));
        return response;
    }

    @Override
    public Response<Payments> getPayment(String paymentId) {
        Response<Payments> resp = new Response<>(null);

        Response.userProfile profile = new Response.userProfile();

        Payments payments = new Payments();
        try{
            payments = paymentsMapper.selectByPaymentId(paymentId);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        if(payments == null){
            resp.setCode(400);
            resp.setMessage("没有找到支付订单");
            return resp;
        }

        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(payments);

        return resp;

    }
}
