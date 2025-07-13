package com.kijinkai.domain.payment.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.payment.dto.PaymentDepositRequestDto;
import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.dto.WithdrawalRequestDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.exception.PaymentNotFoundException;
import com.kijinkai.domain.payment.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.factory.PaymentFactory;
import com.kijinkai.domain.payment.mapper.PaymentMapper;
import com.kijinkai.domain.payment.repository.PaymentRepository;
import com.kijinkai.domain.payment.util.PaymentContents;
import com.kijinkai.domain.payment.validate.PaymentValidator;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import com.kijinkai.domain.wallet.exception.InactiveWalletException;
import com.kijinkai.domain.wallet.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.exception.WalletUpdateFailedException;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import com.kijinkai.domain.wallet.service.WalletService;
import com.kijinkai.domain.wallet.validator.WalletValidator;
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
@Transactional(readOnly = true)
@Service
public class PaymentServiceImpl implements PaymentService {

    private final WalletService walletService;

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    private final PaymentMapper paymentMapper;

    private final UserValidator userValidator;
    private final PaymentValidator paymentValidator;
    private final WalletValidator walletValidator;

    private final PaymentFactory paymentFactory;

    private final PriceCalculationService priceCalculationService;

    /**
     * 유저가 현지화폐의 돈을 엔화로 환전 후에 그 금액이 지갑으로 들어가는 충전 프로세스
     * 이 시점에 돈이 지갑으로 들어가지 않음, 충전 수수료 200엔
     *
     * @param userUuid
     * @param requestDto
     * @return 생성된 지불 응답 DTO
     */
    @Override
    public PaymentResponseDto createDepositPayment(UUID userUuid, PaymentDepositRequestDto requestDto) {
        log.info("Creating payment for user uuid:{}", userUuid);

        Wallet wallet = findWalletByCustomerUuid(userUuid);
        Customer customer = wallet.getCustomer();

        walletValidator.requireActiveStatus(wallet);

        paymentValidator.validateAmount(requestDto);

        BigDecimal amountOriginal = requestDto.getAmountOriginal();
        BigDecimal depositFee = PaymentContents.DEPOSIT_FEE;
        BigDecimal convertedAmount = priceCalculationService.convertAndCalculateTotalInJpy(amountOriginal, Currency.JPY, depositFee);

        Payment payment = paymentFactory.createWithDepositPayment(customer, wallet, amountOriginal, convertedAmount, requestDto);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created payment for payment uuid:{}", savedPayment.getPaymentUuid());
        return paymentMapper.toResponse(savedPayment);
    }


    /**
     * 위에서 유저가 보류한 결제를 완료하고 유저의 지갑을 환전된 엔화가 충전이 처리되는 프로세스
     *
     * @param adminUuid
     * @param paymentUuid
     * @return 완료된 지불 응답 DTO
     */
    @Override
    @Transactional
    public PaymentResponseDto completeDepositPayment(UUID adminUuid, String paymentUuid) {

        log.info("Completing payment by admin: {} for payment: {}", adminUuid, paymentUuid);

        findByAdminWithAdminValidate(adminUuid);

        return executeWithOptimisticLockRetry(() -> processDepositPaymentCompletion(paymentUuid, adminUuid));
    }

