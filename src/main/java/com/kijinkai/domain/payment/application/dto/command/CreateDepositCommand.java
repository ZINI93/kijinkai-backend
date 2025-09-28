package com.kijinkai.domain.payment.application.dto.command;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.domain.enums.BankType;
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
    BankType bankType;
    String memo;

    public static CreateDepositCommand from(Wallet wallet, CustomerJpaEntity customerJpaEntity, DepositRequestDto requestDto){

        return CreateDepositCommand.builder()
                .customerUuid(customerJpaEntity.getCustomerUuid())
                .wlletUuid(wallet.getWalletUuid())
                .amountOriginal(requestDto.getAmountOriginal())
                .originalCurrency(requestDto.getOriginalCurrency())
                .depositorName(requestDto.getDepositorName())
                .bankType(requestDto.getBankType())
                .build();
    }
}
