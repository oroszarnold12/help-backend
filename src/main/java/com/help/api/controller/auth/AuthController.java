package com.help.api.controller.auth;

import com.help.api.assembler.PersonAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.dto.outgoing.ApiResponseMessage;
import com.help.dto.outgoing.person.PersonDto;
import com.help.model.person.Person;
import com.help.security.authentication.LoginRequest;
import com.help.security.authentication.Token;
import com.help.security.util.AuthUtil;
import com.help.security.util.CookieUtil;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import com.help.service.impl.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@CrossOrigin
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

    public AuthController(AuthenticationManager authenticationManager,
                          @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                          JwtTokenProvider jwtTokenProvider, CookieUtil cookieUtil,
                          PersonService personService, PersonAssembler personAssembler) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieUtil = cookieUtil;
        this.personService = personService;
        this.personAssembler = personAssembler;
    }

    @PostMapping("/login")
    public ResponseEntity<PersonDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Incorrect username or password.", e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        Token newAccessToken = jwtTokenProvider.generateToken(userDetails);
        addAccessTokenCookie(httpHeaders, newAccessToken);
        return ResponseEntity.ok().headers(httpHeaders).body(personAssembler.modelToDto(
                personService.getPersonByEmail(loginRequest.getUsername())));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponseMessage> logout(HttpServletRequest request, HttpServletResponse response) {
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
            person.setNotificationToken(null);

            try {
                personService.savePerson(person);
            } catch (ServiceException serviceException) {
                throw new InternalServerException("Could not delete notification subscription!", serviceException);
            }
        }

        SecurityContextHolder.clearContext();

        cookieUtil.deleteAccessTokenCookie(request, response);

        return ResponseEntity.ok().body(new ApiResponseMessage("The logout was successful."));
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token newAccessToken) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue()).toString());
    }
}
