package com.kijinkai.domain.user.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {

    private String email;
    private String password;
    private String nickname;

    @Builder
    public UserRequestDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
