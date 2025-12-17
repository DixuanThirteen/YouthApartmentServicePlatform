package com.yasp.dto;

import lombok.*;

@Getter
@Setter
public class Response {
    private Integer code;
    private String message;
    private userProfile profile;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class userProfile{
        private String username;
        private String role;
        private String team;//user、admin或者企业名
    }
}
