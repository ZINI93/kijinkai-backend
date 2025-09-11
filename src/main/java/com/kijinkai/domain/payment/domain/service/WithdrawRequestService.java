package com.kijinkai.domain.payment.domain.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;


public class WithdrawRequestService {

    private final PaymentValidator paymentValidator;
    private final UserValidator userValidator;
    private final PaymentFactory paymentFactory;

    public WithdrawRequestService(PaymentValidator paymentValidator, UserValidator userValidator, PaymentFactory paymentFactory) {
        this.paymentValidator = paymentValidator;
        this.userValidator = userValidator;
        this.paymentFactory = paymentFactory;
    }

    /**
     * 출금 요청 생성
     *
     * @param customer
     * @param wallet
     * @param requestAmount
     * @param tagetCurrency
     * @param bankName
     * @param accountHolder
     * @return
     */
    public WithdrawRequest createWithdrawRequest(
            Customer customer, Wallet wallet, BigDecimal requestAmount, Currency tagetCurrency
            , String bankName, String accountHolder, BigDecimal withdrawFee, BigDecimal convertedAmount,
            String accountNumber, BigDecimal exchangeRate
    ) {
        paymentValidator.validateWithdrawEligibility(requestAmount);
        return paymentFactory.createWithdrawRequest(
                customer, wallet, requestAmount, tagetCurrency, withdrawFee, bankName, accountHolder, convertedAmount
                ,accountNumber, exchangeRate
        );
    }

    /**
     * 출금 요청 승인
     *
     * @param request
     * @param adminUuid
     * @param memo
     * @param exchangeRate
     * @return
     */
    public WithdrawRequest approveWithdrawRequest(WithdrawRequest request, UUID adminUuid, String memo, BigDecimal exchangeRate) {
        request.approve(adminUuid, memo);
        return request;
    }

    /**
     * 관리자의 유저 출금 정보 조회
     *
     * @param withdrawRequest
     * @param user
     * @return
     */
    public WithdrawRequest getWithdrawInfoByAdmin(WithdrawRequest withdrawRequest, User user) {
        userValidator.requireAdminRole(user);
        return withdrawRequest;
    }

    /**
     * 유저의 출금 정보 조회
     *
     * @param withdrawRequest
     * @return
     */
    public WithdrawRequest getWithdrawInfo(WithdrawRequest withdrawRequest) {
        return withdrawRequest;
    }

    /**
     * 결제 실패
     *
     * @param withdrawRequest
     * @param reason
     */
    public void markAsFailed(WithdrawRequest withdrawRequest, String reason) {
        withdrawRequest.markAsFailed(reason);
    }

}
