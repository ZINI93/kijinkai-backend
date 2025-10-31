package com.kijinkai.domain.orderitem.domain.model;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.domain.model.Delivery;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.order.domain.model.Order;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.dto.OrderItemUpdateDto;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class OrderItemTest {


    OrderItem orderItem;
    Customer customer;
    OrderPaymentJpaEntity orderPayment;
    Delivery delivery;
    Order order;

    @BeforeEach
    void setUp(){

        customer = Customer.builder().customerUuid(UUID.randomUUID()).build();
        orderPayment = OrderPaymentJpaEntity.builder().customerUuid(customer.getCustomerUuid()).build();
        delivery = Delivery.builder().deliveryUuid(UUID.randomUUID()).build();
        order = Order.builder().orderUuid(UUID.randomUUID()).build();

        orderItem = OrderItem.builder()
                .orderItemUuid(UUID.randomUUID())
                .order(order)
                .customerUuid(customer.getCustomerUuid())
                .productLink("www.aaaa.com")
                .quantity(2)
                .priceOriginal(new BigDecimal(1000.00))
                .currencyOriginal(Currency.JPY)
                .memo("양 많이")
                .build();

    }

    @Test
    @DisplayName("주문상품 업데이트")
    void orderItemUpdate(){

        //given
        OrderItemUpdateDto updateDto = OrderItemUpdateDto.builder()
                .productLink("www.bbb.com")
                .quantity(3)
                .memo("많이 많이")
                .priceOriginal(new BigDecimal(4000.00))
                .build();

        //when
        orderItem.updateOrderItem(updateDto);

        //then
        assertThat(orderItem.getProductLink()).isEqualTo(updateDto.getProductLink());
        assertThat(orderItem.getQuantity()).isEqualTo(updateDto.getQuantity());
        assertThat(orderItem.getPriceOriginal()).isEqualTo(updateDto.getPriceOriginal());

    }


    @Test
    @DisplayName("주문 아이디랑, 주문된 상품의 주문 아이디랑 같은 지 검증")
    void validateOrderAndOrderItem(){
        //given

        //when
        orderItem.validateOrderAndOrderItem(order);

        //then
        assertThat(orderItem.getOrder().getOrderUuid()).isEqualTo(order.getOrderUuid());
    }


    @Test
    @DisplayName("취소처리")
    void isCancel(){
        //given

        //when
        orderItem.isCancel();

        //then
        assertThat(orderItem.getOrderItemStatus()).isEqualTo(OrderItemStatus.CANCELLED);
    }

    @Test
    @DisplayName("첫번째 결제번호 번호를 업데이트 시키고 결제 완료 처리")
    void markAsPaymentCompleted(){

        //given

        //when
        orderItem.markAsPaymentCompleted(orderPayment.getPaymentUuid());


        //then
        assertThat(orderItem.getProductPaymentUuid()).isEqualTo(orderPayment.getPaymentUuid());
        assertThat(orderItem.getOrderItemStatus()).isEqualTo(OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
    }


    @Test
    @DisplayName("첫번쨰 결제 완료된 상품 배송 처리")
    void markAsDeliveryPaymentRequest(){

        //given

        //when
        orderItem.markAsDeliveryPaymentRequest(delivery.getDeliveryUuid());


        //then
        assertThat(orderItem.getDeliveryFeePaymentUuid()).isEqualTo(delivery.getDeliveryUuid());
        assertThat(orderItem.getOrderItemStatus()).isEqualTo(OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST);
    }




}