package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;


public class RefundRequestService {


    private final PaymentFactory paymentFactory;
    private final UserApplicationValidator userValidator;

    public RefundRequestService(PaymentFactory paymentFactory, UserApplicationValidator userValidator) {
        this.paymentFactory = paymentFactory;
        this.userValidator = userValidator;
    }

    /**
     * 해당 상품에 대한 환불 생성 및 처리
     * @param customerJpaEntity
     * @param wallet
     * @param orderItem
     * @param refundAmount
     * @param adminUuid
     * @param refundReason
     * @param refundType
     * @return
     */
//    public RefundRequest createRefundRequest(
//            Customer customer, Wallet wallet, OrderItem orderItem
//            , BigDecimal refundAmount, UUID adminUuid, String refundReason, RefundType refundType
//    ){
//        RefundRequest request = paymentFactory.createRefundPayment(
//                customer, wallet, orderItem, refundAmount, adminUuid,refundReason,refundType);
//        return request;
//    }

    /**
     * 해당 상품에 대한 환불 처리 완료
     * @param request
     * @param admin
     * @param memo
     * @return
     */
    public RefundRequest processRefundRequest(RefundRequest request, User admin, String memo){

        userValidator.requireAdminRole(admin);
        request.complete(memo);

        return request;
    }
//
//    public RefundRequest getRefundInfoByAdmin(RefundRequest request, User admin){
//        userValidator.requireAdminRole(admin);
//        return request;
//    }
//
//    public RefundRequest getRefundInfo(RefundRequest request){
//        return request;
//    }


}
