package com.kijinkai.domain.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Value
@Schema(description = "사용자 정보 업데이트")
public class UserUpdateDto {

    @Schema(description = "사용자 현재 비밀번호", example = "1231aaaA")
    String currentPassword;

    @Schema(description = "사용자 변경 비밀번호", example = "1231aaaA")
    String newPassword;



    @Schema(description = "사용자 닉네임", example = "kijinkai")
    String nickname;

}
