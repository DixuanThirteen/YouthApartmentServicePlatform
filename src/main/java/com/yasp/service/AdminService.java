package com.yasp.service;

import com.yasp.dto.LoginResponse;
import com.yasp.entity.Admin;
import com.yasp.mapper.AdminMapper;
import com.yasp.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AdminService(AdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.adminMapper = adminMapper;
    }

    public LoginResponse login(String username, String password, Boolean RememberMe){
        LoginResponse Response = new LoginResponse();
        Admin admin = adminMapper.selectByUsername(username);

        if(admin == null){
            Response.setCode(400);
            Response.setMessage("用户名不存在");
            return Response;
        }

        if(!passwordEncoder.matches(password, admin.getPassword())){
            Response.setCode(400);
            Response.setMessage("密码错误");
        }

        Response.setRole(LoginResponse.Role.ADMIN);

        long EXP_MS = 2 * 60 * 60 * 1000L;
        String token = JwtUtil.generateToken(admin.getId(),username,Response.getRole(), EXP_MS);

        Response.setCode(200);
        Response.setUsername(admin.getUsername());
        Response.setToken(token);
        Response.setMessage("登录成功");

        return Response;
    }

}
