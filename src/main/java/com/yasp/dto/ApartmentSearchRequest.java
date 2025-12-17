package com.yasp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ApartmentSearchRequest {
    private Long id;
    private Long providerId;

    private String keyword;   // name/address/description 模糊
    private String nameLike;  // 仅 name 模糊

    private Integer type;         // tinyint
    private Integer status;       // tinyint
    private Integer publishStatus;// tinyint
    private Integer payCycle;     // tinyint

    private String provinceCode;
    private String cityCode;
    private String districtCode;

    private Integer rentMinCentGte;
    private Integer rentMaxCentLte;

    private Integer depositCentGte;
    private Integer depositCentLte;

    private Integer minLeaseMonthsGte;
    private Integer maxLeaseMonthsLte;

    private BigDecimal latMin;
    private BigDecimal latMax;
    private BigDecimal lngMin;
    private BigDecimal lngMax;

    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;

    private Integer limit;
    private Integer offset;

    // ⚠️注意：orderBy 建议改成枚举而不是字符串
    private String orderBy;

    // getters/setters 省略
}