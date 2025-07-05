package com.kijinkai.domain.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {

    @Schema(description = "user_email", example = "kijinkai@gmail.com")
    @Email(message = "유효한 이메일이여야 합니다.")
    private String email;

    @Schema(description = "user_password", example = "aaa123K1aaa")
    private String password;

    @Schema(description = "user_nickname", example = "kijinkai")
    @NotBlank(message = "닉네임은 필수 입니다.")
    private String nickname;

    @Builder
    public UserRequestDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
