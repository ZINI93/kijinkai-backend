package com.kijinkai.domain.wallet.validator;


import com.kijinkai.domain.order.exception.OrderStatusException;
import com.kijinkai.domain.payment.dto.PaymentRequestDto;
import com.kijinkai.domain.payment.exception.PaymentAmountException;
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

    public void requireSufficientBalance(Wallet wallet){
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) < 0){
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }
}
