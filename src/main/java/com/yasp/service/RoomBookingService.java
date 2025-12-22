package com.yasp.service;

import com.yasp.dto.BookRequest;
import com.yasp.dto.Response;
import com.yasp.entity.*;
import com.yasp.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class RoomBookingService {

    @Autowired
    private RoomBookingMapper roomBookingMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApartmentMapper apartmentMapper;

    @Autowired
    private RoomAvailabilityMapper roomAvailabilityMapper;
    @Autowired
    private PaymentsMapper paymentsMapper;

    // 自选房间预订
    public Response<RoomBooking> bookSpecificRoom(Long roomId, String username, String role, BookRequest request) {
        Response<RoomBooking> resp = new Response<>(null);

        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        if (!Objects.equals(role, "ROLE_USER")) {
            resp.setCode(400);
            resp.setMessage("这不是用户账号");
            return resp;
        }

        // 验证房间是否存在
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            resp.setCode(400);
            resp.setMessage("房间不存在");
            return resp;
        }

        Integer minLeaseMonths = apartmentMapper.selectById(room.getApartmentId()).getMinLeaseMonths();

        if (request.getLeaseMonths() < minLeaseMonths) {
            resp.setCode(400);
            resp.setMessage("最短租期为" + minLeaseMonths + "月");
            return resp;
        }

        // 验证房间是否有时间段冲突
        if (isRoomOccupied(roomId, request.getStartDate(), request.getStartDate().plusMonths(request.getLeaseMonths()))) {
            resp.setCode(400);
            resp.setMessage("房间在选择的时间段不可用");
            return resp;
        }

        // 查找预定者信息
        User user = userMapper.selectByUsername(username);

        // 创建预订记录
        RoomBooking booking = new RoomBooking();
        booking.setBookingNo(UUID.randomUUID().toString()); // 生成唯一编号
        booking.setRoomId(roomId);
        createBook(request, room, user, booking);

        try{
            roomBookingMapper.insert(booking);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        RoomBooking doneBooking = roomBookingMapper.selectByBookingNo(booking.getBookingNo());

        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setRoomId(roomId);
        roomAvailability.setOccupiedFrom(request.getStartDate());
        roomAvailability.setOccupiedTo(request.getStartDate().plusMonths(request.getLeaseMonths()));
        roomAvailability.setSource("booking");
        roomAvailability.setSourceId(doneBooking.getId());

        // 插入预订记录
        try {
            // 插入到 room_availability 表
            roomAvailabilityMapper.insert(roomAvailability);

            // 更新房间的租赁状态为 "预订中"
            room.setRentStatus(1);
            roomMapper.updateById(room);

            resp.setCode(200);
            resp.setMessage("房间已成功预订，请在15分钟内支付");
            resp.setData(booking);
        } catch (Exception e) {
            resp.setCode(500);
            resp.setMessage("内部服务错误：" + e.getMessage());
        }

        return resp;
    }

    // 根据房型预订房间
    public Response<RoomBooking> bookRoomByRoomType(Long roomTypeId, String username, String role, BookRequest request) {
        Response<RoomBooking> resp = new Response<>(null);

        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        if (!Objects.equals(role, "ROLE_USER")) {
            resp.setCode(400);
            resp.setMessage("这不是用户账号");
            return resp;
        }

        // 验证房型是否存在
        RoomType roomType = roomTypeMapper.selectById(roomTypeId);
        if (roomType == null) {
            resp.setCode(400);
            resp.setMessage("房型不存在");
            return resp;
        }

        // 获取房型中的可用房间
        List<Room> rooms = roomMapper.selectByRoomTypeId(roomTypeId);
        Room availableRoom = null;
        for (Room room : rooms){
            if (isRoomOccupied(room.getId(), request.getStartDate(), request.getStartDate().plusMonths(request.getLeaseMonths()))) {
                availableRoom = room;
                break;
            }
        }
        if (availableRoom == null) {
            resp.setCode(400);
            resp.setMessage("此房型下没有可用的房间");
            return resp;
        }

        // 验证最短租期
        Integer minLeaseMonths = apartmentMapper.selectById(availableRoom.getApartmentId()).getMinLeaseMonths();
        if (request.getLeaseMonths() < minLeaseMonths) {
            resp.setCode(400);
            resp.setMessage("最短租期为" + minLeaseMonths + "月");
            return resp;
        }

        // 验证房间是否有时间段冲突
        if (isRoomOccupied(availableRoom.getId(), request.getStartDate(),
                request.getStartDate().plusMonths(request.getLeaseMonths()))) {
            resp.setCode(400);
            resp.setMessage("房间在选择的时间段不可用");
            return resp;
        }

        // 查找预定者信息
        User user = userMapper.selectByUsername(username);

        // 创建预订记录
        RoomBooking booking = new RoomBooking();
        booking.setBookingNo(UUID.randomUUID().toString());
        booking.setRoomId(availableRoom.getId());
        createBook(request, availableRoom, user, booking);

        try {
            roomBookingMapper.insert(booking);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }


        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setRoomId(availableRoom.getId());
        roomAvailability.setOccupiedFrom(request.getStartDate());
        roomAvailability.setOccupiedTo(request.getStartDate().plusMonths(request.getLeaseMonths()));
        roomAvailability.setSource("booking");
        roomAvailability.setSourceId(booking.getId());

        // 插入预订记录
        try {
            // 插入到 room_availability 表
            roomAvailabilityMapper.insert(roomAvailability);

            // 更新房间状态为 "预订中"
            availableRoom.setRentStatus(1);
            roomMapper.updateById(availableRoom);

            resp.setCode(200);
            resp.setMessage("房间已成功预订");
            resp.setData(booking);
        } catch (Exception e) {
            resp.setCode(500);
            resp.setMessage("内部服务错误：" + e.getMessage());
        }

        return resp;
    }

    public Response<List<RoomBooking>> getRoomByUserId(Long userId, String username, String role) {
        Response<List<RoomBooking>> resp = new Response<>(null);

        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        List<RoomBooking> roomBookings = new ArrayList<>();
        try{
            roomBookings = roomBookingMapper.selectByUserId(userId);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        if(roomBookings == null || roomBookings.isEmpty()){
            resp.setCode(400);
            resp.setMessage("没有找到您的订单");
            return resp;
        }

        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(roomBookings);
        resp.setTotal(roomBookings.size());
        return resp;
    }

    public Response<RoomBooking> cancelRoomBooking(Long bookingId, String username, String role) {
        Response<RoomBooking> resp = new Response<>(null);
        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        RoomBooking booking = roomBookingMapper.selectById(bookingId);

        if(booking == null) {
            resp.setCode(400);
            resp.setMessage("没有找到该预订单");
            return resp;
        }

        User user = userMapper.selectByUsername(username);

        if(!Objects.equals(user.getId(), booking.getUserId())) {
            resp.setCode(400);
            resp.setMessage("您不能取消其他人的订单");
            return resp;
        }

        roomBookingMapper.updateBookingStatus(bookingId, (byte) 2);

        Payments payments = paymentsMapper.selectPaymentByBookingId(bookingId);

        if(payments != null) {
            paymentsMapper.updatePaymentStatus(payments.getPaymentId(), "CANCEL");
        }

        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(booking);
        return resp;
    }

    @Scheduled(fixedRate = 300000) // 5分钟检查一次
    public void cancelTimeoutBookings() {
        // 获取15分钟未支付的订单
        List<RoomBooking> timeoutBookings = roomBookingMapper.findTimeoutBookings(LocalDateTime.now());

        for (RoomBooking booking : timeoutBookings) {
            try {
                // 更新订单状态为 "canceled"
                roomBookingMapper.updateBookingStatus(booking.getId(), (byte) 2);

                // 从 room_availability 表中删除占用记录
                roomAvailabilityMapper.deleteBySource("booking", booking.getId());

                // 支付订单修改为
                paymentsMapper.updatePaymentStatus(paymentsMapper.selectPaymentByBookingId(booking.getId()).getPaymentId(), "CANCEL");

                // 更新房间状态为 "空置"
                roomMapper.updateRentStatus(booking.getRoomId(), 0);

                // 发送取消通知给用户（待实现）

                System.out.println("预订取消成功: " + booking.getBookingNo());
            } catch (Exception e) {
                System.err.println("删除预订记录失败: " + booking.getBookingNo());
            }
        }
    }

    private void createBook(BookRequest request, Room room, User user, RoomBooking booking) {
        booking.setUserId(user.getId());
        booking.setStartDate(request.getStartDate());
        booking.setLeaseMonths(request.getLeaseMonths());
        booking.setRentCent(room.getRentCent());
        booking.setDepositCent(room.getDepositCent());
        booking.setStatus((byte) 0); // 状态设置为 pending
        booking.setHoldUntil(LocalDateTime.now().plusMinutes(15)); // 锁房时间 15 分钟
    }

    // 检查房间在指定时间段是否已被占用
    private boolean isRoomOccupied(Long roomId, LocalDate checkStartDate, LocalDate checkEndDate) {
        int occupiedCount = roomAvailabilityMapper.checkConflict(roomId, checkStartDate, checkEndDate);
        return occupiedCount > 0;
    }


}