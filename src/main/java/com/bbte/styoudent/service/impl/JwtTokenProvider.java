package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.security.authentication.Token;
import com.bbte.styoudent.service.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {
    public static final String ACCESS_TOKEN_COOKIE_NAME = "Auth";
    private static final long EXPIRATION_TIME = 864_000_00; // 1 day in ms
    private static SecretKey KEY;
    @Value("${styoudent.config.secret.key}")
    private String secret;

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @PostConstruct
    private void setSecretKey() {
        KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(Claims claims) {
        return extractClaim(claims, Claims::getSubject);
    }

    public Date extractExpiration(Claims claims) {
        return extractClaim(claims, Claims::getExpiration);
    }

    public <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }

    public Claims validateTokenAndExtractAllClaims(String token) {
        try {
            if (token != null && !token.isBlank()) {
                return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
            }

            return Jwts.claims();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new ServiceException("Invalid token!", e);
        }
    }

    private Boolean isTokenExpired(Claims claims) {
        return extractExpiration(claims).before(new Date());
    }

    public Token generateToken(UserDetails user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());

        claims.put(ACCESS_TOKEN_COOKIE_NAME, user.getAuthorities().stream()
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                .collect(Collectors.toList()));

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 8);

        String jwt = Jwts.builder().setClaims(claims).setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY).compact();

        Long duration = now.getTime() + EXPIRATION_TIME;
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        Token token = new Token();
        token.setTokenType(Token.TokenType.ACCESS);
        token.setTokenValue(jwt);
        token.setDuration(duration);
        token.setExpiryDate(LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));

        return token;
    }

    public Boolean validateSubjectAndExpirationOfToken(Claims claims, UserDetails userDetails) {
        final String username = extractUsername(claims);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(claims);
    }
}
