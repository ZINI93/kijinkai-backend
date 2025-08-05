package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import com.kijinkai.domain.payment.domain.repository.RefundRequestRepository;
import com.kijinkai.domain.payment.application.dto.command.CreateRefundCommand;
import com.kijinkai.domain.payment.domain.exception.RefundNotFoundException;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


public class RefundRequestService {


    private final PaymentFactory paymentFactory;
    private final UserValidator userValidator;
    private final PaymentValidator paymentValidator;

    public RefundRequestService(PaymentFactory paymentFactory, UserValidator userValidator, PaymentValidator paymentValidator) {
        this.paymentFactory = paymentFactory;
        this.userValidator = userValidator;
        this.paymentValidator = paymentValidator;
    }

    /**
     * 해당 상품에 대한 환불 생성 및 처리
     * @param customer
     * @param wallet
     * @param orderItem
     * @param refundAmount
     * @param adminUuid
     * @param refundReason
     * @param refundType
     * @return
     */
    public RefundRequest createRefundRequest(
            Customer customer, Wallet wallet, OrderItem orderItem
            , BigDecimal refundAmount, UUID adminUuid, String refundReason, RefundType refundType
    ){
        RefundRequest request = paymentFactory.createRefundPayment(
                customer, wallet, orderItem, refundAmount, adminUuid,refundReason,refundType);
        return request;
    }

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

    public RefundRequest getRefundInfoByAdmin(RefundRequest request, User admin){
        userValidator.requireAdminRole(admin);
        return request;
    }

    public RefundRequest getRefundInfo(RefundRequest request){
        return request;
    }


    // Helper method

    private User findUserByUserUuid(UUID adminUuid) {
        return getUserRepository().findByUserUuid(adminUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for admin uuid: %s", adminUuid)));
    }
}
