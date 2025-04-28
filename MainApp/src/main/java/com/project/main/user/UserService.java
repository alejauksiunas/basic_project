package com.project.main.user;

import com.project.main.accountVerification.TokenService;
import com.project.main.util.ResponseMessages;
import com.project.main.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Value("${app.document-app.url}")
    private String baseFileAppUrl;
    @Value("${app.document-app.generate-pdf.url}")
    private String pdfDocumentApi;

    private final UserAccountRepository userRepository;
    private final TokenService tokenService;

    public ResponseEntity<?> getByEmail(String email) {
        try {
            Optional<UserAccount> userAccount  = userRepository.findByEmail(email);
            if (userAccount.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format(ResponseMessages.USER_NOT_FOUND_MSG, email));
            }

            UserAccountDTO userAccountDto = UserMapper.INSTANCE.mapToUserAccountDto(userAccount.get());

            return ResponseEntity.ok(userAccountDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                    .body(ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage());
        }
    }

//    public UserAccountDTO getByEmail(String email) {
//        try {
//            Optional<UserAccount> userAccount = userRepository.findByEmail(email);
//            if (userAccount.isEmpty()) {
//                throw new RuntimeException(String.format(ResponseMessages.USER_NOT_FOUND_MSG, email));
//            }
//
//            return UserMapper.INSTANCE.mapToUserAccountDto(userAccount.get());
//        } catch (Exception e) {
//            throw new RuntimeException(ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage());
//        }
//    }

    public ResponseEntity<?> delete(String request) {
        try {
            Optional<UserAccount> userOptional = userRepository.findByEmail(request);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format(ResponseMessages.USER_NOT_FOUND_MSG, request));
            }

            userRepository.delete(userOptional.get());
            return ResponseEntity.ok(ResponseMessages.USER_DELETED_SUCCESSFULLY);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                    .body(ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage());
        }
    }

    public ResponseEntity<?> enable(String token) {
        try {
            ResponseEntity<?> confirmationResponse = tokenService.confirmToken(token);

            if (confirmationResponse.getStatusCode() == HttpStatus.OK) {
                userRepository.enableUser(Objects.requireNonNull(confirmationResponse.getBody()).toString());
                return ResponseEntity.ok(ResponseMessages.USER_ENABLED);
            } else {
                return confirmationResponse;
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                    .body(ResponseMessages.SOMETHING_BAD_HAPPENED + e.getMessage());
        }
    }

    public ResponseEntity<byte[]> getUserInfoDocument(Map<String, String> requestBody, String bearerToken) {
        String email = requestBody.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseMessages.EMAIL_PARAMETER_MISSING.getBytes(StandardCharsets.UTF_8));
        }
        ResponseEntity<?> userAccount = getByEmail(email);

        String documentServiceUrl = UriComponentsBuilder.fromHttpUrl(baseFileAppUrl)
                .path(pdfDocumentApi)
                .build()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));
        headers.add("Authorization", bearerToken);

        HttpEntity<Object> httpEntity = new HttpEntity<>(userAccount.getBody(), headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                documentServiceUrl,
                HttpMethod.POST,
                httpEntity,
                byte[].class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentDispositionFormData("filename", email + ".pdf");

        return ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseHeaders)
                .body(responseEntity.getBody());
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(ResponseMessages.USER_NOT_FOUND_MSG, email)));
    }
}
