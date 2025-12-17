package com.yasp.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Apartment implements Serializable {
    private Long id;
    private Long providerId;
    private String name;
    private Integer type;

    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private String coverUrl;
    private String description;

    /** 金额单位：分 */
    private Long rentMinCent;
    private Long rentMaxCent;
    private Long depositCent;

    /** 1=月付 3=季付 6=半年 12=年付 */
    private Integer payCycle;
    private Integer minLeaseMonths;
    private Integer maxLeaseMonths;

    /** 1=active 2=disabled */
    private Integer status;

    /** 0=未上架 1=已上架 */
    private Integer publishStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}