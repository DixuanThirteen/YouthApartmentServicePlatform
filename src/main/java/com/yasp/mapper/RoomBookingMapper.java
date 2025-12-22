package com.yasp.mapper;

import com.yasp.entity.RoomBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RoomBookingMapper {

    RoomBooking selectById(@Param("id") Long id); // 根据 ID 查询

    RoomBooking selectByBookingNo(@Param("bookingNo") String bookingNo); // 根据预订编号查询

    List<RoomBooking> selectByRoomId(@Param("roomId") Long roomId); // 根据房间 ID 查询

    List<RoomBooking> selectByUserId(@Param("userId") Long userId); // 根据用户 ID 查询

    int insert(RoomBooking roomBooking); // 插入预订

    int updateById(RoomBooking roomBooking); // 根据 ID 更新

    int deleteById(@Param("id") Long id); // 根据 ID 删除

    // 查询超时未支付的预订记录
    List<RoomBooking> findTimeoutBookings(@Param("currentTime") LocalDateTime currentTime);

    // 更新订单状态
    int updateBookingStatus(@Param("bookingId") Long bookingId,
                            @Param("status") Byte status);
}