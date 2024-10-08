package com.help.security.util;

import com.help.service.impl.JwtTokenProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Profile("prod")
@Component
public class CookieUtilProdImpl implements CookieUtil {
    @Override
    public HttpCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME, token)
                .maxAge(-1)
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .path("/")
                .build();
    }

    @Override
    public void deleteAccessTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setSecure(true);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }
}

