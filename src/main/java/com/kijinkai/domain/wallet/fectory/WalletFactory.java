package com.kijinkai.domain.wallet.fectory;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WalletFactory {

    public Wallet createWallet(Customer customer){

        return Wallet.builder()
                .walletUuid(UUID.randomUUID())
                .customer(customer)
                .balance(BigDecimal.ZERO)
                .currency(Currency.JPY)
                .walletStatus(WalletStatus.ACTIVE)
                .build();
    }

}
