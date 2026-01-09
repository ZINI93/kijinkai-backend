package com.kijinkai.domain.user.application.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@Schema(description = "사용자 정보 업데이트")
public class UserUpdateDto {


    @Schema(description = "사용자 현재 비밀번호", example = "1231aaaA")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@$%^&*])[a-zA-Z0-9!@$%^&*]{8,20}",
            message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요.")
    String currentPassword;

    @Schema(description = "사용자 변경 비밀번호", example = "1231aaaA")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@$%^&*])[a-zA-Z0-9!@$%^&*]{8,20}",
            message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요.")
    String newPassword;

    @Schema(description = "사용자 닉네임", example = "kijinkai")
    @NotBlank
    String nickname;

}
