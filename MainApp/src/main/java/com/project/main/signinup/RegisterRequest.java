package com.project.main.signinup;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterRequest {
    private final String name;
    private final String surname;
    private final String email;
    private final String password;
}