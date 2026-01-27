package com.kijinkai.domain.payment.application.dto.command;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.domain.enums.BankType;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
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
    BankType bankType;
    String accountHolder;


    public static CreateWithdrawCommand from(WalletJpaEntity wallet, CustomerJpaEntity customerJpaEntity, WithdrawRequestDto requestDto){

        return CreateWithdrawCommand.builder()
                .customerUuid(customerJpaEntity.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .requestAmount(requestDto.getRequestAmount())
                .targetCurrency(requestDto.getCurrency())
                .bankType(requestDto.getBankType())
                .accountHolder(requestDto.getAccountHolder())
                .build();

    }

}
