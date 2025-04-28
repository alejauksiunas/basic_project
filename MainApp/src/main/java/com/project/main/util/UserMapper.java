package com.project.main.util;

import com.project.main.signinup.RegisterRequest;
import com.project.main.user.UserAccount;
import com.project.main.user.UserAccountDTO;
import com.project.main.user.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "password", ignore = true)
    UserAccount registerRequestToUserAccount(RegisterRequest registerRequest);

    default UserAccount mapAndEncryptPassword(RegisterRequest registerRequest, BCryptPasswordEncoder passwordEncoder) {
        UserAccount userAccount = registerRequestToUserAccount(registerRequest);
        userAccount.setUserRole(UserRole.USER);
        userAccount.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return userAccount;
    }

    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname")
    @Mapping(source = "email", target = "email")
    UserAccountDTO mapToUserAccountDto(UserAccount userAccount);
}

