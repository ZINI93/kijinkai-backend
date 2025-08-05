package com.kijinkai.domain.payment.application.dto.command;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class CreateDepositCommand {
    UUID customerUuid;
    UUID wlletUuid;
    BigDecimal amountOriginal;
    Currency originalCurrency;
    String depositorName;
    String bankAccount;
    String memo;

    public static CreateDepositCommand from(Wallet wallet, Customer customer, DepositRequestDto requestDto){

        return CreateDepositCommand.builder()
                .customerUuid(customer.getCustomerUuid())
                .wlletUuid(wallet.getWalletUuid())
                .amountOriginal(requestDto.getAmountOriginal())
                .originalCurrency(requestDto.getOriginalCurrency())
                .depositorName(requestDto.getDepositorName())
                .bankAccount(requestDto.getBankAccount())
                .memo(requestDto.getMemo())
                .build();
    }
}
