package com.kijinkai.domain.orderitem.entity;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.platform.entity.Platform;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Table(name = "order_items")
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long orderItemId;

    @Column(name = "order_item_uuid", nullable = false, updatable = false, unique = true)
    private String orderItemUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

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

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "price_original", nullable = false)
    private BigDecimal priceOriginal;

    @Column(name = "price_converted", nullable = false)
    private BigDecimal priceConverted;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_original", nullable = false)
    private Currency currencyOriginal;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_converted", nullable = false)
    private Currency currencyConverted;

    @Column(name = "exchange_rate", nullable = false, updatable = false)
    private BigDecimal exchangeRate;


    @Builder
    public OrderItem(String orderItemUuid, Customer customer, Platform platform, Order order, String productLink, int quantity, BigDecimal priceOriginal, BigDecimal priceConverted, Currency currencyOriginal, Currency currencyConverted, BigDecimal exchangeRate, String memo) {
        this.orderItemUuid = orderItemUuid != null ? orderItemUuid : UUID.randomUUID().toString();
        this.customer = customer;
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


    public void updateOrderItem(Platform platform, String productLink, int quantity, String memo, BigDecimal priceOriginal, Currency currencyConverted) {
        this.platform = platform;
        this.productLink = productLink;
        this.quantity = quantity;
        this.memo = memo;
        this.priceOriginal = priceOriginal;
        this.currencyConverted = currencyConverted;
    }
}
