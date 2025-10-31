package com.kijinkai.domain.payment.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "withdraw_requests")
@Entity
public class WithdrawRequestJpaEntity extends BaseEntity {

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
    @Column(name = "request_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal requestAmount;

    // 수수료
    @Column(name = "withdraw_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal withdrawFee;

    // 총 차감 금액 (요청금액 + 수수료)
    @Column(name = "total_deduct_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDeductAmount;

    // 환전 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false, length = 10)
    private Currency targetCurrency;

    @Column(name = "converted_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal convertedAmount;

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 8)
    private BigDecimal exchangeRate;

    // 출금 계좌 정보
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 100)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false, length = 100)
    private String accountHolder;

    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WithdrawStatus status;

    // 관리자 처리 정보
    @Column(name = "processed_by_admin_uuid")
    private UUID processedByAdminUuid;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "admin_memo", columnDefinition = "TEXT")
    private String adminMemo;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    // 출금 요청은 만료기간이 필요 없을것 같음 유저가 요청하면 해줘야 함

    @Version
    @Column(name = "version", nullable = false)
    private Long version;


}
