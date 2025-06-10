package com.kijinkai.domain.user.factory;

import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        return User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .build();
    }
}
