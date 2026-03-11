package com.kijinkai.domain.payment.application.service.facade;

import com.kijinkai.domain.coupon.application.port.in.usercoupon.GetUserCouponUseCase;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemValidateException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.port.in.orderPayment.CreateOrderPaymentUseCase;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderPaymentFacade {


    private final CreateOrderPaymentUseCase createOrderPaymentUseCase;
    private final GetUserCouponUseCase getUserCouponUseCase;
    private final ExchangeRateService exchangeRateService;


    /*
    1차 상품에 대한 결제 (수량 * , 검수비는 orderItemUuid당 한건
     */
    public OrderPayment processProductPayment(Customer customer, List<OrderItem> orderItems, UUID userCouponUuid, BigDecimal totalPhotoRequestFee, BigDecimal exchangeRate) {

        // 결제 요청 받은 가격 + 검수비 계산
        BigDecimal totalPrice = calculateTotalPrice(orderItems).add(totalPhotoRequestFee);
        // 환전
        BigDecimal exchangedAmount = totalPrice.multiply(exchangeRate);

        // 쿠폰 할인 계산
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (userCouponUuid != null){
            discountAmount = getUserCouponUseCase.discountValue(customer.getUserUuid(), userCouponUuid, exchangedAmount);
        }

        BigDecimal finalPaymentAmount = exchangedAmount.subtract(discountAmount).max(BigDecimal.ZERO);

        return createOrderPaymentUseCase.saveOrderItem(
                customer,
                exchangedAmount,
                discountAmount,
                finalPaymentAmount,
                userCouponUuid);
    }


    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {

        // order item 가격 * 수량

        BigDecimal totalPrice = orderItems.stream()
                .map(orderItem -> {
                    // 가격 추출
                    BigDecimal priceOriginal = orderItem.getPriceOriginal();
                    // 방어로직
                    BigDecimal unitPrice = (priceOriginal != null) ? priceOriginal : BigDecimal.ZERO;
                    // 가격 * 수량
                    return unitPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("totalPrice = " + totalPrice);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderItemValidateException("결제 금액은 0원보다 커야 합니다.");
        }

        return totalPrice;
    }

}
