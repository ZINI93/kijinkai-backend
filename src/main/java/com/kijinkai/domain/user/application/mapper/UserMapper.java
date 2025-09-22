package com.kijinkai.domain.user.application.mapper;

import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponse(User user){

        return UserResponseDto.builder()
                .userUuid(user.getUserUuid())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();

    }
}
