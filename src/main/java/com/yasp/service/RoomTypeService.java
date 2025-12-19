package com.yasp.service;

import com.yasp.dto.Response;
import com.yasp.entity.Apartment;
import com.yasp.entity.Provider;
import com.yasp.entity.ProviderAccount;
import com.yasp.entity.RoomType;
import com.yasp.mapper.ApartmentMapper;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.mapper.ProviderMapper;
import com.yasp.mapper.RoomTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RoomTypeService {
    @Autowired
    RoomTypeMapper roomTypeMapper;
    @Autowired
    private ProviderAccountMapper providerAccountMapper;
    @Autowired
    private ApartmentMapper apartmentMapper;
    @Autowired
    private ProviderMapper providerMapper;

    public Response createRoomType(RoomType roomType, String username, String role) {
        Response resp = new Response();
        Response.userProfile profile = new Response.userProfile();

        if(roomType.getApartmentId() == null){
            resp.setCode(400);
            resp.setMessage("没有指定公寓");
            return resp;
        }


        //获取企业名称
        ProviderAccount account = providerAccountMapper.selectProviderAccountByUsername(username);
        Provider provider = providerMapper.selectProviderById(account.getProviderId());
        String team = provider.getName();

        profile.setUsername(username);
        profile.setRole(role);
        profile.setTeam(team);
        resp.setProfile(profile);

        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");

            return resp;
        }

        Apartment apartment = apartmentMapper.selectById(roomType.getApartmentId());

        if(!Objects.equals(apartment.getProviderId(), provider.getId())) {
            resp.setCode(400);
            resp.setMessage("您不是该企业员工");
            return resp;
        }

        if(roomTypeMapper.selectRoomTypeByName(roomType.getName()) != null){
            resp.setCode(400);
            resp.setMessage("房型名不能重复");
            return resp;
        }

        roomType.setStatus(1);//房型状态

        try{
            roomTypeMapper.insert(roomType);
            resp.setCode(200);
            resp.setMessage("创建房型成功！");
        }catch(Exception e){
            resp.setCode(400);
            resp.setMessage(e.getMessage());
        }

        return resp;
    }

    public List<RoomType> getRoomTypeByApartmentId(Long apartmentId) {
        List<RoomType> roomTypeList = roomTypeMapper.selectByApartmentId(apartmentId);



        return  roomTypeList;
    }
}
