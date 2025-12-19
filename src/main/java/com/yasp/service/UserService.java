package com.yasp.service;

import com.yasp.dto.LoginResponse;
import com.yasp.dto.UserRegisterRequest;
import com.yasp.dto.UserRegisterResponse;
import com.yasp.entity.User;
import com.yasp.mapper.UserMapper;
import com.yasp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // 1. 标识本类是Service组件，交给Spring容器管理，属于业务逻辑层
public class UserService {
    @Autowired // 2. 由Spring自动装配UserMapper实例（依赖注入，简化手动new对象）
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //注册
    public UserRegisterResponse register(UserRegisterRequest request) {
        UserRegisterResponse Response = new UserRegisterResponse();
        //密码、手机号、邮箱校验
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            Response.setCode(400);
            Response.setMessage("用户名已存在");
            return Response;
        }
        if (userMapper.selectByPhone(request.getPhone()) != null) {
            Response.setCode(400);
            Response.setMessage("手机号码已被使用");
            return Response;
        }
        if (userMapper.selectByEmail(request.getEmail()) != null) {
            Response.setCode(400);
            Response.setMessage("邮箱已被使用");
            return Response;
        }

        //密码哈希
        String encoded = passwordEncoder.encode(request.getPassword());
        User user = getUser(request, encoded);

        if (user == null) {
            Response.setCode(400);
            Response.setMessage("输入为空");
            return Response;
        }

        userMapper.insertUser(user);    //把user插入数据库

        Response.setUsername(request.getUsername());
        Response.setNickName(user.getNickName());
        Response.setCode(200);
        Response.setMessage("注册成功");

        return Response;
    }

    private static User getUser(UserRegisterRequest request, String encoded) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickName(request.getNickName());
        user.setRealName(request.getRealName());
        user.setHashPassword(encoded);
        user.setIdNumber(request.getIdNumber());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setBornDate(request.getBornDate());
        user.setGender(request.getGender());
        user.setAvatar(request.getAvatar());
        user.setIntroduction(request.getIntroduction());
        user.setRegion(request.getRegion());
        user.setDegree(request.getDegree());
        return user;
    }

    public LoginResponse login(String username, String password, Boolean RememberMe) {
        LoginResponse Response = new LoginResponse();
        User user = userMapper.selectByUsername(username);

        if (user == null) {
            Response.setCode(400);
            Response.setMessage("用户名不存在");
            return Response;
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            Response.setCode(400);
            Response.setMessage("密码错误");
            return Response;
        }
        Response.setRole(LoginResponse.Role.USER);

        long EXP_MS = 2 * 60 * 60 * 1000L;
        String token = JwtUtil.generateToken(user.getId(),username,Response.getRole(), EXP_MS);

        Response.setCode(200);
        Response.setUsername(user.getUsername());
        Response.setToken(token);
        Response.setMessage("登录成功");

        return Response;
    }

    }




