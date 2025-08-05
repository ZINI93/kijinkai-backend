package com.kijinkai.domain.payment.infrastructure.adapter.out.external;

import com.kijinkai.domain.payment.application.port.out.WalletPort;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class WalletAdapter implements WalletPort {

    private final WalletService walletService;

    @Override
    public Wallet findByCustomerUuid(UUID customerUuid) {
        return walletService.findByCustomerUuid(customerUuid);
    }

    @Override
    public WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount) {
        return walletService.deposit(customerUuid,walletUuid,amount);
    }

    @Override
    public WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount) {
        return walletService.withdrawal(customerUuid,walletUuid,amount);
    }
}
