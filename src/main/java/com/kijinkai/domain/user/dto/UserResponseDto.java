package com.kijinkai.domain.user.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserResponseDto {

    private UUID userUuid;
    private String email;
    private String nickname;

    @Builder
    public UserResponseDto(UUID userUuid, String email, String nickname) {
        this.userUuid = userUuid;
        this.email = email;
        this.nickname = nickname;
    }

    @QueryProjection
    public UserResponseDto(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
