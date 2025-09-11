package com.kijinkai.domain.orderitem.entity;

import com.kijinkai.domain.common.TimeBaseEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.exception.OrderItemValidateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderItemId;

    @Column(name = "order_item_uuid", nullable = false, updatable = false, unique = true)
    private UUID orderItemUuid;

    @Column(name = "customer_uuid" , nullable = false , updatable = false)
    private UUID customerUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, nullable = false)
    private Order order;

    @Column(name = "product_payment_uuid")
    private UUID productPaymentUuid;

    @Column(name = "delivery_fee_payment_uuid")
    private UUID deliveryFeePaymentUuid;

    @Column(name = "product_link", nullable = false)
    private String productLink;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_original", nullable = false)
    private BigDecimal priceOriginal;

    @Column(name = "price_converted", nullable = false)
    private BigDecimal priceConverted;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_currency_original", nullable = false)
    private Currency currencyOriginal; //JYP

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_currency_converted", nullable = false)
    private Currency currencyConverted;

    @Column(name = "exchange_rate", nullable = false, updatable = false)
    private BigDecimal exchangeRate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_item_status", nullable = false)
    private OrderItemStatus orderItemStatus;

    @Builder
    public OrderItem(UUID orderItemUuid, UUID customerUuid, Order order, UUID  productPaymentUuid, String productLink, int quantity, BigDecimal priceOriginal, BigDecimal priceConverted, Currency currencyOriginal, Currency currencyConverted, BigDecimal exchangeRate, String memo, OrderItemStatus orderItemStatus) {
        this.orderItemUuid = orderItemUuid != null ? orderItemUuid : UUID.randomUUID();
        this.customerUuid = customerUuid != null ? customerUuid : UUID.randomUUID();
        this.order = order;
        this.productPaymentUuid = productPaymentUuid;
        this.productLink = productLink;
        this.quantity = quantity;
        this.priceOriginal = priceOriginal;
        this.priceConverted = priceConverted;
        this.currencyOriginal = currencyOriginal != null ? currencyOriginal : Currency.JPY;
        this.currencyConverted = currencyConverted;
        this.exchangeRate = exchangeRate;
        this.memo = memo;
        this.orderItemStatus = orderItemStatus != null ? orderItemStatus : OrderItemStatus.PENDING;
    }

    public void updateOrderItem(OrderItemUpdateDto updateDto) {
        this.productLink = updateDto.getProductLink();
        this.quantity = updateDto.getQuantity();
        this.memo = updateDto.getMemo();
        this.priceOriginal = updateDto.getPriceOriginal();
        this.currencyConverted = updateDto.getCurrencyConverted();
    }


    public void validateOrderAndOrderItem(Order order) {

        if (!Objects.equals(this.getOrder().getOrderUuid(), order.getOrderUuid())) {
            throw new OrderItemValidateException("OrderItem " + this.getOrderItemUuid() + " does not belong to Order " + this.order.getOrderUuid());
        }
    }

    public void updateEstimatedPrice() {
        if (this.priceOriginal.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderItemValidateException("Estimated price cannot be negative for order item: " + this.orderItemUuid);
        }
    }

    public void isCancel(){
        orderItemStatus = OrderItemStatus.CANCELLED;
    }

    public void markAsPaymentCompleted(UUID fristproductPaymentUuid){
        this.productPaymentUuid = fristproductPaymentUuid;
        orderItemStatus = OrderItemStatus.PRODUCT_PAYMENT_COMPLETED;
    }

    public void markAsDeliveryPaymentRequest(UUID deliveryFeePaymentUuid){
        this.deliveryFeePaymentUuid = deliveryFeePaymentUuid;
        this.orderItemStatus = OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST;
    }
}
