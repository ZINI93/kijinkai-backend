package com.kijinkai.domain.payment.application.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.OrderPaymentRequestDto;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentResponseDto;
import com.kijinkai.domain.payment.application.port.in.PaymentUseCase;
import com.kijinkai.domain.payment.application.port.out.*;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.exception.*;
import com.kijinkai.domain.payment.domain.repository.DepositRequestRepository;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.payment.domain.repository.RefundRequestRepository;
import com.kijinkai.domain.payment.domain.repository.WithdrawRequestRepository;
import com.kijinkai.domain.payment.domain.service.DepositRequestService;
import com.kijinkai.domain.payment.domain.service.OrderPaymentService;
import com.kijinkai.domain.payment.domain.service.RefundRequestService;
import com.kijinkai.domain.payment.domain.service.WithdrawRequestService;
import com.kijinkai.domain.payment.application.dto.request.DepositRequestDto;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.payment.application.dto.request.WithdrawRequestDto;
import com.kijinkai.domain.payment.application.dto.response.DepositRequestResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
import com.kijinkai.domain.payment.application.dto.response.WithdrawResponseDto;
import com.kijinkai.domain.payment.domain.util.PaymentContents;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.exception.WalletNotActiveException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentApplicationService implements PaymentUseCase {

    // domain service
    private final DepositRequestService depositRequestService;
    private final WithdrawRequestService withdrawRequestService;
    private final RefundRequestService refundRequestService;
    private final OrderPaymentService orderPaymentService;
    //ports
    private final CustomerPort customerPort;
    private final WalletPort walletPort;
    private final UserPort userPort;
    private final OrderPort orderPort;
    private final OrderItemPort orderItemPort;
    private final ExchangePort exchangePort;


    //repository
    private final DepositRequestRepository depositRequestRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final RefundRequestRepository refundRequestRepository;
    private final OrderPaymentRepository orderPaymentRepository;


    //util
    private final PaymentMapper paymentMapper;
    private final PriceCalculationService priceCalculationService;

    //----- 입금  -----

    /**
     * 입금 요청 생성
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public DepositRequestResponseDto processDepositRequest(UUID userUuid, DepositRequestDto requestDto) {

        log.info("Creating deposit request for user uuid: {}", userUuid);

        Customer customer = customerPort.findByUserUuid(userUuid);
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        BigDecimal exchangeRate = exchangePort.exchangeRate(requestDto.getOriginalCurrency(), Currency.JPY);

        DepositRequest request = depositRequestService.createDepositRequest(
                customer, wallet, requestDto.getAmountOriginal(), requestDto.getOriginalCurrency(),
                exchangeRate, requestDto.getDepositorName(), requestDto.getBankAccount());

        DepositRequest saveDepositRequest = depositRequestRepository.save(request);

        log.info("Created deposit request for request uuid: {}", saveDepositRequest.getRequestUuid());

        return paymentMapper.createDepositResponse(saveDepositRequest);
    }

    /**
     * 입금 요청 승인
     *
     * @param requestUuid
     * @param adminUuid
     * @param memo
     * @return
     */

    @Transactional
    @Override
    public DepositRequestResponseDto approveDepositRequest(UUID requestUuid, UUID adminUuid, String memo) {

        log.info("Start deposit approval process for request uuid: {}", requestUuid);

        DepositRequest depositRequest = findDepositRequestByRequestUuid(requestUuid);

        try {
            DepositRequest approvedRequest = depositRequestService.approveDepositRequest(depositRequest, adminUuid, memo);

            WalletResponseDto wallet = walletPort.deposit(
                    approvedRequest.getCustomerUuid(),
                    approvedRequest.getWalletUuid(),
                    approvedRequest.getAmountConverted());

            DepositRequest savedRequest = depositRequestRepository.save(approvedRequest);

            log.info("Completed deposit approval process for request uuid: {} , charged amount: {}", requestUuid, approvedRequest.getAmountConverted());

            return paymentMapper.approveDepositResponse(savedRequest, wallet);
        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for deposit approval: {}", requestUuid, e);
            depositRequestService.markAsFailed(depositRequest, "잔액 부족: " + e.getMessage());
            throw new DepositApprovalException("잔액이 부족합니다", e);

        } catch (WalletNotActiveException e) {
            log.error("Wallet not active for deposit approval: {}", requestUuid, e);
            depositRequestService.markAsFailed(depositRequest, "비활성 지갑: " + e.getMessage());
            throw new DepositApprovalException("지갑이 비활성 상태입니다", e);

        } catch (DepositRequestNotFoundException | DepositRequestStatusException e) {
            log.error("Invalid deposit request for approval: {}", requestUuid, e);
            throw e; // 다시 던지기

        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("Unexpected error during deposit approval: {}", requestUuid, e);
            depositRequestService.markAsFailed(depositRequest, "시스템 오류: " + e.getMessage());
            throw new PaymentProcessingException("입금 승인 중 예상치 못한 오류가 발생했습니다", e);
        }
    }

    /**
     * 관리자가 유저 거래정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public DepositRequestResponseDto getDepositRequestInfoByAdmin(UUID requestUuid, UUID userUuid) {

        // admin account in user
        User admin = userPort.findUserByUserUuid(userUuid);
        DepositRequest depositRequest = findDepositRequestByRequestUuid(requestUuid);
        DepositRequest deposit = depositRequestService.getDepositInfoByAdmin(depositRequest, admin);

        log.info("retrieved deposit request for request uuid: {} by admin", requestUuid);
        return paymentMapper.depositInfoResponse(deposit);
    }


    /**
     * 유저가 본인 거래정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public DepositRequestResponseDto getDepositRequestInfo(UUID requestUuid, UUID userUuid) {

        Customer customer = customerPort.findByUserUuid(userUuid);
        DepositRequest depositRequest = findDepositRequestByRequest(requestUuid, customer);

        DepositRequest deposit = depositRequestService.getDepositInfo(depositRequest, customer);

        log.info("retrieved deposit request for request uuid: {}", requestUuid);
        return paymentMapper.depositInfoResponse(deposit);
    }

    /**
     * 시간 지난 결제 전체 만료처리
     *
     * @return
     */
    @Override
    public List<DepositRequestResponseDto> expireOldRequests() {
        List<DepositRequest> pendingRequests = depositRequestRepository.findByStatus(DepositStatus.PENDING_ADMIN_APPROVAL);
        List<DepositRequest> expiredRequests = depositRequestService.expireOldRequests(pendingRequests);
        List<DepositRequest> savedRefund = depositRequestRepository.saveAll(expiredRequests);

        return savedRefund.stream().map(paymentMapper::depositInfoResponse).collect(Collectors.toList());
    }

    //----- 출금  -----

    /**
     * 출금 생성
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto processWithdrawRequest(UUID userUuid, WithdrawRequestDto requestDto) {

        log.info("Creating withdraw request for user uuid: {}", userUuid);

        Customer customer = customerPort.findByUserUuid(userUuid);
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());
        BigDecimal withdrawFee = PaymentContents.WITHDRAWAL_FEE;

        BigDecimal convertedAmount = priceCalculationService.convertAndCalculateTotalInLocalCurrency(requestDto.getRequestAmount(), requestDto.getTargetCurrency());

        WithdrawRequest request = withdrawRequestService.createWithdrawRequest(
                customer, wallet, requestDto.getRequestAmount(), requestDto.getTargetCurrency(), requestDto.getBankName(), requestDto.getAccountHolder(), withdrawFee, convertedAmount
        );

        WithdrawRequest savedWithdrawRequest = withdrawRequestRepository.save(request);

        log.info("Created withdraw request for request uuid: {}", request.getRequestUuid());

        return paymentMapper.createWithdrawResponse(savedWithdrawRequest);
    }

    /**
     * 출금 요청 승인
     *
     * @param requestUuid
     * @param adminUuid
     * @param memo
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto approveWithdrawRequest(UUID requestUuid, UUID adminUuid, String memo) {

        log.info("Start withdraw approval process for request uuid: {}", requestUuid);

        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuid(requestUuid);

        try {
            BigDecimal exchangeRate = exchangePort.exchangeRate(Currency.JPY, withdrawRequest.getTargetCurrency());
            WithdrawRequest approvedWithdrawRequest = withdrawRequestService.approveWithdrawRequest(withdrawRequest, adminUuid, memo, exchangeRate);

            WalletResponseDto wallet = walletPort.withdrawal(
                    approvedWithdrawRequest.getCustomerUuid(),
                    approvedWithdrawRequest.getWalletUuid(),
                    approvedWithdrawRequest.getTotalDeductAmount());

            log.info("Completed withdraw approval process for request uuid: {} , withdraw amount: {}", requestUuid, approvedWithdrawRequest.getConvertedAmount());
            return paymentMapper.approvedWithdrawResponse(approvedWithdrawRequest, wallet);

        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for deposit approval: {}", requestUuid, e);
            withdrawRequestService.markAsFailed(withdrawRequest, "잔액 부족:" + e.getMessage());
            throw new WithdrawApprovalException("잔액이 부족합니다", e);
        } catch (WalletNotActiveException e) {
            log.error("Wallet not active for deposit approval: {}", requestUuid, e);
            withdrawRequestService.markAsFailed(withdrawRequest, "비활성된 지갑: " + e.getMessage());
            throw new WalletNotActiveException("지갑이 비활성 상태입니다", e);
        } catch (WithdrawRequestNotFoundException | WithdrawRequestStatusException e) {
            log.error("Invalid withdraw request for approval: {} ", requestUuid, e);
            throw e;
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("Unexpected error during deposit approval: {}", requestUuid, e);
            withdrawRequestService.markAsFailed(withdrawRequest, "시스템 오류: " + e.getMessage());
            throw new PaymentProcessingException("출금 승인 중 예상치 못한 오류가 발생했습니다", e);
        }

    }

    /**
     * 관리자가 유저의 출금 요청 정보 조회
     *
     * @param requestUuid
     * @param adminUuid
     * @return
     */
    @Override
    public WithdrawResponseDto getWithdrawInfoByAdmin(UUID requestUuid, UUID adminUuid) {

        User admin = userPort.findUserByUserUuid(adminUuid);
        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuid(requestUuid);

        WithdrawRequest request = withdrawRequestService.getWithdrawInfoByAdmin(withdrawRequest, admin);

        log.info("Retrieved withdraw request for request uuid: {} by admin", requestUuid);

        return paymentMapper.withdrawInfoResponse(request);
    }

    /**
     * 유저가 본인 출금 요청 정보 조회
     *
     * @param requestUuid
     * @param userUuid
     * @return
     */
    @Override
    public WithdrawResponseDto getWithdrawInfo(UUID requestUuid, UUID userUuid) {

        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuidAndUserUuid(requestUuid, userUuid);
        WithdrawRequest request = withdrawRequestService.getWithdrawInfo(withdrawRequest);

        log.info("Retrieved withdraw request for request uuid: {}", requestUuid);
        return paymentMapper.withdrawInfoResponse(request);
    }


    //----- 환불  -----

    /**
     * 환불 프로세스 생성
     *
     * @param adminUuid
     * @param orderItemUuid
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public RefundResponseDto processRefundRequest(UUID adminUuid, UUID orderItemUuid, RefundRequestDto requestDto) {

        log.info("Creating refund request for admin uuid: {}", adminUuid);

        OrderItem orderItem = orderItemPort.findByOrderItemUuid(orderItemUuid);
        orderItem.isCancel();
        Customer customer = customerPort.findByCustomerUuid(orderItem.getCustomerUuid());
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        RefundRequest refundRequest = refundRequestService.createRefundRequest(
                customer, wallet, orderItem, orderItem.getPriceOriginal(), adminUuid,
                requestDto.getRefundReason(), requestDto.getRefundType());

        RefundRequest savedRefundRequest = refundRequestRepository.save(refundRequest);

        log.info("Created refund request for refund uuid: {}", savedRefundRequest.getRefundUuid());

        return paymentMapper.createRefundResponse(savedRefundRequest);
    }

    /**
     * 환불 처리 승인
     *
     * @param refundUuid
     * @param adminUuid
     * @param memo
     * @return
     */
    @Transactional
    @Override
    public RefundResponseDto approveRefundRequest(UUID refundUuid, UUID adminUuid, String memo) {

        log.info("Start refund approval process for refund uuid: {}", refundUuid);

        User admin = userPort.findUserByUserUuid(adminUuid);
        RefundRequest request = findRefundRequestByRefundUuid(refundUuid);

        try {
            RefundRequest refundRequest = refundRequestService.processRefundRequest(request, admin, memo);
            WalletResponseDto wallet = walletPort.deposit(
                    request.getCustomerUuid(),
                    request.getWalletUuid(),
                    request.getRefundAmount()
            );
            RefundRequest savedRefundRequest = refundRequestRepository.save(refundRequest);

            log.info("Completed refund approval process for refund uuid: {}", savedRefundRequest.getRefundUuid());
            return paymentMapper.processRefundResponse(savedRefundRequest, wallet);
        } catch (OptimisticLockException e) {  // 이 부분 추가 필요
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("refund approval rollback due to charge failed");
            throw new PaymentProcessingException("Failed charge");
        }
    }

    /**
     * 관리자의 환불 내역 조회
     *
     * @param refundUuid
     * @param adminUuid
     * @return
     */

    @Override
    public RefundResponseDto getRefundInfoByAdmin(UUID refundUuid, UUID adminUuid) {

        User admin = userPort.findUserByUserUuid(adminUuid);
        RefundRequest refundRequest = findRefundRequestByRefundUuid(refundUuid);

        RefundRequest refund = refundRequestService.getRefundInfoByAdmin(refundRequest, admin);
        log.info("retrieved refund request for refund uuid by admin: {}", refundUuid);
        return paymentMapper.refundInfoResponse(refund);
    }

    /**
     * 유저의 환불 내역 조회
     *
     * @param refundUuid
     * @param userUuid
     * @return
     */
    @Override
    public RefundResponseDto getRefundInfo(UUID refundUuid, UUID userUuid) {

        Customer customer = customerPort.findByUserUuid(userUuid);
        RefundRequest request = findRefundRequestByRefundUuidAndCustomer(refundUuid, customer.getCustomerUuid());

        RefundRequest refund = refundRequestService.getRefundInfo(request);

        log.info("retrieved refund request for refund uuid: {}", refundUuid);
        return paymentMapper.refundInfoResponse(refund);
    }

    // -------- 상품에 대한 결제

    /**
     * 유저가 제출한 Order에 대해 결제 생성
     *
     * @param adminUuid
     * @param orderUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto createFirstPayment(UUID adminUuid, UUID orderUuid) {
        User admin = userPort.findUserByUserUuid(adminUuid);
        Order order = orderPort.findOrderByOrderUuid(orderUuid);
        Wallet wallet = walletPort.findByCustomerUuid(order.getCustomer().getCustomerUuid());
        OrderPayment orderPayment = orderPaymentService.crateOrderPayment(order.getCustomer(), wallet, order, order.getFinalPriceOriginal(), admin);
        OrderPayment savedOrderPayment = orderPaymentRepository.save(orderPayment);

        return paymentMapper.createOrderPayment(savedOrderPayment);
    }


    /**
     * 상품의 값에 대한 첫번째 결제
     *
     * @param userUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto completeFirstPayment(UUID userUuid, UUID paymentUuid) {

        Customer customer = customerPort.findByUserUuid(userUuid);
        OrderPayment findByOrderPayment = findOrderPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);

        try {
            Wallet findWallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());
            WalletResponseDto wallet = walletPort.deposit(
                    customer.getCustomerUuid(),
                    findWallet.getWalletUuid(),
                    findByOrderPayment.getPaymentAmount()
            );
            OrderPayment orderPayment = orderPaymentService.completePayment(findByOrderPayment);

            Order order = orderPort.findOrderByOrderUuid(orderPayment.getOrderUuid());
            order.fistOrderPayment();

            OrderPayment savedOrderPayment = orderPaymentRepository.save(orderPayment);

            return paymentMapper.completeOrderPayment(savedOrderPayment, wallet);
        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for payment complete: {}", paymentUuid);
            orderPaymentService.markAsFailed(findByOrderPayment, "잔액 부족:" + e.getMessage());
            throw new OrderPaymentCompletionException("잔액이 부족합니다.");
        } catch (WalletNotActiveException e) {
            log.error("Wallet not active for order payment: {}", paymentUuid, e);
            orderPaymentService.markAsFailed(findByOrderPayment, "비활성된 지갑: " + e.getMessage());
            throw new WalletNotActiveException("지갑이 비활성 상태입니다", e);
        } catch (OrderPaymentNotFoundException | OrderPaymentStatusException e) {
            log.error("Invalid order payment for approval: {} ", paymentUuid, e);
            throw e;
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("Unexpected error during order complete: {}", paymentUuid, e);
            orderPaymentService.markAsFailed(findByOrderPayment, "시스템 오류: " + e.getMessage());
            throw new PaymentProcessingException("결제 완료 중 예상치 못한 오류가 발생했습니다", e);
        }

    }

    /**
     * 상품에 대한 배송비 결제
     *
     * @param adminUuid
     * @param orderUuid
     * @param requestDto
     * @return
     */
    @Override
    public OrderPaymentResponseDto createSecondPayment(UUID adminUuid, UUID orderUuid, OrderPaymentRequestDto requestDto) {
        User admin = userPort.findUserByUserUuid(adminUuid);
        Order order = orderPort.findOrderByOrderUuid(orderUuid);
        Wallet wallet = walletPort.findByCustomerUuid(order.getCustomer().getCustomerUuid());

        OrderPayment secondOrderPayment = orderPaymentService.createSecondOrderPayment(order.getCustomer(), wallet, order, requestDto.getDeliveryFee(), admin);

        return paymentMapper.createOrderPayment(secondOrderPayment);
    }


    /**
     * 유저가 결제된 상품에 대한 배송비 결제
     *
     * @param userUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto completeSecondPayment(UUID userUuid, UUID paymentUuid) {

        Customer customer = customerPort.findByUserUuid(userUuid);
        OrderPayment findByOrderPayment = findOrderPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);

        try {
            Wallet findWallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());
            WalletResponseDto wallet = walletPort.deposit(
                    customer.getCustomerUuid(),
                    findWallet.getWalletUuid(),
                    findByOrderPayment.getPaymentAmount()
            );
            OrderPayment orderPayment = orderPaymentService.completeSecondOrderPayment(findByOrderPayment);

            Order order = orderPort.findOrderByOrderUuid(orderPayment.getOrderUuid());
            order.secondOrderPayment();

            OrderPayment savedOrderPayment = orderPaymentRepository.save(orderPayment);

            return paymentMapper.completeOrderPayment(savedOrderPayment, wallet);
        } catch (InsufficientBalanceException e) {
            log.error("Insufficient balance for second payment complete: {}", paymentUuid);
            orderPaymentService.markAsFailed(findByOrderPayment, "잔액 부족:" + e.getMessage());
            throw new OrderPaymentCompletionException("잔액이 부족합니다.");
        } catch (WalletNotActiveException e) {
            log.error("Wallet not active for order payment: {}", paymentUuid, e);
            orderPaymentService.markAsFailed(findByOrderPayment, "비활성된 지갑: " + e.getMessage());
            throw new WalletNotActiveException("지갑이 비활성 상태입니다", e);
        } catch (OrderPaymentNotFoundException | OrderPaymentStatusException e) {
            log.error("Invalid order payment for approval: {} ", paymentUuid, e);
            throw e;
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("Unexpected error during order complete: {}", paymentUuid, e);
            orderPaymentService.markAsFailed(findByOrderPayment, "시스템 오류: " + e.getMessage());
            throw new PaymentProcessingException("결제 완료 중 예상치 못한 오류가 발생했습니다", e);
        }
    }

    /**
     * 관리자가 오더 거래내역 조회
     *
     * @param adminUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto getOrderPaymentInfoByAdmin(UUID adminUuid, UUID paymentUuid) {
        User admin = userPort.findUserByUserUuid(adminUuid);
        OrderPayment findByorderPayment = orderPaymentRepository.findByPaymentUuid(paymentUuid)
                .orElseThrow(() -> new OrderPaymentNotFoundException(String.format("Order payment not found for payment uuid: %s", paymentUuid)));
        OrderPayment orderPayment = orderPaymentService.getOrderPaymentInfoByAdmin(admin, findByorderPayment);
        return paymentMapper.orderPaymentInfo(orderPayment);
    }


    /**
     * 유저가 본인의 거래내역 조회
     *
     * @param userUuid
     * @param paymentUuid
     * @return
     */
    @Override
    public OrderPaymentResponseDto getOrderPaymentInfo(UUID userUuid, UUID paymentUuid) {
        Customer customer = customerPort.findByUserUuid(userUuid);
        OrderPayment findByOrderPayment = findOrderPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);
        OrderPayment orderPayment = orderPaymentService.getOrderPaymentInfo(findByOrderPayment);
        return paymentMapper.orderPaymentInfo(orderPayment);
    }


    // helper method
    private DepositRequest findDepositRequestByRequestUuid(UUID requestUuid) {
        return depositRequestRepository.findByRequestUuid(requestUuid)
                .orElseThrow(() -> new DepositNotFoundException(String.format("Deposit request not found for request uuid: %s", requestUuid)));
    }

    private DepositRequest findDepositRequestByRequest(UUID requestUuid, Customer customer) {
        return depositRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid, customer.getCustomerUuid())
                .orElseThrow(() -> new DepositNotFoundException(String.format("Deposit request not found for request uuid: %s and customer uuid: %s", requestUuid, customer.getCustomerUuid())));
    }


    private OrderPayment findOrderPaymentByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid) {
        return orderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid)
                .orElseThrow(() -> new OrderPaymentNotFoundException(String.format("Order payment not found for customer uuid: %s and payment uuid: %s ", customerUuid, paymentUuid)));
    }

    private WithdrawRequest findWithdrawRequestByRequestUuid(UUID requestUuid) {
        return withdrawRequestRepository.findByRequestUuid(requestUuid)
                .orElseThrow(() -> new WithdrawRequestNotFoundException(String.format("Withdraw request not found for request uuid: %s", requestUuid)));
    }

    private WithdrawRequest findWithdrawRequestByRequestUuidAndUserUuid(UUID requestUuid, UUID userUuid) {
        return withdrawRequestRepository.findByRequestUuidAndUserUuid(requestUuid, userUuid)
                .orElseThrow(() -> new WithdrawRequestNotFoundException(String.format("Withdraw request not found for request uuid: %s", requestUuid)));
    }

    private RefundRequest findRefundRequestByRefundUuid(UUID refundUuid) {
        return refundRequestRepository.findByRequestUuid(refundUuid)
                .orElseThrow(() -> new RefundNotFoundException(String.format("Refund not found for request uuid: %s", refundUuid)));
    }

    private RefundRequest findRefundRequestByRefundUuidAndCustomer(UUID refundUuid, UUID customerUuid) {
        return refundRequestRepository.findByRefundUuidAndCustomerUuid(refundUuid, customerUuid)
                .orElseThrow(() -> new RefundNotFoundException(String.format("Refund request not found for request uuid: %s and customer uuid: %s", refundUuid, customerUuid)));
    }
}




