package com.yasp.controller;

import com.yasp.dto.LoginRequest;
import com.yasp.dto.LoginResponse;
import com.yasp.dto.Response;
import com.yasp.entity.Admin;
import com.yasp.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        String username = request.getUsername();
        String password = request.getPassword();
        Boolean RememberMe = request.getRememberMe();
        LoginResponse response = adminService.login(username, password, RememberMe);
        if(response.getCode() == 400){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<Admin>> addAdmin(@RequestBody LoginRequest request){
        Response<Admin> resp = adminService.addAdmin(request);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
