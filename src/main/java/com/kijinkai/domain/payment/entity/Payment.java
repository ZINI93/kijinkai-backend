package com.kijinkai.domain.payment.entity;

import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.entity.Wallet;
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
@Table(name = "payments")
@Entity
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false, unique = true)
    private Long paymentId;

    @Column(name = "payment_uuid", nullable = false, updatable = false, unique = true)
    private UUID paymentUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private BigDecimal amountOriginal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_currency_original", nullable = false)
    private Currency currencyOriginal;

    @Column(nullable = false)
    private BigDecimal amountConverter;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_currency_converter", nullable = false)
    private Currency currencyConverter;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    private String description;

    @Column(name = "external_transaction_id", updatable = false)
    private String externalTransactionId;

    @Column(name = "exchange_rate", updatable = false, nullable = false)
    private BigDecimal exchangeRate;

    // -- 입금관련
    @Column(name = "depositor")
    private String depositor;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // -- 출금관련
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "amount_number")
    private String amountNumber;

    @Column(name = "amount_holder")
    private String amountHolder;

    // -- 결제완료
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by_admin_uuid")
    private UUID completedByAdminUuid;

    // -- 캔슬

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by_admin_uuid")
    private UUID cancelledByAdminUuid;

    @Column(name = "cancel_reason")
    private String cancelReason;

    // -- 환불
    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "refund_adminUuid")
    private UUID refundAdminUuid;


    @Builder
    public Payment(UUID paymentUuid, Customer customer, Wallet wallet, Order order, PaymentStatus paymentStatus, PaymentMethod paymentMethod, BigDecimal amountOriginal, Currency currencyOriginal, BigDecimal amountConverter, Currency currencyConverter, PaymentType paymentType, String description, String externalTransactionId, BigDecimal exchangeRate, String bankName, String amountNumber, String amountHolder, LocalDateTime expiresAt, LocalDateTime completedAt, UUID completedByAdminUuid, LocalDateTime cancelledAt, UUID cancelledByAdminUuid, String cancelReason,
                   String refundReason, UUID refundAdminUuid) {
        this.paymentUuid = paymentUuid != null ? paymentUuid : UUID.randomUUID();
        this.customer = customer;
        this.wallet = wallet;
        this.order = order;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.paymentMethod = paymentMethod;
        this.amountOriginal = amountOriginal;
        this.currencyOriginal = currencyOriginal != null ? currencyOriginal : Currency.JPY;
        this.amountConverter = amountConverter;
        this.currencyConverter = currencyConverter;
        this.paymentType = paymentType;
        this.description = description;
        this.externalTransactionId = externalTransactionId;
        this.exchangeRate = exchangeRate;
        this.bankName = bankName;
        this.amountNumber = amountNumber;
        this.amountHolder = amountHolder;
        this.expiresAt = expiresAt;
        this.completedAt = completedAt;
        this.completedByAdminUuid = completedByAdminUuid;
        this.cancelledAt = cancelledAt;
        this.cancelledByAdminUuid = cancelledByAdminUuid;
        this.cancelReason = cancelReason;
        this.refundReason = refundReason;
        this.refundAdminUuid = refundAdminUuid;
    }

    public void completeByAdmin(UUID adminUUid) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.completedByAdminUuid = adminUUid;
    }

    public void cancelPaymentByAdmin(UUID adminUUid) {
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.completedByAdminUuid = adminUUid;
    }

    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.CANCELLED;
    }

}
