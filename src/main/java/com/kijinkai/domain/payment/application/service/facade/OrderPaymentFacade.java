package com.kijinkai.domain.payment.application.service.facade;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.port.in.orderPayment.CreateOrderPaymentUseCase;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderPaymentFacade {


    private final CreateOrderPaymentUseCase createOrderPaymentUseCase;
    private final ExchangeRateService exchangeRateService;



    public OrderPayment processProductPayment(Customer customer, List<OrderItem> orderItems){

        // 결제요청 받은 가격의 합 계산
        BigDecimal totalPrice = calculateTotalPrice(orderItems);

        // 환룰조회
        ExchangeRateResponseDto exchangeRateByKor = exchangeRateService.getExchangeRateInfoByCurrency(Currency.KRW);

        // 환전
        BigDecimal exchangedAmount = totalPrice.multiply(exchangeRateByKor.getRate());


        return createOrderPaymentUseCase.saveOrderItem(customer, exchangedAmount);
    }


    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems){
        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::calculateFinalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderItemValidateException("결제 금액은 0원보다 커야 합니다.");
        }

        return totalPrice;

    }

}
