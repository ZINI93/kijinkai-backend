package com.kijinkai.domain.payment.domain.service;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.domain.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DepositRequestService {

    private final PriceCalculationService priceCalculationService;

    private final UserApplicationValidator userValidator;
    private final PaymentValidator paymentValidator;
    private final PaymentFactory paymentFactory;

    public DepositRequestService(PriceCalculationService priceCalculationService, UserApplicationValidator userValidator, PaymentValidator paymentValidator, PaymentFactory paymentFactory) {
        this.priceCalculationService = priceCalculationService;
        this.userValidator = userValidator;
        this.paymentValidator = paymentValidator;
        this.paymentFactory = paymentFactory;
    }



    /**
     * 입금 요청 생성
     * @param customerJpaEntity
     * @param wallet
     * @param originalAmount
     * @param originalCurrency
     * @param exchangeRate
     * @param depositorName
     * @param bankAccount
     * @return
     */
    public DepositRequest createDepositRequest(
            Customer customer, Wallet wallet, BigDecimal originalAmount, Currency originalCurrency, BigDecimal exchangeRate, String depositorName, BankType bankType,
            BigDecimal convertedAmount
    ) {
        paymentValidator.validateDepositEligibility(originalAmount, wallet);
        return paymentFactory.createDepositRequest(customer, wallet, originalAmount, originalCurrency, convertedAmount, exchangeRate, depositorName, bankType);
    }

    /**
     *  입금요청 승인
     * @param depositRequest
     * @param adminUuid
     * @param memo
     * @return
     */
    public DepositRequest approveDepositRequest(DepositRequest depositRequest, UUID adminUuid, String memo) {
        depositRequest.approve(adminUuid, memo);
        return depositRequest;
    }

    /**
     * 관리자의 입금정보 조회
     * @param depositRequest
     * @param user
     * @return
     */
    public DepositRequest getDepositInfoByAdmin(DepositRequest depositRequest, User user) {

        userValidator.requireAdminRole(user);

        return depositRequest;
    }

    public DepositRequest getDepositsByStatus(DepositRequest depositRequest, User user){
        userValidator.requireAdminRole(user);
        return depositRequest;
    }


    public DepositRequest getDepositInfo(DepositRequest depositRequest, Customer customer) {
        return depositRequest;
    }

    public void markAsFailed(DepositRequest depositRequest, String reason) {
        depositRequest.markAsFailed(reason);
    }

    /**
     * 만료된 요청 일괄 처리
     *
     * @return
     */
    public List<DepositRequest> expireOldRequests(List<DepositRequest> pendingRequests) {

        List<DepositRequest> expiredRequests = pendingRequests.stream()
                .filter(DepositRequest::isExpired)
                .peek(DepositRequest::expire)
                .collect(Collectors.toList());

        return expiredRequests;
    }
}
