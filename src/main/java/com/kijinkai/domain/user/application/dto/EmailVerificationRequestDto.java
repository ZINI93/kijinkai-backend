package com.kijinkai.domain.user.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailVerificationRequestDto {

    private String email;
    private String verificationCode;
}
