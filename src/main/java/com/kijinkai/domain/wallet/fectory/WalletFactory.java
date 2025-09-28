package com.kijinkai.domain.wallet.fectory;

import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.entity.WalletStatus;
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
