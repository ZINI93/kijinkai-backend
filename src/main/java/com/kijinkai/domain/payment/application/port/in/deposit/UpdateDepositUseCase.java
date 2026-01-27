package com.kijinkai.domain.payment.application.port.in.deposit;

import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;

import java.util.UUID;

public interface UpdateDepositUseCase {
    DepositRequestResponseDto approveDepositRequest(UUID userUuid, UUID depositUuid, DepositRequestDto requestDto);
}
