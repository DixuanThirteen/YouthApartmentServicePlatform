package com.yasp.controller;

import com.yasp.dto.*;
import com.yasp.entity.Apartment;
import com.yasp.entity.ProviderAccount;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.service.ApartmentService;
import com.yasp.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ProviderAccountMapper providerAccountMapper;
    @Autowired
    private ApartmentService apartmentService;

    @PostMapping("/register")
    public ResponseEntity<ProviderRegisterResponse> register(@RequestBody ProviderRegisterRequest request){
        ProviderRegisterResponse response = providerService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse resp = providerService.login(request);
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
        return  ResponseEntity.ok(resp);
    }

}
