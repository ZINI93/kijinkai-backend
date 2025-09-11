package com.kijinkai.domain.payment.domain.entity;


import com.kijinkai.domain.common.BaseEntity;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentMethod;
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
@Table(name = "deposit_requests")
@Entity
public class DepositRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_request_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "request_uuid", nullable = false, updatable = false, unique = true)
    private UUID requestUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    // 원본 금액 정보
    @Column(name = "amount_original", nullable = false)
    private BigDecimal amountOriginal;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_original", nullable = false)
    private Currency currencyOriginal;

    // 변환된 금액 정보
    @Column(name = "amount_converted", nullable = false)
    private BigDecimal amountConverted;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    // 입금자 정보
    @Column(name = "depositor_name", nullable = false)
    private String depositorName;

    @Column(name = "bank_account")
    private String bankAccount;

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DepositStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 관리자 처리 정보
    @Column(name = "processed_by_admin")
    private UUID processedByAdmin;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_memo")
    private String adminMemo;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "bank_type", nullable = false)
    private BankType bankType;

    @Version
    private Long version;

    @Builder
    public DepositRequest(UUID customerUuid, UUID walletUuid, BigDecimal amountOriginal,
                          Currency currencyOriginal, BigDecimal amountConverted,
                          BigDecimal exchangeRate, String depositorName,
                          BankType bankType) {
        this.requestUuid = UUID.randomUUID();
        this.customerUuid = customerUuid;
        this.walletUuid = walletUuid;
        this.amountOriginal = amountOriginal;
        this.currencyOriginal = currencyOriginal;
        this.amountConverted = amountConverted;
        this.exchangeRate = exchangeRate;
        this.depositorName = depositorName;
        this.bankType = bankType;
        this.status = DepositStatus.PENDING_ADMIN_APPROVAL;
        this.expiresAt = LocalDateTime.now().plusDays(7); // 7일 후 만료
    }

    @Builder
    public DepositRequest(UUID requestUuid, UUID customerUuid, UUID walletUuid, BigDecimal amountOriginal, Currency currencyOriginal, BigDecimal amountConverted, BigDecimal exchangeRate, String depositorName, String bankAccount, DepositStatus status, LocalDateTime expiresAt, UUID processedByAdmin, LocalDateTime processedAt,
                          String adminMemo, String rejectionReason, BankType bankType) {
        this.requestUuid = requestUuid;
        this.customerUuid = customerUuid;
        this.walletUuid = walletUuid;
        this.amountOriginal = amountOriginal;
        this.currencyOriginal = currencyOriginal;
        this.amountConverted = amountConverted;
        this.exchangeRate = exchangeRate;
        this.depositorName = depositorName;
        this.bankAccount = bankAccount;
        this.status = status;
        this.expiresAt = expiresAt;
        this.processedByAdmin = processedByAdmin;
        this.processedAt = processedAt;
        this.adminMemo = adminMemo;
        this.rejectionReason = rejectionReason;
        this.bankType = bankType;
    }

    // 도메인 로직: 승인
    public void approve(UUID adminUuid, String memo) {
        validateCanApprove();
        this.status = DepositStatus.APPROVED;
        this.processedByAdmin = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.adminMemo = memo;
    }

    public void reject(UUID adminUuid, String reason) {
        validateCanReject();
        this.status = DepositStatus.REJECTED;
        this.processedByAdmin = adminUuid;
        this.processedAt = LocalDateTime.now();
        this.rejectionReason = reason;

    }


    // 도메인 로직: 만료 처리
    public void expire() {
        if (this.status == DepositStatus.PENDING_ADMIN_APPROVAL && isExpired()) {
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

    public void markAsFailed(String rejectionReason){
        if (this.status == DepositStatus.APPROVED){
            throw new IllegalStateException("완료된 요청은 실패 처리 될 수 없습니다.");
        }
        this.status = DepositStatus.REJECTED;
        this.rejectionReason = rejectionReason;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isPendingApproval() {
        return this.status == DepositStatus.PENDING_ADMIN_APPROVAL;
    }
}
