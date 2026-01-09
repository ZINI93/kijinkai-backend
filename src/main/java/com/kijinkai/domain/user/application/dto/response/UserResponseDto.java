package com.kijinkai.domain.user.application.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@Schema(description = "사용자 정보 응답")
public class UserResponseDto {

    @Schema(description = "사용자 고유 식별자", example = "xxxx-xxxx")
    private UUID userUuid;

    @Schema(description = "사용자 이메일", example = "kijinkai@gmail.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "kijinkai")
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
