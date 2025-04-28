package com.project.main.security;

import com.project.main.util.ObtainSecretJwtKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${app.application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${app.application.security.jwt.refresh.expiration}")
    private long jwtRefreshExpiration;

    private final ObtainSecretJwtKey obtainSecretJwtKey;

    public boolean isTokenValid(String token, UserDetails userDetails) throws IOException {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String jwt) throws IOException {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().before(Date.from(Instant.now()));
    }

    private Claims getClaims(String token) throws IOException {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) throws IOException {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws IOException {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    public void validateToken(String token) throws JwtException, IOException {
        Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token);
    }

    public String generateToken(UserDetails userDetails) throws IOException {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMillis(jwtExpiration))))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() throws IOException {
        byte[] bytes = Base64.getDecoder().decode(obtainSecretJwtKey.readKey().getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA512");
    }
}
