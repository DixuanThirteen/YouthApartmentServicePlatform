package com.yasp.controller;

import com.yasp.dto.*;
import com.yasp.entity.User;
import com.yasp.service.UserService;
import com.yasp.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController // 1. 标记这是一个Rest接口控制器，返回JSON（不渲染页面）
@RequestMapping("/users") // 2. 统一设定本类下所有接口的URL前缀
public class UserController {
    @Autowired // 3. 自动注入Service层对象
    private UserService userService;

    @Autowired
    private JwtTokenService jwtTokenService;

    //注册
    @PostMapping
    // 5. HTTP POST, 用于注册。@RequestBody自动把json反序列化为User对象
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest request) {
        UserRegisterResponse resp = userService.register(request);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        log.info(request.toString());
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        String username = request.getUsername();
        String password = request.getPassword();
        Boolean RememberMe = request.getRememberMe();
        LoginResponse response = userService.login(username, password, RememberMe);
        if(response.getCode() == 400){
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")//查看个人信息
    public ResponseEntity<Object> userDetail(@PathVariable Long id){
        User user = userService.userDetail(id);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Resource with ID " + id + " was not found.");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<User>> userUpdate(
            @PathVariable Long id,
            @RequestBody User user,
            Authentication authentication){
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        Response<User> resp = userService.userUpdate(id, user, username, role);

        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Response<User>> changePassword(
            @PathVariable Long id,
            @RequestBody UpdatePasswordRequest request,
            Authentication authentication
    ){
        String username = authentication.getName();
        Response<User> resp = userService.changePassword(id, request, username);
        if(resp.getCode() == 400){
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(Authentication authentication,
                                                   @RequestHeader("Authorization") String token) {
        Response<String> resp = new Response<>(null);

        if (authentication == null || token == null || token.isEmpty()) {
            resp.setCode(400);
            resp.setMessage("Authorization token is required");
            return ResponseEntity.badRequest()
                    .body(resp);
        }

        // 从 Authorization 请求头提取 JWT
        String jwt = token.replace("Bearer ", "");

        // 调用服务层处理注销逻辑
        boolean isLoggedOut = jwtTokenService.invalidateToken(jwt);

        if (!isLoggedOut) {
            resp.setCode(500);
            resp.setMessage("Logout Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(resp);
        }else {
            resp.setCode(200);
            resp.setMessage("Logged out successfully");
        }

        return ResponseEntity.ok(resp);
    }


}