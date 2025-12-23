package com.yasp.mapper;

import com.yasp.entity.LeaseContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LeaseContractMapper {
    // 插入新租赁合同
    void insertLeaseContract(LeaseContract leaseContract);

    // 更新租赁合同状态
    void updateLeaseContractStatus(@Param("contractId") Long contractId, 
                                    @Param("status") Byte status, 
                                    @Param("terminatedAt") LocalDateTime terminatedAt,
                                    @Param("terminateReason") String terminateReason);

    // 根据合同编号查询
    LeaseContract selectByContractNo(@Param("contractNo") String contractNo);

    // 根据房间ID查询相关合同
    List<LeaseContract> selectContractsByRoomId(@Param("roomId") Long roomId);

    // 查询用户的所有租赁合同
    List<LeaseContract> selectContractsByUserId(@Param("userId") Long userId);

    // 查询所有合同（可选条件：状态）
    List<LeaseContract> selectAllContracts(@Param("status") Byte status);
}