package com.kijinkai.domain.payment.application.port.in.withdraw;

import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetWithdrawUseCase {
    WithdrawResponseDto getWithdrawInfo(UUID requestUuid, UUID userUuid);
    WithdrawResponseDto getWithdrawInfoByAdmin(UUID requestUuid, UUID adminUuid);
    Page<WithdrawResponseDto> getWithdraws(UUID adminUuid, Pageable pageable);
    Page<WithdrawResponseDto> getWithdrawByApprovalPending(UUID adminUuid, String withdrawName, Pageable pageable);
}
