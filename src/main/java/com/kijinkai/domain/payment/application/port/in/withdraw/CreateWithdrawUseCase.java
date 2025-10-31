package com.kijinkai.domain.payment.application.port.in.withdraw;

import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;

import java.util.UUID;

public interface CreateWithdrawUseCase {
    WithdrawResponseDto processWithdrawRequest(UUID userUuid, WithdrawRequestDto requestDto);
}
