package com.kijinkai.domain.user.mapper;

import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.entity.User;
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
