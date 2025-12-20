package com.yasp.service;

import com.yasp.dto.*;
import com.yasp.entity.Provider;
import com.yasp.entity.ProviderAccount;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.mapper.ProviderMapper;
import com.yasp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yasp.dto.LoginResponse.Role.PROVIDER_Admin;
import static com.yasp.dto.LoginResponse.Role.PROVIDER_Staff;

@Service
public class ProviderService {
    @Autowired
    ProviderMapper providerMapper;

    @Autowired
    ProviderAccountMapper providerAccountMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ProviderRegisterResponse register(ProviderRegisterRequest request){
        ProviderRegisterResponse response = new ProviderRegisterResponse();

        if(providerMapper.SelectProviderByName(request.getName()) != null){ //判断企业是否已经注册
            response.setCode(400);
            response.setMessage("企业名称已存在");
            return  response;
        }
        if(providerMapper.SelectProviderByLicense(request.getLicenseNumber()) != null){ //判断企业是否已经注册
            response.setCode(400);
            response.setMessage("企业执照已存在");
            return  response;
        }

        Integer status = 0;     //注册时status默认为0，0意味待审核
        Provider provider = getProvider(request, status);   //把数据按Provider实体类封装

        if(provider == null){
            response.setCode(400);
            response.setMessage("数据为空");
            return  response;
        }
        //创建provider
        providerMapper.insertProvider(provider);    //把provider插入数据库



        //创建providerAccount
        ProviderAccount providerAccount = new ProviderAccount();

        String encode = passwordEncoder.encode(request.getPassword());

        Long providerId = provider.getId();                 //保存provider的ID
        providerAccount.setProviderId(providerId);
        providerAccount.setUsername(request.getDefaultAccount());
        providerAccount.setPassword(encode);
        providerAccount.setRole(1);
        providerAccount.setStatus(1);
        providerAccountMapper.insertProviderAccount(providerAccount);   //把providerAccount插入数据库

        ProviderRegisterResponse.accountProfile accountProfile = new ProviderRegisterResponse.accountProfile();     //填写accountProfile
        accountProfile.setUsername(providerAccount.getUsername());
        accountProfile.setPassword(request.getPassword());
        accountProfile.setRole(providerAccount.getRole());

        response.setProfile(accountProfile);
        response.setName(request.getName());
        response.setType(request.getType());
        response.setCode(200);
        response.setMessage("注册成功");

        return response;
    }

    public LoginResponse login(LoginRequest request){
        LoginResponse response = new LoginResponse();
        ProviderAccount providerAccount = providerAccountMapper.selectProviderAccountByUsername(request.getUsername());

        if(providerAccount == null){
            response.setCode(400);
            response.setMessage("用户不存在");
            return  response;
        }

        if(!passwordEncoder.matches(request.getPassword(),providerAccount.getPassword())){
            response.setCode(400);
            response.setMessage("密码错误");
            return  response;
        }

        switch(providerAccount.getRole()) {
            case 1:
                response.setRole(PROVIDER_Admin);
                break; // 结束此 case 的执行
            case 2:
                response.setRole(PROVIDER_Staff);
                break; // 防止继续往下执行
            default:
                // 可以添加默认行为，例如抛出异常或设置默认值
                response.setCode(400);
                return response;
        }

        response.setUsername(request.getUsername());
        response.setCode(200);
        response.setMessage("登录成功");
        response.setProvider(providerMapper.selectProviderById(providerAccount.getProviderId()).getName());

        long EXP_MS = 2 * 60 * 60 * 1000L;
        String token = JwtUtil.generateToken(providerAccount.getId(),providerAccount.getUsername(),response.getRole(), EXP_MS);

        response.setToken(token);

        return response;
    }

    public Provider getProvider(ProviderRegisterRequest request, Integer status){
        Provider provider = new Provider();
        provider.setName(request.getName());
        provider.setType(request.getType());
        provider.setContactPerson(request.getContactPerson());
        provider.setContactPhone(request.getContactPhone());
        provider.setContactEmail(request.getContactEmail());
        provider.setAddress(request.getAddress());
        provider.setLicenseNumber(request.getLicenseNumber());
        provider.setDescription(request.getDescription());
        provider.setStatus(status);
        return provider;
    }

    public Response<ProviderAccount> addAccount(ProviderAccount account, String username, String role){
        //account = 被添加账号 ， username = 操作者用户名 ， role = 操作者身份

        Response<ProviderAccount> resp = new Response<>(account);

        //查找操作者信息
        //userAccount = 操作者信息
        ProviderAccount userAccount = providerAccountMapper.selectProviderAccountByUsername(username);

        if(userAccount.getRole() != 1){
            resp.setCode(400);
            resp.setMessage("您不是管理员");
            resp.setData(null);
            return resp;
        }

        //查找操作者的企业信息
        //provider = 操作者企业信息
        Provider provider = providerMapper.selectProviderById(userAccount.getProviderId());

        //判断账号是否存在
        if(providerAccountMapper.selectProviderAccountByUsername(account.getUsername()) != null){
            resp.setCode(400);
            resp.setMessage("用户名已存在");
            resp.setData(null);
            return resp;
        }

        //企业Id
        account.setProviderId(userAccount.getProviderId());

        //密码哈希
        try{
            String rawPassword = account.getPassword();
            String encode = passwordEncoder.encode(rawPassword);
            account.setPassword(encode);
        }catch (Exception e){
            resp.setCode(400);
            resp.setMessage(e.getMessage());
            return resp;
        }

        providerAccountMapper.insertProviderAccount(account);

        resp.setData(providerAccountMapper.selectProviderAccountByUsername(account.getUsername()));

        resp.setCode(200);
        resp.setMessage("账号已添加");

        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(provider.getName());

        resp.setProfile(profile);
        return resp;
    }
}
