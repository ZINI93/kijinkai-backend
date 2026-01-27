package com.kijinkai.domain.payment.application.handler;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DepositFailedEvent {

    private final UUID depositRequestUuid;
    private final String reason;

}
