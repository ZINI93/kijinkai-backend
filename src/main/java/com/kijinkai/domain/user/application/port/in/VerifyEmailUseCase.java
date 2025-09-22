package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.EmailVerificationRequestDto;

public interface VerifyEmailUseCase {
    void verifyEmail(EmailVerificationRequestDto requestDto);
    void resendCode(String email);

}
