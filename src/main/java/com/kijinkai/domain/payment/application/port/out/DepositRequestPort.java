package com.kijinkai.domain.payment.application.port.out;


import com.kijinkai.domain.payment.domain.entity.DepositRequest;

import java.util.UUID;

public interface DepositRequestPort {

    DepositRequest save(DepositRequest request);
    DepositRequest findDepositRequest(UUID requestUuid);
}
