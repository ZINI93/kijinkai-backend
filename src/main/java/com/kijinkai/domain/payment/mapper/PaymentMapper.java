package com.kijinkai.domain.payment.mapper;

import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PaymentMapper {
    public PaymentResponseDto toResponse(Payment payment, WalletResponseDto walletResponseDto){

        return PaymentResponseDto.builder()
                .balance(walletResponseDto.getBalance())
                .paymentUuid(payment.getPaymentUuid())
                .build();
    }

    public PaymentResponseDto toResponse(Payment payment){

        return PaymentResponseDto.builder()
                .paymentUuid(payment.getPaymentUuid())
                .build();
    }
}
