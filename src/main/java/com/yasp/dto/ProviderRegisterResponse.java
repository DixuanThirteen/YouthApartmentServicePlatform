package com.yasp.dto;

import lombok.*;

@Getter
@Setter
public class ProviderRegisterResponse {
    private String name;
    private Integer type;
    private accountProfile profile;
    private Integer code;
    private String message;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class accountProfile{
        private String username;
        private String password;
        private Integer role;
    }
}
