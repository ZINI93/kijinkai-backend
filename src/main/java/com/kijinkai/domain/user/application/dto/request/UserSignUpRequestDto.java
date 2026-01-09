package com.kijinkai.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSignUpRequestDto {
    public interface existsGroup {
    }

    public interface createGroup {
    }

    public interface passwordGroup {
    }

    public interface updateGroup {
    }

    public interface deleteGroup {
    }

    @Schema(description = "사용자 이메일", example = "kijinkai@gmail.com")
    @Email(message = "유효한 이메일이여야 합니다.")
    @NotBlank(groups = {UserSignUpRequestDto.existsGroup.class, UserRequestDto.createGroup.class, UserRequestDto.updateGroup.class, UserRequestDto.deleteGroup.class})
    private String email;

    @Schema(description = "사용자 비밀번호", example = "aaa123K1aaA!")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@$%^&*])[a-zA-Z0-9!@$%^&*]{8,20}$",
            message = "영문, 숫자, 특수문자를 포함한 8~20자리로 입력해주세요.",
            groups = {UserSignUpRequestDto.createGroup.class, UserRequestDto.passwordGroup.class}
    )
    private String password;

    @Schema(description = "사용자 닉네임", example = "kijinkai")
    @NotBlank(groups = {UserSignUpRequestDto.createGroup.class, UserRequestDto.updateGroup.class}, message = "닉네임은 필수 입니다.")
    private String nickname;

    @Schema(description = "구매자 이름", example = "Jinhee")
    @NotBlank(groups = {UserSignUpRequestDto.createGroup.class, UserSignUpRequestDto.updateGroup.class}, message = "이름은 필수 입니다.")
    private String firstName;

    @Schema(description = "구매자 성", example = "Park")
    @NotBlank(groups = {UserSignUpRequestDto.createGroup.class, UserSignUpRequestDto.updateGroup.class},message = "성은 필수 입니다.")
    private String lastName;

    @Schema(description = "휴대폰 번호", example = "080-1234-1234")
    @NotBlank(groups = {UserSignUpRequestDto.createGroup.class, UserSignUpRequestDto.updateGroup.class},message = "전화 번호는 필수 입니다.")
    private String phoneNumber;

    public UserSignUpRequestDto() {
    }

    @Builder
    public UserSignUpRequestDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
