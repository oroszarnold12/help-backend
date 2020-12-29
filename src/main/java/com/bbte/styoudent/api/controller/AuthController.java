package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.PersonAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.dto.PersonDto;
import com.bbte.styoudent.dto.incoming.PersonSignUpDto;
import com.bbte.styoudent.dto.outgoing.ApiResponseMessage;
import com.bbte.styoudent.security.authentication.LoginRequest;
import com.bbte.styoudent.security.authentication.Token;
import com.bbte.styoudent.security.util.CookieUtil;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import com.bbte.styoudent.service.impl.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final PersonService personService;
    private final PersonAssembler personAssembler;

    public AuthController(AuthenticationManager authenticationManager, @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, CookieUtil cookieUtil, PersonService personService, PersonAssembler personAssembler) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieUtil = cookieUtil;
        this.personService = personService;
        this.personAssembler = personAssembler;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponseMessage> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Incorrect username or password", e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        Token newAccessToken = jwtTokenProvider.generateToken(userDetails);
        addAccessTokenCookie(httpHeaders, newAccessToken);
        return ResponseEntity.ok().headers(httpHeaders).body(new ApiResponseMessage("Auth successful. Tokens are created in cookie."));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponseMessage> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
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

        return ResponseEntity.ok().body(new ApiResponseMessage("The logout was successful."));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<PersonDto> signUpPerson(@RequestBody @Valid PersonSignUpDto personSignUpDto) {
        try {
            return new ResponseEntity<>(personAssembler.modelToDto(personService.registerNewPerson(
                    personAssembler.signUpDtoToModel(personSignUpDto))), HttpStatus.OK);
        } catch (ServiceException se) {
            throw new BadRequestException("Person saving failed", se);
        }
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token newAccessToken) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue()).toString());
    }
}
