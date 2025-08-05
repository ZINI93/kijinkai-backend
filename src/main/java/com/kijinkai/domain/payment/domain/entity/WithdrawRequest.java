package com.kijinkai.domain.payment.domain.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "withdraw_requests")
@Entity
public class WithdrawRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_request_id", nullable = false, updatable = false)
    private Long withdrawRequestId;

    @Column(name = "request_uuid", nullable = false, updatable = false, unique = true)
    private UUID requestUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    // 출금 요청 금액
    @Column(name = "request_amount", nullable = false)
    private BigDecimal requestAmount;

    // 수수료
    @Column(name = "withdraw_fee", nullable = false)
    private BigDecimal withdrawFee;

    // 총 차감 금액 (요청금액 + 수수료)
    @Column(name = "total_deduct_amount", nullable = false)
    private BigDecimal totalDeductAmount;

    // 환전 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false)
    private Currency targetCurrency;

    @Column(name = "converted_amount")
    private BigDecimal convertedAmount;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    // 출금 계좌 정보
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false)
    private String accountHolder;

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WithdrawStatus status;

    // 관리자 처리 정보
    @Column(name = "processed_by_admin")
    private UUID processedByAdmin;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_memo")
    private String adminMemo;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Version
    private Long version;

    @Builder
    public WithdrawRequest(UUID customerUuid, UUID walletUuid, BigDecimal requestAmount,
                           BigDecimal withdrawFee, Currency targetCurrency, BigDecimal convertedAmount,
                           String bankName, String accountNumber, String accountHolder, LocalDateTime expiresAt) {

        this.requestUuid = UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.walletUuid = walletUuid;
        this.requestAmount = requestAmount;
        this.convertedAmount = convertedAmount;
        this.withdrawFee = withdrawFee;
        this.totalDeductAmount = requestAmount.add(withdrawFee);
        this.targetCurrency = targetCurrency;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.expiresAt = expiresAt;
        this.status = WithdrawStatus.PENDING_ADMIN_APPROVAL;
    }

    // 도메인 로직: 승인
    public void approve(UUID adminUuid, String memo, BigDecimal exchangeRate) {
        validateCanApprove();

        this.status = WithdrawStatus.APPROVED;
        this.processedByAdmin = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = memo;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = calculateConvertedAmount(exchangeRate);
    }

    // 도메인 로직: 거절
    public void reject(UUID adminUuid, String reason) {
        validateCanReject();

        this.status = WithdrawStatus.REJECTED;
        this.processedByAdmin = adminUuid;
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
        BigDecimal minimumAmount = new BigDecimal("20000"); // 2만엔 최소
        if (this.requestAmount.compareTo(minimumAmount) < 0) {
            throw new IllegalArgumentException("최소 출금 금액은 " + minimumAmount + "엔입니다");
        }
    }

    public void markAsFailed(String reason){
        if (this.status == WithdrawStatus.APPROVED){
            throw new IllegalStateException("완료된 요청은 실패 될 수 없습니다.");
        }
        this.status = WithdrawStatus.FAILED;
        this.rejectionReason = reason;
    }

    private BigDecimal calculateConvertedAmount(BigDecimal rate) {
        return this.convertedAmount.multiply(rate);
    }

    public boolean isPendingApproval() {
        return this.status == WithdrawStatus.PENDING_ADMIN_APPROVAL;
    }
}