    /**
     * 입금 프로세스
     *
     * @param paymentUuid
     * @param adminUuid
     * @return
     */
    private PaymentResponseDto processDepositPaymentCompletion(String paymentUuid, UUID adminUuid) {

        log.info("Completed payment for payment uuid:{}", paymentUuid);

        Payment payment = findPaymentByPaymentUuid(paymentUuid);

        WalletResponseDto depositResult = null;

        try {
            depositResult = walletService.deposit(payment.getCustomer().getCustomerUuid(),
                    payment.getWallet().getWalletUuid(), payment.getAmountConverter());

            log.info("WalletService deposit successful. Wallet UUID: {}, New Balance: {}",
                    depositResult.getWalletUuid(), depositResult.getBalance());
        } catch (WalletUpdateFailedException e) {
            throw new PaymentProcessingException("Charging failed");
        }

        if (payment.getPaymentStatus().canTransitionTo(PaymentStatus.PENDING)) {
            payment.completeByAdmin(adminUuid);
        }
        paymentRepository.save(payment);

        log.info("Confirmed payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment, depositResult);
    }


    /**
     * 유저가 본인의 월렛에 있는 잔액을 출금 신청하는 프로세스
     * 2만엔 이상 출금 가능
     *
     * @param userUuid
     * @param requestDto
     * @return
     */

    @Override
    @Transactional
    public PaymentResponseDto createWithdrawalPayment(UUID userUuid, WithdrawalRequestDto requestDto) {
        log.info("Creating withdrawal payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Wallet wallet = findWalletByCustomerId(customer);

        walletValidator.requireActiveStatus(wallet);
        walletValidator.validateMinimumExchangeAmount(wallet);  // 환전 최저 금액 20,000엔 이상

        paymentValidator.validateAmountByWithdrawal(requestDto);

        BigDecimal amountOriginal = requestDto.getAmountOriginal();
        BigDecimal withdrawalFee = PaymentContents.WITHDRAWAL_FEE;

        BigDecimal totalDeductionAmount = amountOriginal.subtract(withdrawalFee);
        BigDecimal balanceAfterWithdrawal = wallet.getBalance().subtract(totalDeductionAmount);
        if (balanceAfterWithdrawal.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance including fees");
        }

        BigDecimal convertedAmount = priceCalculationService.convertAndCalculateTotalInLocalCurrency(amountOriginal, Currency.KRW, withdrawalFee);

        Payment payment = paymentFactory.createWithWithdrawalPayment(customer, wallet, amountOriginal, convertedAmount, requestDto);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created  credit payment for payment uuid:{}", savedPayment.getPaymentUuid());
        return paymentMapper.toResponse(savedPayment);
    }

    /**
     * 위에서 유저가 출금신청을 완료하고 유저 월렛에서 돈이 감소되는 프로세스
     *
     * @param adminUuid
     * @param paymentUuid
     * @return 완료된 지불 응답 DTO
     */
    @Override
    @Transactional
    public PaymentResponseDto completePaymentByWithdrawal(UUID adminUuid, String paymentUuid) {

        log.info("Completing withdrawal payment by admin: {} for payment: {}", adminUuid, paymentUuid);
        findByAdminWithAdminValidate(adminUuid);

        return executeWithOptimisticLockRetry(() ->
                processPaymentCompletionByWithdrawal(paymentUuid, adminUuid));
    }

    /**
     * 출금 프로세스
     *
     * @param paymentUuid
     * @param adminUuid
     * @return
     */
    @Transactional
    private PaymentResponseDto processPaymentCompletionByWithdrawal(String paymentUuid, UUID adminUuid) {

        log.info("Completed withdrawal payment for payment uuid:{}", paymentUuid);
        Payment payment = findPaymentByPaymentUuid(paymentUuid);

        paymentValidator.requiredPendingStatus(payment);
        paymentValidator.validateWithdrawalType(payment);

        if (!payment.getWallet().getWalletStatus().equals(WalletStatus.ACTIVE)) {
            throw new InactiveWalletException("Wallet is inactive");
        }

        BigDecimal feeAmount = new BigDecimal("300.00");
        BigDecimal totalDeductionAmount = payment.getAmountOriginal().add(feeAmount);
        if (payment.getWallet().getBalance().compareTo(totalDeductionAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient current balance for withdrawal");
        }

        WalletResponseDto withdrawalResult = null;

        try {
            withdrawalResult = walletService.withdrawal(payment.getCustomer().getCustomerUuid(), payment.getWallet().getWalletUuid(), totalDeductionAmount);
            log.info("WalletService withdrawal successful. Wallet UUID: {}, New Balance: {}",
                    withdrawalResult.getWalletUuid(), withdrawalResult.getBalance());
        } catch (WalletUpdateFailedException e) {
            throw new PaymentProcessingException("Withdrawal Failed");
        }

        if (payment.getPaymentStatus().canTransitionTo(PaymentStatus.PENDING)) {
            payment.completeByAdmin(adminUuid);
        }
        paymentRepository.save(payment);

        log.info("Confirmed withdrawal payment for payment uuid:{}", payment.getPaymentUuid());

        return paymentMapper.toResponse(payment, withdrawalResult);
    }


    /**
     * 유저가 구매한 상품이 재고가 없거나 구매가 되어버렸을때 환불을 해주는 프로세스
     * wallet refund, orderitem cancel, paymetn에 입출금 기록
     * 관리자 기능
     *
     * @param adminUuid
     * @param orderItemUuid
     * @return 환불 응답 DTO
     */
    @Override
    @Transactional
    public PaymentResponseDto refundPayment(UUID adminUuid, String orderItemUuid, String reason) {

        findByAdminWithAdminValidate(adminUuid);

        return executeWithOptimisticLockRetry(() ->
                processPaymentRefund(adminUuid, orderItemUuid, reason));

    }

    private PaymentResponseDto processPaymentRefund(UUID adminUuid, String orderItemUuid, String reason) {

        log.info("Refunding payment for user uuid:{}", adminUuid);

        findByAdminWithAdminValidate(adminUuid);

        OrderItem orderItem = orderItemRepository.findByOrderItemUuidWithOrderAndCustomer(UUID.fromString(orderItemUuid))
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("Order item not found for order Uuid: %s", orderItemUuid)));
        orderItem.isCancel();

        Wallet wallet = walletRepository.findByCustomerCustomerId(orderItem.getOrder().getCustomer().getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for customer id: %s", orderItem.getOrder().getCustomer().getCustomerId())));

        Payment payment = paymentFactory.createWithRefundPayment(orderItem, adminUuid, wallet, orderItem.getPriceOriginal(), reason);
        paymentValidator.requiredCompletedStatus(payment);

        try {
            walletService.deposit(wallet.getCustomer().getCustomerUuid(), wallet.getWalletUuid(), orderItem.getPriceOriginal());
        } catch (WalletUpdateFailedException e) {
            throw new PaymentProcessingException("Withdrawal Failed");
        }

        if (payment.getPaymentStatus().canTransitionTo(PaymentStatus.PENDING)) {
            payment.completeByAdmin(adminUuid);
        }
        paymentRepository.save(payment);

        log.info("Refunded payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }

    /**
     * 거래가 진행되지 않고 결제전 보류 상태에서 유저가 수동으로 취소가 가능함
     *
     * @param userUuid
     * @param paymentUuid
     * @return 취소 응답 DTO
     */
    @Override // 거래가 보류 상태일때 취소가능
    public PaymentResponseDto cancelPayment(UUID userUuid, String paymentUuid) {
        log.info("Cancel payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);

        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);
        paymentValidator.requiredPendingStatus(payment);
        if (payment.getPaymentStatus().canTransitionTo(PaymentStatus.PENDING)) {
            payment.cancelPayment();
        }
        paymentRepository.save(payment);

        log.info("Cancel payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }

    /**
     * 유저가 결제정보 확인
     *
     * @param userUuid
     * @param paymentUuid
     * @return 결제 정보 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentInfo(UUID userUuid, String paymentUuid) {
        log.info("Searching payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);

        return paymentMapper.toResponse(payment);
    }

    /**
     * 관리자가 유저 결제정보를 확인
     *
     * @param userUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public PaymentResponseDto getPaymentInfoByAdmin(UUID userUuid, String paymentUuid) {

        log.info("Searching payment for user uuid:{}", userUuid);

        findByAdminWithAdminValidate(userUuid);

        Payment payment = findPaymentByPaymentUuid(paymentUuid);

        return paymentMapper.toResponse(payment);
    }


    public PaymentResponseDto executeWithOptimisticLockRetry(Supplier<PaymentResponseDto> operation) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                return operation.get();
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                log.warn("Optimistic lock failure, retry{}/{}", retryCount, maxRetries);

                if (retryCount >= maxRetries) {
                    throw new PaymentProcessingException("Payment completion failed due to concurrent access");
                }

                try {
                    Thread.sleep(100 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PaymentProcessingException("Payment processing interrupted");
                }
            }
        }
        throw new PaymentProcessingException("Unexpected error in payment completion");
    }


    /*
    Helper method
     */

    private void findByAdminWithAdminValidate(UUID adminUuid) {
        User user = findUserByUserUuid(adminUuid);
        userValidator.requireAdminRole(user);
    }

    private Wallet findWalletByCustomerUuid(UUID userUuid) {
        return walletRepository.findByUserUserUuidWithCustomerAndUser(userUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found for userUuid: %s", userUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));
    }

    private Payment findPaymentByCustomerUuidAndPaymentUuid(UUID customerUuid, String paymentUuid) {
        return paymentRepository.findByCustomerCustomerUuidAndPaymentUuidWithCustomerAndWallet(customerUuid, UUID.fromString(paymentUuid))
                .orElseThrow(() -> new PaymentNotFoundException("CustomerUuidAndPaymentUuid: Payment not found"));
    }

    private Wallet findWalletByCustomerId(Customer customer) {
        return walletRepository.findByCustomerCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer uuid: wallet not found"));
    }

    private User findUserByUserUuid(UUID adminUuid) {
        return userRepository.findByUserUuid(adminUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("admin not found for admin uuid: %s", adminUuid)));
    }

    private Payment findPaymentByPaymentUuid(String paymentUuid) {
        return paymentRepository.findByPaymentUuidWithCustomerAndWallet(UUID.fromString(paymentUuid))
                .orElseThrow(() -> new PaymentNotFoundException("Payment with the provided UUID not found"));
    }
}