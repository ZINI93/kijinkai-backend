package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface PaymentPersistencePort {


    DepositRequest saveDepositRequest(DepositRequest request);
    DepositRequest findDepositRequest(UUID requestUuid);
    WithdrawRequest saveWithdrawRequest(WithdrawRequest request);
}
