package com.kijinkai.domain.payment.adapter.out.persistence.entity;


import com.kijinkai.domain.common.BaseEntity;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

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


    @Comment("식별자 UUID")
    @Column(name = "request_uuid", nullable = false, updatable = false, unique = true)
    private UUID requestUuid;

    @Comment("입금 코드")
    @Column(name = "deposit_code", nullable = false, updatable = false, unique = true)
    private String depositCode;


    @Comment("구매자 식별자 UUID")
    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;


    @Comment("지갑 식별자 UUID")
    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;


    @Comment("구매자가 가지고있는 통화")
    @Column(name = "amount_original", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountOriginal;


    @Comment("통화 종류")
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_original", nullable = false, length = 10)
    private Currency currencyOriginal;


//    @Comment("환전된 금액")
//    @Column(name = "amount_converted", nullable = false, precision = 19, scale = 4)
//    private BigDecimal amountConverted;

//    @Comment("환전 당시 환률 스냅샷")
//    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 8)
//    private BigDecimal exchangeRate;

    @Comment("입금자 이름")
    @Column(name = "depositor_name", nullable = false, length = 100)
    private String depositorName;

    // 상태 관리
    @Comment("입금 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DepositStatus status;

    @Comment("무통장 입금 기간")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Comment("처리를 완료한 관리자")
    @Column(name = "processed_by_admin_uuid")
    private UUID processedByAdminUuid;

    @Comment("처리 완료 날짜/시간")
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Comment("메모")
    @Column(name = "admin_memo", columnDefinition = "TEXt")
    private String adminMemo;

    @Comment("거절이유")
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Comment("은행")
    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type", nullable = false, length = 50)
    private BankType bankType;

    @Version
    @Column(name = "version")
    private Long version;

}
