package com.kijinkai.domain.payment.infrastructure.config;

import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.service.DepositRequestService;
import com.kijinkai.domain.payment.domain.service.OrderPaymentService;
import com.kijinkai.domain.payment.domain.service.RefundRequestService;
import com.kijinkai.domain.payment.domain.service.WithdrawRequestService;
import com.kijinkai.domain.payment.domain.validator.PaymentValidator;
import com.kijinkai.domain.user.validator.UserValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public DepositRequestService depositRequestService(
            PriceCalculationService priceCalculationService,
            UserValidator userValidator,
            PaymentValidator paymentValidator,
            PaymentFactory paymentFactory
    ) {
        return new DepositRequestService(
                priceCalculationService,
                userValidator,
                paymentValidator,
                paymentFactory
        );
    }

    @Bean
    public WithdrawRequestService withdrawRequestService(
            PaymentValidator paymentValidator,
            UserValidator userValidator,
            PaymentFactory paymentFactory
            ) {
        return new WithdrawRequestService(
                paymentValidator,
                userValidator,
                paymentFactory
        );
    }

    @Bean
    public RefundRequestService refundRequestService(
            PaymentFactory paymentFactory,
            UserValidator userValidator
    ) {
        return new RefundRequestService(
                paymentFactory,
                userValidator
        );
    }

    @Bean
    public OrderPaymentService orderPaymentService(
            PaymentFactory paymentFactory,
            UserValidator userValidator
    ) {
        return new OrderPaymentService(
                paymentFactory,
                userValidator
        );
    }
}
