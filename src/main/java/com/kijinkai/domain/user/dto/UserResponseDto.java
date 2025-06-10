package com.kijinkai.domain.user.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    private String userUuid;
    private String email;
    private String nickname;

    @Builder
    public UserResponseDto(String userUuid, String email, String nickname) {
        this.userUuid = userUuid;
        this.email = email;
        this.nickname = nickname;
    }
}
