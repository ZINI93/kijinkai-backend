package com.kijinkai.domain.payment.application.dto.request;


import com.kijinkai.domain.exchange.doamin.Currency;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class WithdrawRequestDto {

    BigDecimal requestAmount;
    Currency targetCurrency;
    String bankName;
    String accountHolder;


}
