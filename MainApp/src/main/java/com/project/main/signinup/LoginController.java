package com.project.main.signinup;

import com.project.main.user.UserLoginResponse;
import com.project.main.util.ResponseMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/rest/auth")
@AllArgsConstructor
@CrossOrigin
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return loginService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest request) {
        try {
            UserLoginResponse userLoginResponse = loginService.login(request);
            return new ResponseEntity<>(userLoginResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMessages.INVALID_LOGIN_CREDENTIALS);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ResponseMessages.PLEASE_VERIFY_YOUR_EMAIL);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
    }

}
