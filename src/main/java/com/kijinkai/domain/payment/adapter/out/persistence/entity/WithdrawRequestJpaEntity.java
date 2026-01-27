package com.kijinkai.domain.payment.adapter.out.persistence.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
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
@Table(name = "withdraw_requests")
@Entity
public class WithdrawRequestJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_request_id", nullable = false, updatable = false)
    private Long withdrawRequestId;

    @Column(name = "request_uuid", nullable = false, updatable = false, unique = true)
    private UUID requestUuid;

    @Comment("출금코드")
    @Column(name = "withdraw_code", nullable = false, unique = true, updatable = false)
    private String withdrawCode;

    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "wallet_uuid", nullable = false, updatable = false)
    private UUID walletUuid;

    @Comment("출금요청 금액")
    @Column(name = "request_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal requestAmount;

    @Comment("수수료")
    @Column(name = "withdraw_fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal withdrawFee;

    @Comment("수수료 포함 총 금액")
    @Column(name = "total_deduct_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDeductAmount;

    @Comment("통화")
    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false, length = 10)
    private Currency targetCurrency;


    @Comment("은행")
    @Column(name = "bank_type", nullable = false, length = 100)
    private BankType bankType;

    @Comment("계좌 번호")
    @Column(name = "account_number", nullable = false, length = 100)
    private String accountNumber;


    @Comment("예금주")
    @Column(name = "account_holder", nullable = false, length = 100)
    private String accountHolder;

    @Comment("상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WithdrawStatus status;


    @Comment("출금 승인 관리자")
    @Column(name = "processed_by_admin_uuid")
    private UUID processedByAdminUuid;

    @Comment("승인 처리 일시")
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Comment("승인 처리중 메모")
    @Column(name = "admin_memo", columnDefinition = "TEXT")
    private String adminMemo;

    @Comment("거절")
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;


}
