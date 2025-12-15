package com.yasp.controller;

import com.yasp.dto.ProviderRegisterRequest;
import com.yasp.dto.ProviderRegisterResponse;
import com.yasp.dto.UserRegisterRequest;
import com.yasp.service.ProviderService;
import com.yasp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider")
public class ProviderController {
    @Autowired
    private ProviderService providerService;

    @PostMapping("/register")
    public ResponseEntity<ProviderRegisterResponse> login(@RequestBody ProviderRegisterRequest request){
        ProviderRegisterResponse response = providerService.register(request);
        return ResponseEntity.ok(response);
    }
}
