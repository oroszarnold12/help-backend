package com.bbte.styoudent.security.authentication;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
