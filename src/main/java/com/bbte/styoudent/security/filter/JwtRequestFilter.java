package com.bbte.styoudent.security.filter;

import com.bbte.styoudent.api.ApiException;
import com.bbte.styoudent.service.ServiceException;
import com.bbte.styoudent.service.impl.JwtTokenProvider;
import com.bbte.styoudent.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromCookie(request);
            if (StringUtils.hasText(jwt)) {
                Claims claims = jwtTokenProvider.validateTokenAndExtractAllClaims(jwt);

                if(!claims.isEmpty()) {
                    String username = jwtTokenProvider.extractUsername(claims);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtTokenProvider.validateSubjectAndExpirationOfToken(claims, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        } catch (ServiceException se) {
            deleteCookies(request, response);
            throw new ApiException("Unauthorized request", se);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie: request.getCookies()) {
                if (cookie.getName().equals(JwtTokenProvider.ACCESS_TOKEN_COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }
}
