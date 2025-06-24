package com.kijinkai.domain.user.factory;

import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createUser(UserRequestDto requestDto, String encodedPassword) {
        return User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .build();
    }
}
