package com.yasp.service;

import com.yasp.dto.Response;
import com.yasp.entity.Apartment;
import com.yasp.entity.ProviderAccount;
import com.yasp.entity.Room;
import com.yasp.entity.RoomType;
import com.yasp.mapper.ApartmentMapper;
import com.yasp.mapper.ProviderAccountMapper;
import com.yasp.mapper.RoomMapper;
import com.yasp.mapper.RoomTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoomService {
    @Autowired
    RoomMapper roomMapper;

    @Autowired
    RoomTypeMapper roomTypeMapper;

    @Autowired
    ApartmentMapper apartmentMapper;

    @Autowired
    ProviderAccountMapper providerAccountMapper;

    public Response<List<Room>> getRoomsByRoomTypeId(Long roomTypeId) {
        Response<List<Room>> resp = new Response<>(null);

        try {
            // 查询与该房型关联的所有房间
            List<Room> rooms = roomMapper.selectByRoomTypeId(roomTypeId);

            // 如果房间列表为空
            if (rooms == null || rooms.isEmpty()) {
                resp.setCode(400);
                resp.setMessage("房型下没有房间");
                return resp;
            }

            // 返回成功结果
            resp.setCode(200);
            resp.setMessage("房间列表获取成功");
            resp.setTotal(rooms.size());
            resp.setData(rooms);
        } catch (Exception e) {
            // 捕获异常
            resp.setCode(500);
            resp.setMessage(e.getMessage());
        }

        return resp;
    }

    public Response<Room> createRoom(Room room, Long roomTypeId, String username, String role) {
        Response<Room> resp = new Response<>(null);

        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        // 权限校验
        if (!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限执行此操作");
            return resp;
        }

        // 设置房间的 roomTypeId
        room.setRoomTypeId(roomTypeId);

        // 验证 roomTypeId 是否存在
        RoomType roomType = roomTypeMapper.selectById(roomTypeId);

        if (roomType == null) {
            resp.setCode(400);
            resp.setMessage("房型不存在");
            return resp;
        }

        room.setApartmentId(roomType.getApartmentId());

        // 验证房间的 apartmentId 是否匹配
        Apartment apartment = apartmentMapper.selectById(roomType.getApartmentId());
        ProviderAccount account = providerAccountMapper.selectProviderAccountByUsername(username);
        if (account == null || !Objects.equals(apartment.getProviderId(), account.getProviderId())) {
            resp.setCode(400);
            resp.setMessage("您没有权限为此房型创建房间");
            return resp;
        }

        try {
            roomMapper.insert(room);
            resp.setCode(200);
            resp.setMessage("房间创建成功");
            resp.setData(room);
        } catch (Exception e) {
            resp.setCode(500);
            resp.setMessage(e.getMessage());
        }

        return resp;
    }

    public Response<List<Room>> batchCreateRooms(List<Room> rooms, Long roomTypeId, String username, String role) {
        Response<List<Room>> resp = new Response<>(null);
        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        // 权限校验
        if (!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限执行此操作");
            return resp;
        }

        // 验证 roomTypeId 是否存在
        RoomType roomType = roomTypeMapper.selectById(roomTypeId);
        if (roomType == null) {
            resp.setCode(400);
            resp.setMessage("房型不存在");
            return resp;
        }

        // 验证房间的 apartmentId 是否匹配
        Apartment apartment = apartmentMapper.selectById(roomType.getApartmentId());
        ProviderAccount account = providerAccountMapper.selectProviderAccountByUsername(username);

        if (account == null || !Objects.equals(apartment.getProviderId(), account.getProviderId())) {
            resp.setCode(400);
            resp.setMessage("您没有权限为此房型批量创建房间");
            return resp;
        }

        // 设置房间的 roomTypeId 并批量插入
        try {
            for (Room room : rooms) {
                room.setRoomTypeId(roomTypeId);
                room.setApartmentId(roomType.getApartmentId());
            }
            roomMapper.batchInsert(rooms);
            resp.setCode(200);
            resp.setMessage("批量创建房间成功");
            resp.setData(rooms);
        } catch (Exception e) {
            resp.setCode(500);
            resp.setMessage(e.getMessage());
        }

        return resp;
    }

    public Response<Room> getRoomById(Long id) {
        Response<Room> resp = new Response<>(null);

        try {
            Room room = roomMapper.selectById(id);
            if (room == null) {
                resp.setCode(400);
                resp.setMessage("房间不存在");
            } else {
                resp.setCode(200);
                resp.setMessage("房间信息获取成功");
                resp.setData(room);
            }
        } catch (Exception e) {
            resp.setCode(500);
            resp.setMessage(e.getMessage());
        }

        return resp;
    }

    public Response<Room> updateRoom(Long id, Room room, String username, String role) {
        Response<Room> resp = new Response<>(null);
        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        profile.setRole(role);
        resp.setProfile(profile);

        // 验证权限
        if (!Objects.equals(role, "ROLE_PROVIDER_Admin") && !Objects.equals(role, "ROLE_PROVIDER_Staff")) {
            resp.setCode(400);
            resp.setMessage("您没有权限执行此操作");
            return resp;
        }

        // 验证房间是否存在
        Room existingRoom = roomMapper.selectById(id);
        if (existingRoom == null) {
            resp.setCode(400);
            resp.setMessage("房间不存在");
            return resp;
        }

        // 验证用户是否属于房间所属的企业
        ProviderAccount account = providerAccountMapper.selectProviderAccountByUsername(username);
        if (account == null) {
            resp.setCode(400);
            resp.setMessage("您不是企业用户");
            return resp;
        }

        Apartment apartment = apartmentMapper.selectById(existingRoom.getApartmentId());
        if (apartment == null || !Objects.equals(account.getProviderId(), apartment.getProviderId())) {
            resp.setCode(400);
            resp.setMessage("您没有权限操作该房间");
            return resp;
        }

        try {
            // 更新房间数据
            room.setId(id);
            roomMapper.updateById(room);

            // 重新查询更新后的房间数据
            Room updatedRoom = roomMapper.selectById(id);
            resp.setCode(200);
            resp.setMessage("房间更新成功");
            resp.setData(updatedRoom);
        } catch (Exception e) {
            resp.setCode(500);
            resp.setMessage(e.getMessage());
        }

        return resp;
    }
}
