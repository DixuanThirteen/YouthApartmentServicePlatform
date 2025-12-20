package com.yasp.dto;

import lombok.*;

@Getter
@Setter
public class Response<T> {
    private Integer code;
    private String message;
    private userProfile profile;
    private T Data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class userProfile{
        private String username;
        private String role;
        private String team;//user、admin或者企业名
    }

    public Response(T data) {
        this.Data = data;
    }

    public static Response<String> fail(Integer code, String message) {
        Response<String> response = new Response<>("fail");
        response.setCode(code);
        response.setMessage("Operation failed: " + message);
        return response;
    }
}
