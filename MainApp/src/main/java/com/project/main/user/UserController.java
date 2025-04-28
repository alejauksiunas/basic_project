package com.project.main.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "api/rest/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?> getByEmail(@RequestParam("email") String email) {
        return userService.getByEmail(email);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("email") String email) {
        return userService.delete(email);
    }

    @PostMapping("/document")
    public ResponseEntity<byte[]> getUserInfoDocument(@RequestBody Map<String, String> requestBody, @RequestHeader Map<String, String> headers) {
        return userService.getUserInfoDocument(requestBody, headers.get("Authorization"));
    }

}
