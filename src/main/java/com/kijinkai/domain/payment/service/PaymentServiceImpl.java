package com.kijinkai.domain.payment.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.payment.dto.PaymentRequestDto;
import com.kijinkai.domain.payment.dto.PaymentResponseDto;
import com.kijinkai.domain.payment.entity.Payment;
import com.kijinkai.domain.payment.entity.PaymentStatus;
import com.kijinkai.domain.payment.exception.PaymentNotFoundException;
import com.kijinkai.domain.payment.factory.PaymentFactory;
import com.kijinkai.domain.payment.mapper.PaymentMapper;
import com.kijinkai.domain.payment.repository.PaymentRepository;
import com.kijinkai.domain.payment.validate.PaymentValidator;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public PaymentResponseDto createPaymentWithValidate(String userUuid, PaymentRequestDto requestDto) {
        log.info("Creating payment for user uuid:{}", userUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Wallet wallet = findWalletByCustomerId(customer);

        paymentValidator.validateAmount(requestDto);
        Payment payment = paymentFactory.createPayment(customer, wallet, requestDto);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Created payment for payment uuid:{}", savedPayment.getPaymentUuid());
        return paymentMapper.toResponse(savedPayment);
    }


    @Override
    public PaymentResponseDto completedPayment(String userUuid, String paymentUuid) {
        log.info("Completed payment for user uuid:{}", userUuid);
        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);

        paymentValidator.requiredPendingStatus(payment);

        //wallet balance update
        Wallet wallet = payment.getWallet();
        wallet.increaseBalance(payment.getAmountConverter());
        walletRepository.save(wallet);

        payment.updatePaymentStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        log.info("Confirmed payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }

    @Override // 관리자기능 //일단 환불할떄 시스템으로 환불 처리만 해주고 직접 수동으로 입금
    public PaymentResponseDto refundedPayment(String userUuid, String paymentUuid) {
        log.info("Refunding payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);

        paymentValidator.requiredCompletedStatus(payment);

        //wallet balance update
        Wallet wallet = payment.getWallet();
        wallet.decreaseBalance(payment.getAmountConverter());
        walletRepository.save(wallet);

        userValidator.requireAdminRole(customer.getUser());
        payment.updatePaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        log.info("Refunded payment for payment uuid:{}", payment.getPaymentUuid());
        return paymentMapper.toResponse(payment);
    }

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

    @Override @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentInfo(String userUuid, String paymentUuid) {
        log.info("Searching payment for user uuid:{}", userUuid);

        Customer customer = findCustomerByUserUuid(userUuid);
        Payment payment = findPaymentByCustomerUuidAndPaymentUuid(customer, paymentUuid);

        return paymentMapper.toResponse(payment);
    }


    private Customer findCustomerByUserUuid(String userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("UserUuid: Customer not found"));
    }

    private Payment findPaymentByCustomerUuidAndPaymentUuid(Customer customer,String paymentUuid) {
        return paymentRepository.findByCustomerCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid)
                .orElseThrow(() -> new PaymentNotFoundException("CustomerUuidAndPaymentUuid: Payment not found"));
    }

    private Wallet findWalletByCustomerId(Customer customer) {
        return walletRepository.findByCustomerCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer uuid: wallet not found"));
    }
}
