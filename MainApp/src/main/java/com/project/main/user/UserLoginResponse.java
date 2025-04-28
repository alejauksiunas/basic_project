package com.project.main.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserLoginResponse {
    private UserAccountDTO userAccount;
    private String token;
}
