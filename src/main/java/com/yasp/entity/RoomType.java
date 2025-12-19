package com.yasp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoomType {
    private Long id;

    private Long apartmentId;
    private String name;

    private Integer bedroomCount;
    private Integer livingCount;
    private Integer bathroomCount;
    private Integer kitchenCount;

    private BigDecimal areaSqm;
    private Integer bedCount;
    private Integer capacity;

    private Integer rentCent;
    private Integer depositCent;

    private Integer hasWindow;   // 0/1
    private Integer hasBalcony;  // 0/1
    private Integer orientation; // 1东2南3西4北...

    private String description;

    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomType() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getBedroomCount() { return bedroomCount; }
    public void setBedroomCount(Integer bedroomCount) { this.bedroomCount = bedroomCount; }

    public Integer getLivingCount() { return livingCount; }
    public void setLivingCount(Integer livingCount) { this.livingCount = livingCount; }

    public Integer getBathroomCount() { return bathroomCount; }
    public void setBathroomCount(Integer bathroomCount) { this.bathroomCount = bathroomCount; }

    public Integer getKitchenCount() { return kitchenCount; }
    public void setKitchenCount(Integer kitchenCount) { this.kitchenCount = kitchenCount; }

    public BigDecimal getAreaSqm() { return areaSqm; }
    public void setAreaSqm(BigDecimal areaSqm) { this.areaSqm = areaSqm; }

    public Integer getBedCount() { return bedCount; }
    public void setBedCount(Integer bedCount) { this.bedCount = bedCount; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getRentCent() { return rentCent; }
    public void setRentCent(Integer rentCent) { this.rentCent = rentCent; }

    public Integer getDepositCent() { return depositCent; }
    public void setDepositCent(Integer depositCent) { this.depositCent = depositCent; }

    public Integer getHasWindow() { return hasWindow; }
    public void setHasWindow(Integer hasWindow) { this.hasWindow = hasWindow; }

    public Integer getHasBalcony() { return hasBalcony; }
    public void setHasBalcony(Integer hasBalcony) { this.hasBalcony = hasBalcony; }

    public Integer getOrientation() { return orientation; }
    public void setOrientation(Integer orientation) { this.orientation = orientation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}