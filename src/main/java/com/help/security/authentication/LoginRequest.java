package com.help.security.authentication;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
