package com.kijinkai.domain.payment.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawRequest {

    private Long withdrawRequestId;
    private UUID requestUuid;
    private UUID customerUuid;
    private UUID walletUuid;
    private String withdrawCode;
    private BigDecimal requestAmount;
    private BigDecimal withdrawFee;
    private BigDecimal totalDeductAmount;
    private Currency targetCurrency;

    private BankType bankType;
    private String accountNumber;
    private String accountHolder;
    private WithdrawStatus status;
    private UUID processedByAdminUuid;
    private LocalDateTime processedAt;
    private String adminMemo;
    private String rejectionReason;
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // 도메인 로직: 승인
    public void approve(UUID adminUuid, String memo) {
        validateCanApprove();
        this.status = WithdrawStatus.APPROVED;
        this.processedByAdminUuid = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = memo;
    }

    // 도메인 로직: 거절
    public void reject(UUID adminUuid, String reason) {
        validateCanReject();

        this.status = WithdrawStatus.REJECTED;
        this.processedByAdminUuid = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.rejectionReason = reason;

    }

    // 도메인 로직: 송금 완료
    public void completeTransfer(UUID adminUuid, String transferMemo) {
        if (this.status != WithdrawStatus.APPROVED) {
            throw new IllegalStateException("승인된 상태가 아닙니다");
        }

        this.status = WithdrawStatus.COMPLETED;
        this.adminMemo = transferMemo;

    }

    // 비즈니스 규칙 검증
    private void validateCanApprove() {
        if (this.status != WithdrawStatus.PENDING_ADMIN_APPROVAL) {
            throw new IllegalStateException("승인 가능한 상태가 아닙니다: " + this.status);
        }
        validateMinimumAmount();
    }

    private void validateCanReject() {
        if (this.status != WithdrawStatus.PENDING_ADMIN_APPROVAL) {
            throw new IllegalStateException("거절 가능한 상태가 아닙니다: " + this.status);
        }
    }

    private void validateMinimumAmount() {
        BigDecimal minimumAmount = new BigDecimal("5000"); // 최소 출금금액
        if (this.requestAmount.compareTo(minimumAmount) < 0) {
            throw new IllegalArgumentException("최소 출금 금액은 " + minimumAmount + "원입니다");
        }
    }

    public void markAsFailed(String reason){
        if (this.status != WithdrawStatus.PENDING_ADMIN_APPROVAL){
            throw new IllegalStateException("완료된 요청은 실패 될 수 없습니다.");
        }
        this.status = WithdrawStatus.FAILED;
        this.rejectionReason = reason;
    }

    /**
     * 출금 요청 받은 돈은 1000원 상이여야 한다.
     */
    public void validateWithdrawEligibility(WithdrawRequestDto withdrawRequestDto) {
        BigDecimal minimumAmount = new BigDecimal("1000");
        if (withdrawRequestDto.getRequestAmount().compareTo(minimumAmount) < 0) {
            throw new PaymentAmountException("The minimum withdraw is 1000 won");
        }
    }


    public boolean isPendingApproval() {
        return this.status == WithdrawStatus.PENDING_ADMIN_APPROVAL;
    }
}
