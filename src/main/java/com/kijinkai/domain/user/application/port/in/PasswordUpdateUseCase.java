package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.request.UserRequestDto;
import jakarta.mail.MessagingException;

public interface PasswordUpdateUseCase {

    void forgetPassword(UserRequestDto requestDto) throws MessagingException;
    public String verifyResetTokenAndIssueInterim(String token);
    void updatePasswordWithInterimToken(String interimToken, UserRequestDto requestDto);
}
