package com.bbte.styoudent.security.authentication;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Token {
    private TokenType tokenType;
    private String tokenValue;
    private Long duration;
    private LocalDateTime expiryDate;

    public enum TokenType {
        ACCESS, REFRESH
    }
}
