package com.kijinkai.domain.payment.domain.factory;

import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DepositRequestFactory {

    public DepositRequest createDepositRequest(
            Customer customer, Wallet wallet, BigDecimal originalAmount, Currency originalCurrency,
            BigDecimal convertAmount, BigDecimal exchangeRate, String depositorName, BankType bankType
    ) {
        return DepositRequest.builder()
                .requestUuid(UUID.randomUUID())
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .amountOriginal(originalAmount)
                .currencyOriginal(originalCurrency)
                .amountConverted(convertAmount)
                .exchangeRate(exchangeRate)
                .depositorName(depositorName)
                .bankType(bankType)
                .status(DepositStatus.PENDING_ADMIN_APPROVAL)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }
}
