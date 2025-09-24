package com.kijinkai.domain.user.domain.factory;

import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.user.domain.model.UserStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class UserFactory {

    public User createUser(UserRequestDto requestDto, String encodedPassword) {
        return User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();
    }
}