package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public class OrderPaymentService {

    private final PaymentFactory paymentFactory;
    private final UserApplicationValidator userValidator;

    public OrderPaymentService(PaymentFactory paymentFactory, UserApplicationValidator userValidator) {
        this.paymentFactory = paymentFactory;
        this.userValidator = userValidator;
    }


    // 1차 결제 생성
    public OrderPayment crateOrderPayment(
            Customer customer, Wallet wallet
    ) {
        OrderPayment orderPayment = paymentFactory.createOrderFirstPayment(
                customer, wallet
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



//    // 유저가 결제 2차 결제 생성
//    public OrderPayment crateSecuondOrderPayment(
//            Customer customer, Wallet wallet
//    ) {
//        OrderPayment orderPayment = paymentFactory.createOrderSecondPayment(
//                customer, wallet
//        );
//
//        return orderPayment;
//    }

    // 배송비 지불 결제 생성

    public OrderPayment createSecondOrderPayment(Customer customer, BigDecimal paymentAmount, User admin, Wallet wallet) {
        userValidator.requireAdminRole(admin);
        OrderPayment orderPayment = paymentFactory.createOrderSecondPayment(customer, paymentAmount, wallet, admin.getUserUuid());

        return orderPayment;
    }

    // 유저가 결제

    public OrderPayment completeSecondOrderPayment(OrderPayment orderPayment) {
        orderPayment.complete();;
        return orderPayment;
    }


    /**
     * 관리자의 결제정보 확인
     *
     * @param admin
     * @param orderPayment
     * @return
     */
    public OrderPayment getOrderPaymentInfoByAdmin(User admin, OrderPayment orderPayment) {
        userValidator.requireAdminRole(admin);
        return orderPayment;
    }


    // 관리자의 결제정보 확인
    public OrderPayment getOrderPaymentInfo(OrderPayment orderPayment) {
        return orderPayment;
    }


    public void markAsFailed(List<OrderPayment> orderPayments, String reason) {
        orderPayments.forEach(orderPayment ->
                orderPayment.markAsFailed(reason));
    };
}
