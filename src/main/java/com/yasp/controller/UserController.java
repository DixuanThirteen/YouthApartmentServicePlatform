package com.yasp.controller;

import com.yasp.dto.LoginRequest;
import com.yasp.entity.User;
import com.yasp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController // 1. 标记这是一个Rest接口控制器，返回JSON（不渲染页面）
@RequestMapping("/api/user") // 2. 统一设定本类下所有接口的URL前缀
public class UserController {
    @Autowired // 3. 自动注入Service层对象
    private UserService userService;

    //注册
    @PostMapping("/register")
    // 5. HTTP POST, 用于注册。@RequestBody自动把json反序列化为User对象
    public boolean register(@RequestBody User user) {
        return userService.register(user);
    }

    //登录
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        User user = userService.findByUsername(req.getUsername());

        if (user == null || !Objects.equals(user.getPassword(), getByUsername(req.getUsername()).getPassword())) {
            return Map.of("success", false, "message", "用户名或密码错误");
        }


        return Map.of();
    }


    //查找方法
    //通过用户名查用户
    @GetMapping("/{username}")
    // 4. HTTP GET，路径变量，查询指定用户名用户
    public User getByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    //通过手机号查用户
    @GetMapping("/phone/{phone}")
    // 4. HTTP GET，路径变量，查询指定手机号用户
    public User getByPhone(@PathVariable String phone) {
        return userService.findByPhone(phone);
    }

    //通过邮箱查用户
    @GetMapping("/email/{email}")
    // 4. HTTP GET，路径变量，查询指定邮箱用户
    public User getByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }



    //更新方法
    //更新头像、昵称、个人介绍、地区。
    @PutMapping("/update")
    // 6. HTTP PUT，更新用户除密码外信息
    public boolean updateUser(@RequestParam String username,
                              @RequestParam String avatar,
                              @RequestParam String nickname,
                              @RequestParam String introduction,
                              @RequestParam String region) {
        User user = new User();
        user.setUsername(username);
        user.setAvatar(avatar);
        user.setNickname(nickname);
        user.setIntroduction(introduction);
        user.setRegion(region);
        return userService.updateUser(user);
    }

    //修改密码
    @PutMapping("/update-password")
    // 7. 修改密码，接收用户名和新密码
    public boolean updatePassword(@RequestParam String username,
                                  @RequestParam String oldPassword,
                                  @RequestParam String newPassword) {
        return userService.updatePassword(username, oldPassword , newPassword);
    }

    //更改手机号
    @PutMapping("/update-phone")
    // 修改手机号，接收用户名和新手机号
    public boolean updatePhone(@RequestParam String username,
                               @RequestParam String oldPhone,
                               @RequestParam String phone) {
        return userService.updatePhone(username, oldPhone, phone);
    }

    //更改邮箱
    @PutMapping("/update-email")
    // 修改邮箱，接收用户名和新邮箱
    public boolean updateEmail(@RequestParam String username,
                               @RequestParam String oldEmail,
                               @RequestParam String email) {
        return userService.updateEmail(username, oldEmail, email);
    }

    //更改学位信息
    @PutMapping("/update-degree")
    // 修改学位信息，接收用户名和新学位
    public boolean updateDegree(@RequestParam String username,
                                @RequestParam String degree) {
        return userService.updateDegree(username, degree);
    }

    //删除用户
    @DeleteMapping("/{username}")
    // 8. 删除指定用户名用户
    public boolean deleteByUsername(@PathVariable String username) {
        return userService.deleteByUsername(username);
    }


}