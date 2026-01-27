package com.kijinkai.domain.payment.application.port.in.withdraw;

import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;

import java.util.UUID;

public interface UpdateWithdrawUseCase {
    WithdrawResponseDto approveWithdrawRequest(UUID adminUserUuid ,UUID requestUuid, WithdrawRequestDto request);
}
