package com.kijinkai.domain.payment.adapter.out.persistence.entity;


import com.kijinkai.domain.common.BaseEntity;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;




@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "deposit_requests")
public class DepositRequestJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_request_id", nullable = false, updatable = false)
    private Long depositRequestId;

    @Column(name = "request_uuid", nullable = false, updatable = false, unique = true)
    private UUID requestUuid;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    // 원본 금액 정보
    @Column(name = "amount_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountOriginal;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_original", nullable = false, length = 10)
    private Currency currencyOriginal;

    // 변환된 금액 정보
    @Column(name = "amount_converted", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountConverted;

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 8)
    private BigDecimal exchangeRate;

    // 입금자 정보
    @Column(name = "depositor_name", nullable = false, length = 100)
    private String depositorName;

    @Column(name = "bank_account", length = 50, nullable = false)
    private String bankAccount;

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DepositStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 관리자 처리 정보
    @Column(name = "processed_by_admin_uuid")
    private UUID processedByAdminUuid;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_memo", columnDefinition = "TEXt")
    private String adminMemo;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type", nullable = false, length = 50)
    private BankType bankType;

    @Version
    @Column(name = "version")
    private Long version;

}
