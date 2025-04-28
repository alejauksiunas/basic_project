package com.project.main.authentication;

import com.project.main.security.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/generate")
    public String authenticate(@RequestParam String email) throws IOException {
        return authenticationService.generateToken(email);
    }

    @PostMapping("/revoke")
    public String change(@RequestParam String email) {
        return authenticationService.revoke(email);
    }

    @PostMapping("/isvalid")
    public boolean validToken(@RequestParam String email, @RequestParam String token) throws IOException {
        return authenticationService.checkIsValidToken(email, token);
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<byte[]> generate(@RequestHeader("Authorization") String authorizationHeader) throws IOException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        try {
            jwtService.validateToken(token);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
