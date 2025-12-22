package com.yasp.mapper;

import com.yasp.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {

    Room selectById(@Param("id") Long id);

    List<Room> selectByApartmentId(@Param("apartmentId") Long apartmentId);

    List<Room> selectByRoomTypeId(@Param("roomTypeId") Long roomTypeId);

    int insert(Room room);

    int updateById(Room room);

    int deleteById(@Param("id") Long id);

    int batchInsert(List<Room> rooms);

    Room selectAvailableRoomByRoomTypeId(Long RoomTypeId);
    // 更新房间租赁状态
    int updateRentStatus(@Param("roomId") Long roomId, @Param("rentStatus") int rentStatus);
}