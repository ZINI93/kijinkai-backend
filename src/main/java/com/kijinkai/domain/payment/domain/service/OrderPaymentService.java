package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;

import java.math.BigDecimal;

public class OrderPaymentService {

    private final PaymentFactory paymentFactory;
    private final UserValidator userValidator;
    private final PaymentValidator paymentValidator;

    public OrderPaymentService(PaymentFactory paymentFactory, UserValidator userValidator, PaymentValidator paymentValidator) {
        this.paymentFactory = paymentFactory;
        this.userValidator = userValidator;
        this.paymentValidator = paymentValidator;
    }


    // 관리자가 결제 생성
    public OrderPayment crateOrderPayment(
            Customer customer, Wallet wallet,
            Order order, BigDecimal paymentAmount, User admin
    ) {
        userValidator.requireAdminRole(admin);
        OrderPayment orderPayment = paymentFactory.createOrderFirstPayment(
                customer, wallet, order, paymentAmount, admin.getUserUuid()
        );

        return orderPayment;
    }

    /**
     * 1차 상품에 대한 상품대금 결제
     *
     * @param orderPayment
     * @return
     */
    public OrderPayment completePayment(OrderPayment orderPayment) {
        orderPayment.complete();
        return orderPayment;
    }

    // 배송비 지불 결제 생성

    public OrderPayment createSecondOrderPayment(Customer customer, Wallet wallet,
                                           Order order, BigDecimal paymentAmount, User admin) {

        userValidator.requireAdminRole(admin);
        OrderPayment orderPayment = paymentFactory.createOrderSecondPayment(customer, wallet, order, paymentAmount, admin.getUserUuid());

        return orderPayment;
    }

    // 유저가 결제

    public OrderPayment completeSecondOrderPayment(OrderPayment orderPayment) {
        orderPayment.complete();;
        return orderPayment;
    }


    /**
     * 관리자의 결제정보 확인
     * @param admin
     * @param orderPayment
     * @return
     */
    public OrderPayment getOrderPaymentInfoByAdmin(User admin, OrderPayment orderPayment){
        userValidator.requireAdminRole(admin);
        return orderPayment;
    }


    // 관리자의 결제정보 확인
    public OrderPayment getOrderPaymentInfo(OrderPayment orderPayment) {
        return orderPayment;
    }



    public OrderPayment markAsFailed(OrderPayment orderPayment, String reason) {
        orderPayment.markAsFailed(reason);
        return orderPayment;
    }
}
