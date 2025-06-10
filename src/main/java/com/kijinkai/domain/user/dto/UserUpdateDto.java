package com.kijinkai.domain.user.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateDto {

    private String password;
    private String nickname;

    @Builder
    public UserUpdateDto(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
