package com.yasp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProviderRegisterRequest {

    private String name;
    private Integer type;
    private String defaultAccount;
    private String password;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String licenseNumber;
    private String description;

}
