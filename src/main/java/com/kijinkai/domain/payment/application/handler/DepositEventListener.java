package com.kijinkai.domain.payment.application.handler;

import com.kijinkai.domain.payment.application.port.out.DepositRequestPersistencePort;
import com.kijinkai.domain.payment.domain.exception.DepositNotFoundException;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import com.kijinkai.domain.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class DepositEventListener {


    private final DepositRequestPersistencePort depositRequestPersistencePort;
    private final TransactionService transactionService;


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handlerDepositFailure(DepositFailedEvent event){
        log.info("Handling deposit failure event for: {}", event.getDepositRequestUuid());

        // 1. 이미 롤백되었으므로 DB에서 다시 깔끔한 상태의 엔티티를 조회해야 함
        DepositRequest request = depositRequestPersistencePort.findByRequestUuid(event.getDepositRequestUuid())
                .orElseThrow(() -> new DepositNotFoundException("Not found Deposit"));


        // 2. 실패 상태로 업데이트
        request.markAsFailed(event.getReason());


        //거래내역 실패 처리
        transactionService.failedPayment(request.getCustomerUuid(), request.getDepositCode());


        depositRequestPersistencePort.saveDepositRequest(request);

        log.info("Successfully recorded failure status.");

    }
}
