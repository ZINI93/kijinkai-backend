package com.kijinkai.domain.payment.application.handler;

import com.kijinkai.domain.payment.application.port.out.WithdrawPersistenceRequestPort;
import com.kijinkai.domain.payment.domain.exception.WithdrawRequestNotFoundException;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import com.kijinkai.domain.transaction.service.TransactionService;
import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class WithdrawEventListener {

    private final TransactionService transactionService;
    private final WithdrawPersistenceRequestPort withdrawPersistenceRequestPort;
    private final UpdateWalletUseCase updateWalletUseCase;


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handlerWithdrawFailure(WithdrawFailedEvent event) {
        log.info("Handling deposit failure event for : {}", event.getWithdrawRequestUuid());

        // 다시조회
        WithdrawRequest withdrawRequest = withdrawPersistenceRequestPort.findByRequestUuid(event.getWithdrawRequestUuid())
                .orElseThrow(() -> new WithdrawRequestNotFoundException("Not found withdraw"));

        //실패처리
        withdrawRequest.markAsFailed(event.getReason());

        // 환불 금액복구
        updateWalletUseCase.refund(withdrawRequest.getCustomerUuid(), withdrawRequest.getWalletUuid(), withdrawRequest.getRequestAmount());

        // 거리내역 실패처리
        transactionService.failedPayment(withdrawRequest.getCustomerUuid(), withdrawRequest.getWithdrawCode());

        // 저장
        withdrawPersistenceRequestPort.saveWithdrawRequest(withdrawRequest);

        log.info("Successfully recorded failure status");
    }
}
