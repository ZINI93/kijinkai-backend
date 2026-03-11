package com.kijinkai.domain.payment.application.port.in.deposit;

import com.kijinkai.domain.payment.adapter.in.web.deposit.DepositSearchConditionDto;
import com.kijinkai.domain.payment.application.dto.DepositAdminSearchDto;
import com.kijinkai.domain.payment.application.dto.DepositAdminSummaryDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetDepositUseCase {
    DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid);
    DepositRequestResponseDto getDepositRequestInfoByAdmin(UUID requestUuid, UUID adminUuid);
    Page<DepositRequestResponseDto> getDepositsByStatus(UUID userUuid, DepositStatus status, Pageable pageable);
    Page<DepositRequestResponseDto> getDeposits(UUID userUuid, Pageable pageable);
    List<DepositRequestResponseDto> expireOldRequests();

    Page<DepositAdminSearchDto> searchDepositsByAdmin(UUID userAdminUuid,DepositSearchConditionDto conditionDto, Pageable pageable);
    DepositAdminSummaryDto summaryByAdmin(UUID userAdminUuid, LocalDate date);
}
