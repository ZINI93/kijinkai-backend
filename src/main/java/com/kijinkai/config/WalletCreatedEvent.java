package com.kijinkai.config;

import com.kijinkai.domain.customer.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WalletCreatedEvent {

    private final Customer customer;
//    private final WalletCreateResult walletCreateResult;
    private final LocalDateTime localDateTime;
    private  final String correlationId;
}
