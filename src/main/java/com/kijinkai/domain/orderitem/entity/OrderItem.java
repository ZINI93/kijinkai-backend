package com.kijinkai.domain.orderitem.entity;

import com.kijinkai.domain.BaseEntity;
import com.kijinkai.domain.TimeBaseEntity;
import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.dto.OrderItemUpdateDto;
import com.kijinkai.domain.orderitem.exception.OrderItemValidateException;
import com.kijinkai.domain.platform.entity.Platform;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.sql.Update;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Table(name = "order_items")
@Entity
public class OrderItem extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderItemId;

    @Column(name = "order_item_uuid", nullable = false, updatable = false, unique = true)
    private UUID orderItemUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", updatable = false, nullable = false, unique = true)
    private Order order;

    @Column(name = "product_link", nullable = false)
    private String productLink;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_original", nullable = false)
    private BigDecimal priceOriginal;

    @Column(name = "price_converted", nullable = false)
    private BigDecimal priceConverted;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_original", nullable = false)
    private Currency currencyOriginal; //JYP

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_converted", nullable = false)
    private Currency currencyConverted;

    @Column(name = "exchange_rate", nullable = false, updatable = false)
    private BigDecimal exchangeRate;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Builder
    public OrderItem(UUID orderItemUuid, Platform platform, Order order, String productLink, int quantity, BigDecimal priceOriginal, BigDecimal priceConverted, Currency currencyOriginal, Currency currencyConverted, BigDecimal exchangeRate, String memo) {
        this.orderItemUuid = orderItemUuid != null ? orderItemUuid : UUID.randomUUID();
        this.platform = platform;
        this.order = order;
        this.productLink = productLink;
        this.quantity = quantity;
        this.priceOriginal = priceOriginal;
        this.priceConverted = priceConverted;
        this.currencyOriginal = currencyOriginal != null ? currencyOriginal : Currency.JPY;
        this.currencyConverted = currencyConverted;
        this.exchangeRate = exchangeRate;
        this.memo = memo;
    }


    public void updateOrderItem(OrderItemUpdateDto updateDto, Platform platform) {
        this.platform = platform;
        this.productLink = updateDto.getProductLink();
        this.quantity = updateDto.getQuantity();
        this.memo = updateDto.getMemo();
        this.priceOriginal = updateDto.getPriceOriginal();
        this.currencyConverted = updateDto.getCurrencyConverted();
    }


    public void validateOrderAndOrderItem(Order order) {

        if (!Objects.equals(this.getOrder().getOrderId(), order.getOrderId())) {
            throw new IllegalArgumentException("OrderItem " + this.getOrderItemUuid() + " does not belong to Order " + this.order.getOrderUuid());
        }
    }

    public void updateEstimatedPrice() {
        if (this.priceOriginal.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderItemValidateException("Estimated price cannot be negative for order item: " + this.orderItemUuid);
        }
    }
}
