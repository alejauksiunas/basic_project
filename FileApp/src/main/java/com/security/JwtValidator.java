package com.security;

import com.util.SecurityUtil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class JwtValidator {

    @Value("${app.application-secure-properties}")
    private String applicationSecureProperties;

    public void validateToken(String token) throws JwtException, IOException {
        Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token);
    }

    private SecretKey getSignInKey() throws IOException {
        byte[] bytes = Base64.getDecoder().decode(readKey().getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, SecurityUtil.algorithm);
    }

    private String readKey() throws IOException {
        Properties properties = new Properties();
        File file = new File(applicationSecureProperties);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        }
        return properties.getProperty("application.security.jwt.secret-key");
    }
}