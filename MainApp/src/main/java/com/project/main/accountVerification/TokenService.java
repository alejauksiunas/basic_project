package com.project.main.accountVerification;

import com.project.main.user.UserAccount;
import com.project.main.util.ResponseMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public String generateToken(UserAccount userAccount) {
        try {
            String token = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    userAccount
            );
            confirmationTokenRepository.save(confirmationToken);
            return confirmationToken.getToken();
        } catch (Exception e) {
            return ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage();
        }
    }

    public ResponseEntity<?> confirmToken(String token) {
        Optional<ConfirmationToken> confirmationToken  = confirmationTokenRepository.findByToken(token);
        LocalDateTime timeNow = LocalDateTime.now();

        if (confirmationToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseMessages.TOKEN_NOT_FOUND);
        }

        if (confirmationToken.get().getConfirmedAt() != null) {
            return ResponseEntity.status(HttpStatus.IM_USED)
                    .body(ResponseMessages.TOKEN_ALREADY_CONFIRMED);
        }

        if (confirmationToken.get().getExpiresAt().isBefore(timeNow)) {
            return ResponseEntity.status(HttpStatus.IM_USED)
                    .body(ResponseMessages.TOKEN_EXPIRED);
        }

        confirmationTokenRepository.updateConfirmedAt(timeNow, token);

        return ResponseEntity.status(HttpStatus.OK)
                .body(confirmationToken.get().getUserAccount().getEmail());
    }

}
