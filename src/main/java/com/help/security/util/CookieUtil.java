package com.help.security.util;

import org.springframework.http.HttpCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CookieUtil {
    HttpCookie createAccessTokenCookie(String token);

    void deleteAccessTokenCookie(HttpServletRequest request, HttpServletResponse response);
}
