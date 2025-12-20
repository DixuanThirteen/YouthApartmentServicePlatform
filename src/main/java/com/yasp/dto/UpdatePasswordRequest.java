package com.yasp.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    String oldPassword;
    String newPassword;
    String reNewPassword;
}
