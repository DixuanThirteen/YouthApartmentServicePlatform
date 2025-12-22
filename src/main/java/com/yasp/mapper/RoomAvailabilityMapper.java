package com.yasp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yasp.entity.RoomAvailability;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RoomAvailabilityMapper {

    // 插入一条占用记录
    int insert(RoomAvailability roomAvailability);

    // 查询房间在某时间段的占用记录数
    int checkConflict(@Param("roomId") Long roomId,
                      @Param("checkStartDate") LocalDate checkStartDate,
                      @Param("checkEndDate") LocalDate checkEndDate);

    // 根据房间 ID 获取所有占用记录
    List<RoomAvailability> selectByRoomId(@Param("roomId") Long roomId);

    // 根据来源 ID 和类型删除记录
    int deleteBySource(@Param("source") String source,
                       @Param("sourceId") Long sourceId);

}