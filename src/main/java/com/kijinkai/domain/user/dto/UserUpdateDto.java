package com.kijinkai.domain.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자 정보 업데이트")
public class UserUpdateDto {

    @Schema(description = "사용자 비밀번호", example = "1231aaaA")
    private String password;

    @Schema(description = "사용자 닉네임", example = "kijinkai")
    private String nickname;

    @Builder
    public UserUpdateDto(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}
