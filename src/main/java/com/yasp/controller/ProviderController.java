package com.yasp.controller;

import com.yasp.dto.*;
import com.yasp.entity.Apartment;
import com.yasp.entity.ProviderAccount;
import com.yasp.entity.RoomType;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.service.ApartmentService;
import com.yasp.service.ProviderService;
import com.yasp.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/provider")
public class ProviderController {
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ProviderAccountMapper providerAccountMapper;
    @Autowired
    private ApartmentService apartmentService;
    @Autowired
    private RoomTypeService roomTypeService;

    @PostMapping("/register")
    public ResponseEntity<ProviderRegisterResponse> register(@RequestBody ProviderRegisterRequest request){
        ProviderRegisterResponse response = providerService.register(request);
        if(response.getCode() == 400){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse resp = providerService.login(request);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/addAccount")
    public ResponseEntity<Response> addAccount(
            @RequestBody ProviderAccount account,
            Authentication authentication
    ) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response resp = providerService.addAccount(account, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/addApartment")
    public ResponseEntity<Response> addApartment(
            @RequestBody Apartment apartment,
            Authentication authentication
            ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response resp = apartmentService.addApartment(apartment, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return  ResponseEntity.ok(resp);
    }

    @PostMapping("/createRoomType")
    public ResponseEntity<Response> createRoomType(
            @RequestBody RoomType roomType,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response resp = roomTypeService.createRoomType(roomType, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/apartment")
    public ResponseEntity<Response> updateApartment(
            @RequestBody Apartment apartment,
            Authentication authentication
    ){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response resp = apartmentService.updateApartment(apartment, username, role);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

}
