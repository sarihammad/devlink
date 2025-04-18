package com.devlink.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}