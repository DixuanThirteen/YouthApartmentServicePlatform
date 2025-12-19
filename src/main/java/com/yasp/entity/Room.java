package com.yasp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Room {
    private Long id;

    private Long apartmentId;
    private Long roomTypeId;

    private String buildingNo;
    private String unitNo;
    private Integer floorNo;
    private String roomNo;
    private String displayName;

    // generated columns (stored) - 通常不需要插入/更新，但可以保留用于查询展示
    private String buildingNoNorm;
    private String unitNoNorm;
    private String roomNoNorm;

    private BigDecimal areaSqm;
    private Integer orientation;

    private Integer rentCent;
    private Integer depositCent;

    private Integer status;
    private Integer rentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String roomCode;

    public Room() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }

    public Long getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(Long roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getBuildingNo() { return buildingNo; }
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }

    public String getUnitNo() { return unitNo; }
    public void setUnitNo(String unitNo) { this.unitNo = unitNo; }

    public Integer getFloorNo() { return floorNo; }
    public void setFloorNo(Integer floorNo) { this.floorNo = floorNo; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getBuildingNoNorm() { return buildingNoNorm; }
    public void setBuildingNoNorm(String buildingNoNorm) { this.buildingNoNorm = buildingNoNorm; }

    public String getUnitNoNorm() { return unitNoNorm; }
    public void setUnitNoNorm(String unitNoNorm) { this.unitNoNorm = unitNoNorm; }

    public String getRoomNoNorm() { return roomNoNorm; }
    public void setRoomNoNorm(String roomNoNorm) { this.roomNoNorm = roomNoNorm; }

    public BigDecimal getAreaSqm() { return areaSqm; }
    public void setAreaSqm(BigDecimal areaSqm) { this.areaSqm = areaSqm; }

    public Integer getOrientation() { return orientation; }
    public void setOrientation(Integer orientation) { this.orientation = orientation; }

    public Integer getRentCent() { return rentCent; }
    public void setRentCent(Integer rentCent) { this.rentCent = rentCent; }

    public Integer getDepositCent() { return depositCent; }
    public void setDepositCent(Integer depositCent) { this.depositCent = depositCent; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getRentStatus() { return rentStatus; }
    public void setRentStatus(Integer rentStatus) { this.rentStatus = rentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
}