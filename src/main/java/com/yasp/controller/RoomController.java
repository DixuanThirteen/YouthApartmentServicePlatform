package com.yasp.controller;


import com.yasp.dto.Response;
import com.yasp.entity.Room;
import com.yasp.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/room-types/{roomTypeId}")
    public ResponseEntity<Response<List<Room>>> getRooms(@PathVariable Long roomTypeId) {
        // 调用服务层获取房间列表
        Response<List<Room>> resp = roomService.getRoomsByRoomTypeId(roomTypeId);

        // 根据响应的状态调整返回的 HTTP 状态码
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/room-types/{roomTypeId}")
    public ResponseEntity<Response<Room>> createRoom(
            @RequestBody Room room,
            @PathVariable Long roomTypeId,
            Authentication authentication) {

        // 获取登录用户信息
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        // 调用服务层创建房间逻辑
        Response<Room> resp = roomService.createRoom(room, roomTypeId, username, role);

        // 根据响应内容调整返回状态码
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/batch-create/room-types/{roomTypeId}")
    public ResponseEntity<Response<List<Room>>> batchCreateRooms(
            @RequestBody List<Room> rooms,
            @PathVariable Long roomTypeId,
            Authentication authentication) {

        // 获取登录用户信息
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        // 调用服务层批量创建房间逻辑
        Response<List<Room>> resp = roomService.batchCreateRooms(rooms, roomTypeId, username, role);

        // 返回响应
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Room>> getRoomById(@PathVariable Long id) {

        // 调用服务层获取房间信息
        Response<Room> resp = roomService.getRoomById(id);

        // 返回适当的 HTTP 状态码
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Room>> updateRoom(
            @PathVariable Long id,
            @RequestBody Room room,
            Authentication authentication) {

        // 获取登录用户信息
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        // 调用服务层更新房间逻辑
        Response<Room> resp = roomService.updateRoom(id, room, username, role);

        // 根据服务层的处理结果调整 HTTP 响应状态码
        if (resp.getCode() == 400) {
            return ResponseEntity.badRequest().body(resp);
        } else if (resp.getCode() == 500) {
            return ResponseEntity.internalServerError().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
