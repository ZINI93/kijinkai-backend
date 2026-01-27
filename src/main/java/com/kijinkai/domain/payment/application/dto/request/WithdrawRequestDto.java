package com.kijinkai.domain.payment.application.dto.request;


import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.enums.BankType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class WithdrawRequestDto {

    BigDecimal requestAmount;
    Currency currency;
    BankType bankType;
    String accountNumber;
    String accountHolder;

    String memo;
}
