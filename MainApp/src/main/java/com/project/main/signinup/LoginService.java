package com.project.main.signinup;

import com.project.main.email.EmailService;
import com.project.main.security.JwtService;
import com.project.main.user.UserAccountDTO;
import com.project.main.user.UserAccountRepository;
import com.project.main.user.UserAccount;
import com.project.main.user.UserLoginResponse;
import com.project.main.util.EmailValidator;
import com.project.main.util.ResponseMessages;
import com.project.main.util.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserAccountRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailValidator emailValidator;
    private final EmailService emailService;
    private final JwtService jwtService;

    public ResponseEntity<?> register(RegisterRequest request) {
        try {
            boolean isValidEmail = emailValidator.test(request.getEmail());

            if (!isValidEmail) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(String.format(ResponseMessages.EMAIL_NOT_VALID, request.getEmail()));
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(String.format(ResponseMessages.USER_ALREADY_EXISTS, request.getEmail()));
            }
            UserAccount userAccount = UserMapper.INSTANCE.mapAndEncryptPassword(request, bCryptPasswordEncoder);

            userRepository.save(userAccount);
            emailService.sendEmail(request.getEmail());
            return ResponseEntity.ok(ResponseMessages.USER_REGISTERED_SUCCESSFULLY);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                    .body(ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage());
        }
    }

    public UserLoginResponse login(RegisterRequest request) {
        Optional<UserAccount> userAccount = userRepository.findByEmail(request.getEmail());

        if (userAccount.isEmpty() ||
                !bCryptPasswordEncoder.matches(request.getPassword(), userAccount.get().getPassword())) {
            throw new IllegalArgumentException(ResponseMessages.INVALID_LOGIN_CREDENTIALS);
        }

        if (!userAccount.get().isEnabled()) {
            throw new IllegalStateException(ResponseMessages.PLEASE_VERIFY_YOUR_EMAIL);
        }

        try {
            UserAccountDTO userAccountDto = UserMapper.INSTANCE.mapToUserAccountDto(userAccount.get());
            String jwtToken = jwtService.generateToken(userAccount.get());

            return new UserLoginResponse(userAccountDto, jwtToken);
        } catch (Exception e) {
            throw new RuntimeException(ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage(), e);
        }
    }

}
