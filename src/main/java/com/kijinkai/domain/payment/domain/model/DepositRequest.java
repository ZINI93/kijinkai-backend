package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositRequest {

    private Long depositRequestId;  // 식별자
    private UUID requestUuid; // 입금 식별자
    private UUID customerUuid; // 고객 식별자
    private UUID walletUuid; // 지갑 식별자
    private BigDecimal amountOriginal;
    private Currency currencyOriginal;
    private BigDecimal amountConverted;
    private BigDecimal exchangeRate;
    private String depositorName;
    private DepositStatus status;
    private LocalDateTime expiresAt;
    private UUID processedByAdminUuid;
    private LocalDateTime processedAt;
    private String adminMemo;
    private String rejectionReason;
    private BankType bankType;
    private Long version;

    private LocalDateTime createdAt;

    public void validateDepositAmount() {

        if (this.amountOriginal.compareTo(new BigDecimal("1000")) < 0) {
            throw new PaymentAmountException("The minimum deposit amount is 1,000 en");
        }

        if (this.amountOriginal.compareTo(new BigDecimal("1000000")) > 0) {
            throw new PaymentAmountException("The maximum deposit amount is 1,000,000 en");
        }
    }


    // 도메인 로직: 승인
    public void approve(UUID adminUuid, String memo) {
        validateCanApprove();
        this.status = DepositStatus.APPROVED;
        this.processedByAdminUuid = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = memo == null ? "특이사항 없음" : memo;
    }

    public void reject(UUID adminUuid, String reason) {
        validateCanReject();
        this.status = DepositStatus.REJECTED;
        this.processedByAdminUuid = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.rejectionReason = reason;

    }

    // 도메인 로직: 만료 처리
    public void expire() {
        if (this.status == DepositStatus.PENDING_ADMIN_APPROVAL &&  !isExpired()) {
            this.status = DepositStatus.EXPIRED;
        }
    }

    // 비즈니스 규칙 검증
    private void validateCanApprove() {
        if (this.status != DepositStatus.PENDING_ADMIN_APPROVAL) {
            throw new IllegalStateException("승인 가능한 상태가 아닙니다: " + this.status);
        }
        if (isExpired()) {
            throw new IllegalStateException("만료된 요청입니다");
        }
    }

    private void validateCanReject() {
        if (this.status != DepositStatus.PENDING_ADMIN_APPROVAL) {
            throw new IllegalStateException("거절 가능한 상태가 아닙니다: " + this.status);
        }
    }

    /**
     *
     * @param rejectionReason
     */
    public void markAsFailed(String rejectionReason) {
        if (this.status == DepositStatus.APPROVED) {
            throw new IllegalStateException("완료된 요청은 실패 처리 될 수 없습니다.");
        }
        this.status = DepositStatus.REJECTED;
        this.rejectionReason = rejectionReason;
    }


    public boolean isPendingApproval() {
        return this.status == DepositStatus.PENDING_ADMIN_APPROVAL;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }


}
