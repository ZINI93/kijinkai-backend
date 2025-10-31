package com.kijinkai.domain.payment.application.port.in.refund;

import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;

import java.util.UUID;

public interface UpdateRefundUseCase {
    RefundResponseDto approveRefundRequest(UUID refundUuid, UUID adminUuid, String memo);
}
