package com.kijinkai.domain.wallet.validator;


import com.kijinkai.domain.order.exception.OrderStatusException;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletValidator {

    public void requireActiveStatus(Wallet wallet){
        if (wallet.getWalletStatus() != WalletStatus.ACTIVE){
            throw new OrderStatusException("Wallet must be in active status to proceed.");
        }
    }


    public void requireSufficientBalance(Wallet wallet, BigDecimal totalAmount){
        if (wallet.getBalance().compareTo(totalAmount) < 0){
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }

    public void validateMinimumExchangeAmount(Wallet wallet){
        if (wallet.getBalance().compareTo(new BigDecimal(8000)) < 0) {
            throw new IllegalArgumentException("8,000엔 이상 환전이 가능합니다.");
        }
    }
}
