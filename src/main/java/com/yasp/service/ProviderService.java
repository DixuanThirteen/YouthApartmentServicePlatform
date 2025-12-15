package com.yasp.service;

import com.yasp.dto.ProviderRegisterRequest;
import com.yasp.dto.ProviderRegisterResponse;
import com.yasp.entity.Provider;
import com.yasp.entity.ProviderAccount;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.mapper.ProviderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
