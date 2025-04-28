package com.project.main.authentication;

import com.project.main.authToken.AuthToken;
import com.project.main.authToken.AuthTokenRepository;
import com.project.main.authToken.TokenType;
import com.project.main.security.JwtService;
import com.project.main.user.UserAccount;
import com.project.main.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserAccountRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JwtService jwtService;

    public String generateToken(String email) throws IOException {
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return jwtToken;
    }

    public boolean checkIsValidToken(String email, String token) throws IOException {
        var user = userRepository.findByEmail(email)
                .orElseThrow();
        boolean isTokenValid = authTokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        boolean checkIfValid = jwtService.isTokenValid(token, user);
        return isTokenValid && checkIfValid;
    }

    public String revoke(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow();
        revokeAllUserTokens(user);
        return "token revoked";
    }

    private void saveUserToken(UserAccount user, String jwtToken) {
        var token = AuthToken.builder()
                .userAccount(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        authTokenRepository.save(token);
    }

    private void revokeAllUserTokens(UserAccount user) {
        var validUserTokens = authTokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        authTokenRepository.saveAll(validUserTokens);
    }
}
