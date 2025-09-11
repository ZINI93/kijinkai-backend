package com.kijinkai.domain.payment.application.dto.command;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class CreateWithdrawCommand {

    UUID customerUuid;
    UUID walletUuid;
    BigDecimal requestAmount;
    Currency targetCurrency;
    String bankName;
    String accountHolder;


    public static CreateWithdrawCommand from(Wallet wallet, Customer customer, WithdrawRequestDto requestDto){

        return CreateWithdrawCommand.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .requestAmount(requestDto.getRequestAmount())
                .targetCurrency(requestDto.getCurrency())
                .bankName(requestDto.getBankName())
                .accountHolder(requestDto.getAccountHolder())
                .build();

    }

}
