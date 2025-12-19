package com.yasp.mapper;

import com.yasp.entity.RoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomTypeMapper {

    RoomType selectById(@Param("id") Long id);

    List<RoomType> selectByApartmentId(@Param("apartmentId") Long apartmentId);

    int insert(RoomType roomType);

    int updateById(RoomType roomType);

    int deleteById(@Param("id") Long id);

    RoomType selectRoomTypeByName(String name);
}