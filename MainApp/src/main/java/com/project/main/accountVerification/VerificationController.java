package com.project.main.accountVerification;

import com.project.main.email.EmailService;
import com.project.main.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/verify")
@AllArgsConstructor
public class VerificationController {

    private final EmailService emailService;
    private final UserService userService;

    @PostMapping("/send")
    public String send(@RequestParam("email") String email) {
        return emailService.sendEmail(email);
    }

    //TODO: until proper frontend is done
//    @GetMapping("/enable")
//    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
//        return userService.enable(token);
//    }

}
