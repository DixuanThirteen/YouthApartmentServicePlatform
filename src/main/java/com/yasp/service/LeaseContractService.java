package com.yasp.service;

import com.yasp.dto.Response;
import com.yasp.entity.*;
import com.yasp.mapper.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class LeaseContractService {

    @Autowired
    private RoomBookingMapper roomBookingMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApartmentMapper apartmentMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private LeaseContractMapper leaseContractMapper;


    public Response<LeaseContract> createLeaseContract(Long bookingId, String username) {
        Response<LeaseContract> resp = new Response<>(null);
        Response.userProfile profile = new Response.userProfile();
        profile.setUsername(username);
        resp.setProfile(profile);

        LeaseContract contract = new LeaseContract();

        RoomBooking booking = roomBookingMapper.selectById(bookingId);
        User user = userMapper.selectByUsername(username);

        if(!Objects.equals(booking.getUserId(), user.getId()))
        {
            resp.setCode(400);
            resp.setMessage("这不是你的预定单");
            return resp;
        }

        if(booking.getStartDate().isAfter(LocalDate.now())){
            resp.setCode(400);
            resp.setMessage("还未到入住时间");
            return resp;
        }

        Room room = roomMapper.selectById(booking.getRoomId());
        Apartment apartment = apartmentMapper.selectById(room.getApartmentId());

        LocalDate endDate = booking.getStartDate().plusMonths(booking.getLeaseMonths());

        String contractNo = "CONTRACT-" + UUID.randomUUID()
                                        .toString()
                                        .replace("-", "")
                                        .substring(0, 8)
                                        .toUpperCase();

        LocalDateTime signedAt = LocalDateTime.now();

        contract.setContractNo(contractNo);

        contract.setBookingId(bookingId);
        contract.setUserId(user.getId());
        contract.setRoomId(booking.getRoomId());

        contract.setStartDate(booking.getStartDate());
        contract.setEndDate(endDate);

        contract.setRentCent(booking.getRentCent());
        contract.setDepositCent(booking.getDepositCent());
        contract.setPayCycle(apartment.getPayCycle());

        contract.setStatus((byte) 1);

        contract.setSignedAt(signedAt);

        try{
            leaseContractMapper.insertLeaseContract(contract);
            roomMapper.updateRentStatus(booking.getRoomId(), (byte) 3);
            roomBookingMapper.updateBookingStatus(bookingId, (byte) 3);
        }catch(Exception e){
            resp.setCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }

        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(contract);
        return resp;

    }
}
