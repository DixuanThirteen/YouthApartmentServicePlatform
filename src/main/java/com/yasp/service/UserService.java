package com.yasp.service;

import com.yasp.dto.*;
import com.yasp.entity.User;
import com.yasp.mapper.UserMapper;
import com.yasp.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
@Service // 1. 标识本类是Service组件，交给Spring容器管理，属于业务逻辑层
public class UserService {
    @Autowired // 2. 由Spring自动装配UserMapper实例（依赖注入，简化手动new对象）
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
        Response.setId(user.getId());
        Response.setUsername(user.getUsername());
        Response.setToken(token);
        Response.setAvatar(user.getAvatar());
        Response.setMessage("登录成功");

        return Response;
    }

    public  User userDetail(Long id) {
        User user = userMapper.selectById(id);
        return user;
    }

    public Response<User> userUpdate(
            Long id,
            User user,
            String username,
            String role){
        Response<User> resp = new Response<>(user);
        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        User oldUser = userMapper.selectById(id);
        if (oldUser == null) {
            resp.setCode(400);
            resp.setMessage("用户不存在");
            resp.setData(null);
            return resp;
        }

//        String oldAvatar = oldUser.getAvatar();
//        String newAvatar = user.getAvatar();

//        if(newAvatar != null && !newAvatar.isEmpty() && !Objects.equals(oldAvatar, "/images/DefaultAvatar.png")){
//            deleteOldAvatarFile(oldAvatar);
//        }

        User userOperator = userMapper.selectByUsername(username);

        if(!userOperator.getId().equals(id)){
            resp.setCode(400);
            resp.setMessage("您不能修改他人信息");
            resp.setData(null);
            return resp;
        }

        try {
            user.setAvatar(user.getAvatar());
            user.setId(id);
            int rows = userMapper.updateUser(user);

            if(rows > 0){
                User updateUser = userMapper.selectById(id);
                resp.setCode(200);
                resp.setMessage("success");
                resp.setData(updateUser);
                return resp;
            }
            resp.setCode(500);
            resp.setMessage("fail");
            resp.setData(null);
            return resp;

        }catch (Exception e){
            resp.setCode(400);
            resp.setMessage(e.getMessage()+"更新失败");
            resp.setData(null);
            return resp;
        }
    }

    public Response<User> changePassword(
            Long id,
            UpdatePasswordRequest request,
            String username) {

        Response<User> resp = new Response<>(null);
        User user = userMapper.selectByUsername(username);

        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        resp.setProfile(profile);

        if(request.getOldPassword()==null || request.getNewPassword()==null || request.getReNewPassword()==null){
            resp.setCode(400);
            resp.setMessage("不能为空");
            return resp;
        }

        if(!Objects.equals(request.getNewPassword(), request.getReNewPassword())){
            resp.setCode(400);
            resp.setMessage("新密码不一致");
        }

        if(!Objects.equals(id, user.getId())){
            resp.setCode(400);
            resp.setMessage("您不能修改他人密码/该用户不存在");
            return  resp;
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            resp.setCode(400);
            resp.setMessage("旧密码错误");
            return resp;
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
            resp.setCode(400);
            resp.setMessage("新密码不能与旧密码重复");
            return resp;
        }

        String newencode = passwordEncoder.encode(request.getNewPassword());

        userMapper.updatePassword(id, newencode);

        resp.setCode(200);
        resp.setMessage("success");
        return resp;
    }


    /**
     * 辅助方法：删除旧头像文件
     */
    private void deleteOldAvatarFile(String avatarPath) {
        if (avatarPath == null || avatarPath.isEmpty()) {
            return;
        }

        // 保护默认头像
        if (avatarPath.contains("DefaultAvatar")) {
            return;
        }

        try {
            // 1. 解析文件名
            // 假设 avatarPath 是 "/images/abc.jpg"
            String filename = avatarPath.replace("/images/", "");

            // 2. 使用 Paths.get 拼接物理路径
            // uploadDir 是 "./uploads"
            Path filePath = Paths.get(uploadDir, filename);

            // 3. 执行删除 (NIO 方式)
            // deleteIfExists 如果文件不存在不会报错，如果文件被占用(如Windows上正打开)会抛出 IOException
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                System.out.println(">>> [NIO] 旧头像已删除: " + filePath.toAbsolutePath());
            } else {
                System.out.println(">>> [NIO] 旧头像文件不存在，跳过: " + filePath.toAbsolutePath());
            }

        } catch (IOException e) {
            // 比如文件正在被查看、权限不足等
            System.err.println(">>> [NIO] 删除文件失败: " + e.getMessage());
        }
    }

}




