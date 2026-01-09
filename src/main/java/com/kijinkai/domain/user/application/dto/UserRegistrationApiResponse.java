package com.kijinkai.domain.user.application.dto;

import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "사용자 등록 응답")
public class UserRegistrationApiResponse {
    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "응답 메시지", example = "회원가입이 성공적으로 완료되었습니다.")
    private String message;

    @Schema(description = "등록된 사용자 정보")
    private UserResponseDto data;

    // constructors, getters, setters
}

