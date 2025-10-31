package com.kijinkai.domain.payment.application.port.out;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface PaymentPersistencePort {


    DepositRequestJpaEntity saveDepositRequest(DepositRequestJpaEntity request);
    DepositRequestJpaEntity findDepositRequest(UUID requestUuid);
    WithdrawRequestJpaEntity saveWithdrawRequest(WithdrawRequestJpaEntity request);
}
