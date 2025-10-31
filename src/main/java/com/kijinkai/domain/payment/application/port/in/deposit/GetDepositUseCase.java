package com.kijinkai.domain.payment.application.port.in.deposit;

import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GetDepositUseCase {
    DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid);
    DepositRequestResponseDto getDepositRequestInfoByAdmin(UUID requestUuid, UUID adminUuid);
    Page<DepositRequestResponseDto> getDepositsByApprovalPending(UUID adminUuid, String depositorName, Pageable pageable);
    Page<DepositRequestResponseDto> getDeposits(UUID userUuid, Pageable pageable);
    List<DepositRequestResponseDto> expireOldRequests();
}
