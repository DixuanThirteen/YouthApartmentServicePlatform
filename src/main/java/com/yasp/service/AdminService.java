package com.yasp.service;

import com.yasp.dto.LoginRequest;
import com.yasp.dto.LoginResponse;
import com.yasp.dto.Response;
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
            return Response;
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

    public Response<Admin> addAdmin(LoginRequest Request){
        Response<Admin> resp = new Response<>(null);

        if(adminMapper.selectByUsername(Request.getUsername()) != null){
            resp.setCode(400);
            return resp;
        }

        String encode = passwordEncoder.encode(Request.getPassword());

        adminMapper.insertAdmin(Request.getUsername(), encode);

        Admin admin = adminMapper.selectByUsername(Request.getUsername());

        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(admin);

        return resp;
    }

}
