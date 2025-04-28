package com.project.main;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class JwtSecretGenerator {

    @Test
    @Disabled // Run it once to generate your secret key, then disable it
    public void generateJwtSecret() {
        SecretKey key = Jwts.SIG.HS512.key().build();
        System.out.printf("\nKey = [%s]\n", DatatypeConverter.printHexBinary(key.getEncoded()));
    }
}
