package com.kijinkai.domain.payment.application.port.in.refund;

import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;

import java.util.UUID;

public interface CreateRefundUseCase {
    RefundResponseDto processRefundRequest(UUID adminUuid, UUID orderItemUuid, RefundRequestDto requestDto);
}
