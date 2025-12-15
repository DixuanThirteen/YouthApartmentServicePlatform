package com.yasp.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Provider {

    private Long id;

    private String name;

    private Integer type;

    private String contactPerson;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String licenseNumber;

    private String description;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
