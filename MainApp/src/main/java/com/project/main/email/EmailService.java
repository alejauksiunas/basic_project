package com.project.main.email;

import com.project.main.accountVerification.TokenService;
import com.project.main.user.UserAccount;
import com.project.main.user.UserAccountRepository;
import com.project.main.util.ResponseMessages;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    @Value("${spring.mail.username}")
    private String sendFrom;
    @Value("${app.generic-app.url}")
    private String baseAppUrl;

    private final JavaMailSender mailSender;
    private final EmailTemplate emailTemplate;
    private final TokenService tokenService;
    private final UserAccountRepository userRepository;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(EmailUtils.NEW_ACCOUNT_VERIFICATION);
            helper.setFrom(sendFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException(ResponseMessages.EMAIL_SENDING_FAILED + e.getMessage());
        }
    }

    public String sendEmail(String email) {
        try {
            Optional<UserAccount> userAccount = userRepository.findByEmail(email);
            if (userAccount.isPresent()) {
                String link = UriComponentsBuilder.fromHttpUrl(baseAppUrl)
                        .path(EmailUtils.EMAIL_VERIFY_URL)
                        .queryParam("token", tokenService.generateToken(userAccount.get()))
                        .build()
                        .toUriString();
                send(email, emailTemplate.buildEmail(userAccount.get().getName(), link));
                return ResponseMessages.EMAIL_SENT;
            } else {
                return String.format(ResponseMessages.USER_NOT_FOUND_MSG, email);
            }
        } catch (Exception e) {
            return ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage();
        }
    }

}
