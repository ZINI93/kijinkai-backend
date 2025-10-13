package com.kijinkai.domain.wallet.domain.fectory;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletStatus;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WalletFactory {

    public Wallet createWallet(UUID customerUuid){

        return Wallet.builder()
                .walletUuid(UUID.randomUUID())
                .customerUuid(customerUuid)
                .balance(BigDecimal.ZERO)
                .currency(Currency.JPY)
                .walletStatus(WalletStatus.ACTIVE)
                .build();
    }

}
