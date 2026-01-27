package com.kijinkai.domain.payment.application.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class WithdrawFailedEvent {

    private final UUID withdrawRequestUuid;
    private final String reason;

}
