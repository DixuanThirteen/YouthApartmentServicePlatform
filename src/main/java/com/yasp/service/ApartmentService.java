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

    public Response addApartment(Apartment apartment, String username, String role) {
        Response resp = new Response();
        Response.userProfile profile = new Response.userProfile();

        ProviderAccount user = providerAccountMapper.selectProviderAccountByUsername(username);

        profile.setUsername(username);
        profile.setRole(role);

        Provider provider = providerMapper.selectProviderById(user.getProviderId());
        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");
            resp.setProfile(profile);

            return resp;
        }

        if(apartment.getName() == null || apartment.getName().equals("")) {
            resp.setCode(400);
            resp.setMessage("你没有输入公寓名");
        }

        if(apartment.getName() != apartmentMapper.selectByNameAndProviderId(apartment.getName(), provider.getId()).getName()){
            resp.setCode(400);
            resp.setMessage("名下公寓不能重名");
            resp.setProfile(profile);
            return resp;
        }

        apartment.setProviderId(provider.getId());

        apartmentMapper.insert(apartment);

        resp.setCode(200);
        resp.setMessage("success");

        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(provider.getName());

        resp.setProfile(profile);

        return  resp;

    }

    public List<Apartment> getAllApartments() {
        List<Apartment> apartments = apartmentMapper.selectAll();
        return apartments;
    }

    public List<Apartment> searchApartments(ApartmentSearchRequest apartment) {
        List<Apartment> apartments = apartmentMapper.searchApartments(apartment);

        String message = null;
        if (apartments == null || apartments.isEmpty()) {
            message = "找不到符合要求的公寓";
            log.info(message);
        }

        return apartments;
    }

    public Apartment getApartmentById(Long id) {
        Apartment apartment = apartmentMapper.selectById(id);

        if(apartment == null){
            log.info("apartment not found");
        }

        return  apartment;
    }

    public Response updateApartment(Apartment apartment, String username, String role) {
        Response resp = new Response();
        Response.userProfile profile = new Response.userProfile();

        ProviderAccount user = providerAccountMapper.selectProviderAccountByUsername(username);
        Provider provider = providerMapper.selectProviderById(user.getProviderId());
        Apartment apartment1 = apartmentMapper.selectById(apartment.getId());

        //设置profile
        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(provider.getName());
        resp.setProfile(profile);

        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");

            return resp;
        }

        if(!Objects.equals(apartment1.getProviderId(), provider.getId())){
            System.out.println("公寓所属企业id:"+apartment.getProviderId());
            System.out.println("员工所属企业id:"+provider.getId());
            resp.setCode(400);
            resp.setMessage("您不是该企业员工");

            return resp;
        }

        try{
            apartmentMapper.updateById(apartment);
        }catch (Exception e){
            resp.setCode(400);
            resp.setMessage("更新失败");
            return resp;
        }
        resp.setCode(200);
        resp.setMessage("success");

        return resp;
    }

}
