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

    public Response<RoomType> createRoomType(RoomType roomType, String username, String role) {
        Response<RoomType> resp = new Response<>(roomType);
        Response.userProfile profile = new Response.userProfile();

        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限");
            resp.setData(null);
            return resp;
        }

        if(roomType.getApartmentId() == null){
            resp.setCode(400);
            resp.setMessage("没有指定公寓");
            resp.setData(null);
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

        Apartment apartment = apartmentMapper.selectById(roomType.getApartmentId());

        if(!Objects.equals(apartment.getProviderId(), provider.getId())) {
            resp.setCode(400);
            resp.setMessage("您不是该企业员工");
            resp.setData(null);
            return resp;
        }

        if(roomTypeMapper.selectRoomTypeByName(roomType.getName()) != null){
            resp.setCode(400);
            resp.setMessage("房型名不能重复");
            resp.setData(null);
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

    public Response<RoomType> getRoomTypeById(Long id) {
        Response<RoomType> resp = new Response<>(null);

        RoomType roomType = new RoomType();
        try {
            roomType = roomTypeMapper.selectById(id);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage("查找失败");
            resp.setData(null);
            return resp;
        }

        if(roomType == null){
            resp.setCode(400);
            resp.setMessage("未找到该房型");
            resp.setData(null);
            return resp;
        }

        resp.setData(roomType);
        resp.setCode(200);
        resp.setMessage("获取成功");

        return resp;
    }

    public Response<RoomType> updateRoomType(Long id, RoomType roomType, String username, String role) {
        Response<RoomType> resp = new Response<>(null);
        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        if(!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")){
            resp.setCode(400);
            resp.setMessage(role);
            return resp;
        }

        RoomType oldRoomType = roomTypeMapper.selectById(id);
        if(oldRoomType == null){
            resp.setCode(400);
            resp.setMessage("该房型不存在");
            return resp;
        }

        ProviderAccount account = providerAccountMapper.selectProviderAccountByUsername(username);

        if(account == null){
            resp.setCode(400);
            resp.setMessage("你不是企业用户");
            return resp;
        }

        Provider provider = providerMapper.selectProviderById(account.getProviderId());
        String team = provider.getName();
        profile.setTeam(team);
        resp.setProfile(profile);

        if(!Objects.equals(account.getProviderId(), apartmentMapper.selectById(oldRoomType.getApartmentId()).getProviderId())){
            resp.setCode(400);
            resp.setMessage("您不是该企业员工");
            return resp;
        }

        try {
            roomType.setId(id);
            roomTypeMapper.updateById(roomType);
        }catch (Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        RoomType newRoomType = roomTypeMapper.selectById(id);
        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(newRoomType);

        return resp;
    }
}
