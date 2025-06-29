package com.kijinkai.domain.payment.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.exchange.doamin.ExchangeRate;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.dto.PaymentRequestDto;
import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.exception.PaymentNotFoundException;
import com.kijinkai.domain.payment.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.factory.PaymentFactory;
import com.kijinkai.domain.payment.mapper.PaymentMapper;
import com.kijinkai.domain.payment.repository.PaymentRepository;
import com.kijinkai.domain.payment.validate.PaymentValidator;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.exception.WalletUpdateFailedException;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    private final PaymentMapper paymentMapper;
    private final UserValidator userValidator;
    private final PaymentValidator paymentValidator;
    private final PaymentFactory paymentFactory;

    private final PriceCalculationService priceCalculationService;

    /**
     * 유저가 현지 화폐로 지불된 돈을 엔화로 환전 후 값이 필드에 들어가고 완료 전 보류 되는 프로세스 * 이 시점에 돈이 지갑으로 들어가지 않음, 충전 수수료 200엔
     * @param userUuid
     * @param requestDto
     * @return 생성된 지불 응답 DTO
     */
    @Override
    public PaymentResponseDto createPaymentWithValidate(String userUuid, PaymentRequestDto requestDto) {
        log.info("Creating payment for user uuid:{}", userUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Wallet wallet = findWalletByCustomerId(customer);

        paymentValidator.validateAmount(requestDto);

        BigDecimal amountOriginal = requestDto.getAmountOriginal();

        BigDecimal convertedAmount = priceCalculationService.convertAndCalculateTotalInJpy(amountOriginal, requestDto.getCurrencyConverter(), new BigDecimal(200.00));

        Payment payment = paymentFactory.createPayment(customer, wallet, amountOriginal, convertedAmount, requestDto);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created payment for payment uuid:{}", savedPayment.getPaymentUuid());
        return paymentMapper.toResponse(savedPayment);
    }


    /**
     *  위에서 유저가 보류한 결제를 완료하고 유저의 지갑을 환전된 엔화가 충전이 처리되는 프로세스
     * @param userUuid
     * @param paymentUuid
     * @return 완료된 지불 응답 DTO
     */
    @Override
    public PaymentResponseDto completePayment(String userUuid, String paymentUuid) {
        return executeWithOptimisticLockRetry(() ->
                processPaymentCompletion(userUuid,paymentUuid));
    }

    private PaymentResponseDto processPaymentCompletion(String userUuid, String paymentUuid) {
        log.info("Completed payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);
        paymentValidator.requiredPendingStatus(payment);

        int updatedRows = walletRepository.increaseBalanceAtomic(
                payment.getWallet().getWalletId(),
                payment.getAmountConverter()
        );

        if (updatedRows == 0) {
            throw new WalletUpdateFailedException("Failed to update wallet balance - wallet may not exist");
        }

        payment.updatePaymentStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        log.info("Confirmed payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }


    /**
     * 유저가 환불을 요청하면 전액 환불을 해준다. 현재 로직으로는 지갑에 돈은 감소 시키고, 수동으로 입금 받은 금액을 계좌로 돌려준다.
     * @param userUuid
     * @param paymentUuid
     * @return 환불 응답 DTO
     */
    @Override // 관리자기능 //일단 환불할떄 시스템으로 환불 처리만 해주고 직접 수동으로 입금
    public PaymentResponseDto refundPayment(String userUuid, String paymentUuid) {
        return executeWithOptimisticLockRetry(() ->
                processPaymentRefund(userUuid,paymentUuid));

    }
    private PaymentResponseDto processPaymentRefund(String userUuid, String paymentUuid){

        log.info("Refunding payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);
        paymentValidator.requiredCompletedStatus(payment);

        int updatedRows = walletRepository.decreaseBalanceAtomic(
                payment.getWallet().getWalletId(),
                payment.getAmountConverter()
        );

        if (updatedRows == 0){
            throw new WalletUpdateFailedException("Insufficient balance for refund");
        }

        payment.updatePaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        log.info("Refunded payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }

    /**
     * 거래가 진행되지 않고 결제전 보류 상태에서 유저가 수동으로 취소가 가능핟.
     * @param userUuid
     * @param paymentUuid
     * @return 취소 응답 DTo
     */
    @Override // 거래가 보류 상태일때 취소가능
    public PaymentResponseDto cancelPayment(String userUuid, String paymentUuid) {
        log.info("Cancel payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);
        paymentValidator.requiredPendingStatus(payment);
        payment.updatePaymentStatus(PaymentStatus.CANCEL);
        paymentRepository.save(payment);

        log.info("Cancel payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }

    /**
     * 결제정보 확인
     * @param userUuid
     * @param paymentUuid
     * @return 결제 정보 응답 DTO
     */
    @Override @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentInfo(String userUuid, String paymentUuid) {
        log.info("Searching payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);

        return paymentMapper.toResponse(payment);
    }


    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(UUID.fromString(userUuid))
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }

    private Payment findPaymentByCustomerUuidAndPaymentUuid(Customer customer,String paymentUuid) {
        return paymentRepository.findByCustomerCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), UUID.fromString(paymentUuid))
                .orElseThrow(() -> new PaymentNotFoundException("CustomerUuidAndPaymentUuid: Payment not found"));
    }

    private Wallet findWalletByCustomerId(Customer customer) {
        return walletRepository.findByCustomerCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer uuid: wallet not found"));
    }

    public PaymentResponseDto executeWithOptimisticLockRetry(Supplier<PaymentResponseDto> operation) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries){
            try {
                return operation.get();
            }catch (OptimisticLockingFailureException e){
                retryCount++;
                log.warn("Optimistic lock failure, retry{}/{}", retryCount, maxRetries);

                if (retryCount >= maxRetries ){
                    throw new PaymentProcessingException("Payment completion failed due to concurrent access");
                }

                try {
                    Thread.sleep(100 * retryCount);
                }catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                    throw new PaymentProcessingException("Payment processing interrupted");
                }
            }
        }
        throw new PaymentProcessingException("Unexpected error in payment completion");
    }
}