package com.yasp.controller;

import com.yasp.dto.BookRequest;
import com.yasp.dto.Response;
import com.yasp.entity.RoomBooking;
import com.yasp.service.RoomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room-booking")
public class RoomBookingController {
    @Autowired
    RoomBookingService roomBookingService;

    @PostMapping("/rooms/{roomId}")//预定房间------该方法为自选房间预定
    public ResponseEntity<Response<RoomBooking>> bookSpecificRoom(
            @PathVariable Long roomId,
            @RequestBody BookRequest request,
            Authentication authentication){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        // 调用服务层
        Response<RoomBooking> resp = roomBookingService.bookSpecificRoom(roomId, username, role, request);

        // 根据响应设置对应状态码
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/room-types/{roomTypeId}")//预定房间------该方法为选择房型预定房间，房间由系统自动分配
    public ResponseEntity<Response<RoomBooking>> bookRoomByRoomType(
            @PathVariable Long roomTypeId,
            @RequestBody BookRequest request,
            Authentication authentication){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        // 调用服务层
        Response<RoomBooking> resp = roomBookingService.bookRoomByRoomType(roomTypeId, username, role, request);

        // 根据响应设置对应状态码
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response<List<RoomBooking>>> getRoomByUserId(
            @PathVariable Long userId,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        Response<List<RoomBooking>> resp = roomBookingService.getRoomByUserId(userId, username, role);
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);

    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Response<RoomBooking>> CancelRoomByUserId(
            @PathVariable Long bookingId,
            Authentication authentication){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        Response<RoomBooking> resp = roomBookingService.cancelRoomBooking(bookingId, username, role);
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

}
