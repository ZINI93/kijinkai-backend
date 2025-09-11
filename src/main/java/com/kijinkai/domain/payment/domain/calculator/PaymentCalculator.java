package com.kijinkai.domain.payment.domain.calculator;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataJpaOrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentCalculator {

    private final ExchangeRateService exchangeRateService;
    private final SpringDataJpaOrderPaymentRepository springDataJpaOrderPaymentRepository;

    // wallet 입금 금액

    //
    public BigDecimal calculateDepositInJpy(Currency currency, BigDecimal depositAmount) {

        ExchangeRateResponseDto exchangeRate = exchangeRateService.getExchangeRateInfoByCurrency(currency);
        BigDecimal deposit = depositAmount.multiply(exchangeRate.getRate());

        return  deposit.multiply(BigDecimal.valueOf(0.01));
    }



    // wallet 출금 금액 원금 + 수수료 에 입금 해줘야하는 금액은 원금

    public BigDecimal calculateWithdrawInJyp(Currency currency, BigDecimal withdrawAmount){

        ExchangeRateResponseDto exchangeRate = exchangeRateService.getExchangeRateInfoByCurrency(currency);

        BigDecimal withdraw = withdrawAmount.multiply(exchangeRate.getRate());

        return withdraw;
    }




}
