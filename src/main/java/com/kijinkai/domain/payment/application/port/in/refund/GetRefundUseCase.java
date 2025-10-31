package com.kijinkai.domain.payment.application.port.in.refund;

import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetRefundUseCase {
    RefundResponseDto getRefundInfo(UUID refundUuid, UUID userUuid);
    Page<RefundResponseDto> getRefunds(UUID adminUuid, Pageable pageable);
    RefundResponseDto getRefundInfoByAdmin(UUID refundUuid, UUID adminUuid);
}
