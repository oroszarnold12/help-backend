package com.bbte.styoudent.security.util;

import com.bbte.styoudent.service.impl.JwtTokenProvider;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {
    public HttpCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME, token)
                .maxAge(-1)
                // .secure(true)
                // .sameSite("None")
                .httpOnly(true)
                .path("/")
                .build();
    }

    public void deleteAccessTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }
}
