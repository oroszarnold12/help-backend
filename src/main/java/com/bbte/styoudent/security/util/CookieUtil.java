package com.bbte.styoudent.security.util;

import com.bbte.styoudent.service.impl.JwtTokenProvider;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public HttpCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME, token)
                .maxAge(-1)
                .httpOnly(true)
                .path("/")
                .build();
    }

    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME, "").maxAge(0).httpOnly(true).path("/").build();
    }
}
