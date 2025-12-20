package com.yasp.controller;

import com.yasp.dto.Response;
import com.yasp.entity.RoomType;
import com.yasp.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room-types")
public class RoomTypeController {
    @Autowired
    private RoomTypeService roomTypeService;

    @GetMapping("/apartment/{apartmentId}")//获取某公寓所拥有房型
    public ResponseEntity<Object> getRoomTypes(@PathVariable Long apartmentId){
        List<RoomType> roomTypes = roomTypeService.getRoomTypeByApartmentId(apartmentId);
        if(roomTypes == null ||  roomTypes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Resource with ID " + apartmentId + " was not found.");
        }
        return ResponseEntity.ok(roomTypes);
    }

    @PostMapping//创建房型
    public ResponseEntity<Response<RoomType>> createRoomType(
            @RequestBody RoomType roomType,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response<RoomType> resp = roomTypeService.createRoomType(roomType, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
