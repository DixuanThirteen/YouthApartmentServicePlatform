package com.yasp.controller;

import com.yasp.dto.*;
import com.yasp.entity.Admin;
import com.yasp.entity.Apartment;
import com.yasp.service.AdminService;
import com.yasp.service.ApartmentService;
import com.yasp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 1. 标记这是一个Rest接口控制器，返回JSON（不渲染页面）
@RequestMapping("/api/user") // 2. 统一设定本类下所有接口的URL前缀
public class UserController {
    @Autowired // 3. 自动注入Service层对象
    private UserService userService;

    @Autowired
    private ApartmentService apartmentService;

    //注册
    @PostMapping("/register")
    // 5. HTTP POST, 用于注册。@RequestBody自动把json反序列化为User对象
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest request) {
        UserRegisterResponse resp = userService.register(request);
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        String username = request.getUsername();
        String password = request.getPassword();
        Boolean RememberMe = request.getRememberMe();
        LoginResponse response = userService.login(username, password, RememberMe);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apartment")
    public List<Apartment> apartmentView(Authentication authentication){
        return apartmentService.getAllApartments();
    }

    @PostMapping("/searchApartments")
    public List<Apartment> searchApartments(@RequestBody ApartmentSearchRequest apartment){
        return apartmentService.searchApartments(apartment);
    }


}