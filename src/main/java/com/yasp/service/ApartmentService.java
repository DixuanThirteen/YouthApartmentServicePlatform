package com.yasp.service;

import com.yasp.dto.ApartmentSearchRequest;
import com.yasp.dto.Response;
import com.yasp.entity.Apartment;
import com.yasp.entity.Provider;
import com.yasp.entity.ProviderAccount;
import com.yasp.mapper.ApartmentMapper;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.mapper.ProviderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class ApartmentService {
    @Autowired
    ApartmentMapper apartmentMapper;
    @Autowired
    private ProviderAccountMapper providerAccountMapper;
    @Autowired
    private ProviderMapper providerMapper;

    public Response<Apartment> addApartment(Apartment apartment, String username, String role) {
        Response<Apartment> resp = new Response<>(apartment);
        Response.userProfile profile = new Response.userProfile();

        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");
            resp.setProfile(profile);
            resp.setData(null);
            return resp;
        }

        ProviderAccount user = providerAccountMapper.selectProviderAccountByUsername(username);

        profile.setUsername(username);
        profile.setRole(role);

        Provider provider = providerMapper.selectProviderById(user.getProviderId());

        if(apartment.getName() == null || apartment.getName().isEmpty()) {
            resp.setCode(400);
            resp.setMessage("你没有输入公寓名");
            resp.setData(null);
        }

        if(apartmentMapper.selectByNameAndProviderId(apartment.getName(), provider.getId()) != null){
            resp.setCode(400);
            resp.setMessage("名下公寓不能重名");
            resp.setData(null);
            resp.setProfile(profile);
            return resp;
        }

        apartment.setProviderId(provider.getId());

        apartmentMapper.insert(apartment);

        resp.setCode(200);
        resp.setMessage("success");

        resp.setData(apartmentMapper.selectByName(apartment.getName()));

        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(provider.getName());

        resp.setProfile(profile);

        return  resp;

    }

    public Response<List<Apartment>> getAllApartments() {
        Response<List<Apartment>> resp = new Response<>(null);

        List<Apartment> apartments;

        try {
            apartments = apartmentMapper.selectAll();
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        resp.setCode(200);
        resp.setMessage("success");
        resp.setTotal(apartments.size());
        resp.setData(apartments);
        return resp;
    }

    public Response<List<Apartment>> searchApartments(ApartmentSearchRequest apartment) {
        Response<List<Apartment>> resp = new Response<>(null);

        if (apartment == null) {
            resp = getAllApartments();
            return resp;
        }

        List<Apartment> apartments;

        try {
            apartments = apartmentMapper.searchApartments(apartment);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        resp.setCode(200);
        if (apartments == null || apartments.isEmpty()) {
            resp.setMessage("未找到符合要求的公寓");
            return resp;
        }

        resp.setData(apartments);
        resp.setTotal(apartments.size());

        return resp;
    }

    public Response<Apartment> getApartmentById(Long id) {
        Response<Apartment> resp = new Response<>(null);

        Apartment apartment;
        try {
            apartment = apartmentMapper.selectById(id);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        if(apartment == null){
            log.info("apartment not found");
        }
        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(apartment);

        return  resp;
    }

    public Response<Apartment> updateApartment(Long id, Apartment apartment, String username, String role) {
        apartment.setId(id);
        Response<Apartment> resp = new Response<>(apartment);
        Response.userProfile profile = new Response.userProfile();

        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");
            resp.setData(null);
            return resp;
        }
        //操作者信息
        ProviderAccount user = providerAccountMapper.selectProviderAccountByUsername(username);
        //操作者企业信息
        Provider provider = providerMapper.selectProviderById(user.getProviderId());
        //目标公寓信息
        Apartment apartment1 = apartmentMapper.selectById(id);

        //设置profile
        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(provider.getName());
        resp.setProfile(profile);


        //判断目标公寓是否与操作者所属同一企业
        if(!Objects.equals(apartment1.getProviderId(), provider.getId())){
            resp.setCode(400);
            resp.setMessage("您不是该企业员工");
            resp.setData(null);
            return resp;
        }

        try{
            apartmentMapper.updateById(apartment);
        }catch (Exception e){
            resp.setCode(400);
            resp.setMessage("更新失败");
            resp.setData(null);
            return resp;
        }
        resp.setData(apartmentMapper.selectById(id));
        resp.setCode(200);
        resp.setMessage("success");


        return resp;
    }

}
