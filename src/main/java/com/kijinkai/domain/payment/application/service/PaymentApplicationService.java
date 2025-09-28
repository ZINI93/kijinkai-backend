package com.kijinkai.domain.payment.application.service;


import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.PriceCalculationService;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.validator.OrderItemValidator;
import com.kijinkai.domain.payment.application.dto.request.*;
import com.kijinkai.domain.payment.application.dto.response.*;
import com.kijinkai.domain.payment.application.port.in.PaymentUseCase;
import com.kijinkai.domain.payment.application.port.out.*;
import com.kijinkai.domain.payment.domain.calculator.PaymentCalculator;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import com.kijinkai.domain.payment.domain.exception.*;
import com.kijinkai.domain.payment.domain.repository.DepositRequestRepository;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.payment.domain.repository.RefundRequestRepository;
import com.kijinkai.domain.payment.domain.repository.WithdrawRequestRepository;
import com.kijinkai.domain.payment.domain.service.DepositRequestService;
import com.kijinkai.domain.payment.domain.service.OrderPaymentService;
import com.kijinkai.domain.payment.domain.service.RefundRequestService;
import com.kijinkai.domain.payment.domain.service.WithdrawRequestService;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.domain.util.PaymentContents;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.exception.WalletNotActiveException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final OrderItemPort orderItemPort;
    private final ExchangePort exchangePort;

    //repository
    private final DepositRequestRepository depositRequestRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final RefundRequestRepository refundRequestRepository;
    private final OrderPaymentRepository orderPaymentRepository;

    //util
    private final PaymentMapper paymentMapper;
    private final OrderItemValidator orderItemValidator;
    private final PriceCalculationService priceCalculationService;
    private final PaymentCalculator paymentCalculator;

    //----- 입금  -----

    /**
     * 입금 요청 생성
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public DepositRequestResponseDto processDepositRequest(UUID userUuid, DepositRequestDto requestDto) {

        log.info("Creating deposit request for user uuid: {}", userUuid);

        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        ExchangeRateResponseDto exchangeRate = exchangePort.getExchangeRateInfoByCurrency(requestDto.getOriginalCurrency());
        BigDecimal convertedAmount = paymentCalculator.calculateDepositInJpy(exchangeRate.getCurrency(), requestDto.getAmountOriginal());

        DepositRequest request = depositRequestService.createDepositRequest(
                customer, wallet, requestDto.getAmountOriginal(), requestDto.getOriginalCurrency(),
                exchangeRate.getRate(), requestDto.getDepositorName(), requestDto.getBankType(), convertedAmount);

        DepositRequest saveDepositRequest = depositRequestRepository.save(request);

        log.info("Created deposit request for request uuid: {}", saveDepositRequest.getRequestUuid());

        return paymentMapper.createDepositResponse(saveDepositRequest);
    }

    /**
     * 입금 요청 승인
     * 관리자가 입금을 확인 후에 승인
     *
     * @param requestUuid
     * @param adminUuid
     * @param requestDto
     * @return
     */

    @Transactional
    @Override
    public DepositRequestResponseDto approveDepositRequest(UUID requestUuid, UUID adminUuid, DepositRequestDto requestDto) {

        log.info("Start deposit approval process for request uuid: {}", requestUuid);

        DepositRequest depositRequest = findDepositRequestByRequestUuid(requestUuid);

        if (depositRequest.getStatus() != DepositStatus.PENDING_ADMIN_APPROVAL) {
            throw new IllegalStateException("승인 대기중이 아닙니다.");
        }

        try {
            DepositRequest approvedRequest = depositRequestService.approveDepositRequest(depositRequest, adminUuid, requestDto.getMemo());

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
            throw e;

        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("Unexpected error during deposit approval: {}", requestUuid, e);
            depositRequestService.markAsFailed(depositRequest, "시스템 오류: " + e.getMessage());
            throw new PaymentProcessingException("입금 승인 중 예상치 못한 오류가 발생했습니다", e);
        }
    }

    //입금 page 조회/

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
     * @param adminUuid
     * @param depositorName
     * @param pageable
     * @return
     */
    @Override
    public Page<DepositRequestResponseDto> getDepositsByApprovalPending(UUID adminUuid, String depositorName, Pageable pageable) {
        User user = userPort.findUserByUserUuid(adminUuid);
        Customer customer = customerPort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", user.getUserUuid())));
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        Page<DepositRequest> depositRequests = depositRequestRepository.findByDepositPaymentUuidByStatus(customer.getCustomerUuid(), depositorName, DepositStatus.PENDING_ADMIN_APPROVAL, pageable);

        return depositRequests.map(depositRequest -> paymentMapper.depositDetailsInfo(depositRequest, wallet.getBalance()));
    }

    @Override
    public Page<DepositRequestResponseDto> getDeposits(UUID userUuid, Pageable pageable) {
        User user = userPort.findUserByUserUuid(userUuid);
        Customer customer = customerPort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        Page<DepositRequest> depositRequests = depositRequestRepository.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return depositRequests.map(paymentMapper::depositInfoResponse);
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

        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
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

        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());
        BigDecimal withdrawFee = PaymentContents.WITHDRAWAL_FEE;
        ExchangeRateResponseDto exchangeRate = exchangePort.getExchangeRateInfoByCurrency(requestDto.getCurrency());


        BigDecimal convertedAmount = paymentCalculator.calculateWithdrawInJyp(requestDto.getCurrency(), requestDto.getRequestAmount());

        WithdrawRequest request = withdrawRequestService.createWithdrawRequest(
                customer, wallet, requestDto.getRequestAmount(), requestDto.getCurrency(), requestDto.getBankName(), requestDto.getAccountHolder(), withdrawFee, convertedAmount,
                requestDto.getAccountNumber(),exchangeRate.getRate()
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
     * @param requestDto
     * @return
     */
    @Transactional
    @Override
    public WithdrawResponseDto approveWithdrawRequest(UUID requestUuid, UUID adminUuid, WithdrawRequestDto requestDto) {


        log.info("Start withdraw approval process for request uuid: {}", requestUuid);

        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuid(requestUuid);

        try {
            WithdrawRequest approvedWithdrawRequest = withdrawRequestService.approveWithdrawRequest(withdrawRequest, adminUuid, requestDto.getMemo(), withdrawRequest.getExchangeRate());

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

    @Override
    public Page<WithdrawResponseDto> getWithdraws(UUID adminUuid, Pageable pageable) {
        User user = userPort.findUserByUserUuid(adminUuid);
        Customer customer = customerPort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", user.getUserUuid())));
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());
        Page<WithdrawRequest> withdraws = withdrawRequestRepository.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return withdraws.map(withdraw -> paymentMapper.withdrawDetailsInfo(withdraw, wallet.getBalance()));
    }

    /**
     *
     * @param adminUuid
     * @param depositorName
     * @param pageable
     * @return
     */
    @Override
    public Page<WithdrawResponseDto> getWithdrawByApprovalPending(UUID adminUuid, String depositorName, Pageable pageable) {
        User user = userPort.findUserByUserUuid(adminUuid);
        Customer customer= customerPort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", user.getUserUuid())));
        Page<WithdrawRequest> withdrawRequests = withdrawRequestRepository.findByWithdrawPaymentUuidByStatus(customer.getCustomerUuid(), depositorName, WithdrawStatus.PENDING_ADMIN_APPROVAL, pageable);
        return withdrawRequests.map(paymentMapper::withdrawInfoResponse);

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

        Customer customer= customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        WithdrawRequest withdrawRequest = findWithdrawRequestByRequestUuidAndUserUuid(requestUuid, customer.getCustomerUuid());
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
        Customer customer = customerPort.findByCustomerUuid(orderItem.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", adminUuid)));
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

        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        RefundRequest request = findRefundRequestByRefundUuidAndCustomer(refundUuid, customer.getCustomerUuid());

        RefundRequest refund = refundRequestService.getRefundInfo(request);

        log.info("retrieved refund request for refund uuid: {}", refundUuid);
        return paymentMapper.refundInfoResponse(refund);
    }

    @Override
    public Page<RefundResponseDto> getRefunds(UUID adminUuid, Pageable pageable) {
        User user = userPort.findUserByUserUuid(adminUuid);
        Customer customer = customerPort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", user.getUserUuid())));
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        Page<RefundRequest> refunds = refundRequestRepository.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return  refunds.map(refund -> paymentMapper.refundDetailsInfo(refund, wallet.getBalance()));
    }


    ///  결제


    /**
     * 상품의 값에 대한 첫번째 결제
     * 2025.08.19 수정 - 유저가 결제 할 수 있도록 변경, 관리자는 결제 요청 들어오면 바로 바로 구매 하도록 하고, 낙찰된 물건은 환불
     * orderitem uuid를 받아와서 paymnet 결제 작성 order를 따로 생성하지 않고 payment로 묶어서 처리
     * orderPayment를 생성
     *
     * @param userUuid
     * @return
     */
    @Override
    @Transactional
    public OrderPaymentResponseDto completeFirstPayment(UUID userUuid, OrderPaymentRequestDto requestDto) {

        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        Wallet findWallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        OrderPayment orderPayment = orderPaymentService.crateOrderPayment(customer, findWallet);
        OrderPayment savedOrderPayment = orderPaymentRepository.save(orderPayment);

        List<OrderItem> orderItems = orderItemPort.firstOrderItemPayment(customer.getCustomerUuid(), requestDto, savedOrderPayment.getPaymentUuid());

        //총 결제 금액 계산
        BigDecimal totalPrice = orderItems.stream().map(OrderItem::getPriceOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //로깅
        orderItems.forEach(item -> {
            log.debug("OrderItem ID: {}, PriceOriginal: {}", item.getOrderItemUuid(), item.getPriceOriginal());
        });


        log.debug("계산된 총 금액:{}", totalPrice);

        try {
            WalletResponseDto wallet = walletPort.withdrawal(
                    customer.getCustomerUuid(),
                    findWallet.getWalletUuid(),
                    totalPrice
            );

            orderPayment.updateTotalAmount(totalPrice);

            return paymentMapper.completeOrderPayment(savedOrderPayment, wallet);
        } catch (OptimisticLockException e) {
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (InsufficientBalanceException e) { // 특정 비즈니스 예외 처리 추가
            throw new PaymentProcessingException("잔액이 부족합니다.", e);
        } catch (Exception e) {
            log.error("Unexpected error during first payment for user {}: {}", userUuid, e.getMessage(), e);
            // 실패 시 처리는 트랜잭션 롤백으로 자동 처리되므로 명시적 호출은 불필요할 수 있습니다.
            throw new PaymentProcessingException("결제 완료 중 예상치 못한 오류가 발생했습니다.", e);
        }
    }


    /**
     * 상품에 대한 배송비 결제
     * 유저 상품 상태변경, 그리고 요청 금액만 제시하면 될것 같으니까 리팩토링으로 간소화 시키고, 유저가 결제 할때 지갑, 등등을 검증하면 좋을것 같음
     * orderitem 에서 가져올 유저 wallet uuid를 일단 넣고, 나중에, 결제할때 검증용으로 쓰는게 더 안전할거 같기는한데,...
     *
     * @param adminUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public OrderPaymentResponseDto createSecondPayment(UUID adminUuid, OrderPaymentRequestDto requestDto) {

        User admin = userPort.findUserByUserUuid(adminUuid);

        List<UUID> orderItemUuids = requestDto.getOrderItemUuids();
        UUID secondOrderItemUuid = orderItemUuids.get(1);
        OrderItem orderItem = orderItemPort.findByOrderItemUuid(secondOrderItemUuid);

        Customer customer= customerPort.findByCustomerUuid(orderItem.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", adminUuid)));
        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        OrderPayment orderPayment = orderPaymentService.createSecondOrderPayment(customer, requestDto.getDeliveryFee(), admin, wallet);
        OrderPayment savedOrderPayment = orderPaymentRepository.save(orderPayment);
        orderItemPort.secondOrderItemPayment(orderItem.getCustomerUuid(), requestDto, savedOrderPayment.getPaymentUuid());

        OrderPayment secondOrderPayment = orderPaymentService.createSecondOrderPayment(customer, requestDto.getDeliveryFee(), admin, wallet);

        return paymentMapper.createOrderPayment(secondOrderPayment);
    }


    /**
     * 유저가 결제된 상품에 대한 배송비 결제
     *
     * @param userUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public OrderPaymentResponseDto completeSecondPayment(UUID userUuid, OrderPaymentDeliveryRequestDto requestDto) {


        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        if (requestDto.getOrderPaymentUuids() == null || requestDto.getOrderPaymentUuids().isEmpty()) {
            throw new IllegalArgumentException("배송비를 지불할 결제를 선택하세요");
        }
        List<OrderPayment> orderPayments = orderPaymentRepository.findByPaymentUuidInAndCustomerUuid(requestDto.getOrderPaymentUuids(), customer.getCustomerUuid());

        if (orderPayments.size() != requestDto.getOrderPaymentUuids().size()) {
            log.warn("요청된 결제와 조회된 결제가 수가 다름 - 요청: {}, 조회: {}",
                    requestDto.getOrderPaymentUuids().size(), orderPayments.size());
            throw new IllegalArgumentException("일부 결제를 찾을 수 없습니다");
        }

        List<OrderPayment> invalidPayments = orderPayments.stream()
                .filter(payment -> payment.getOrderPaymentStatus() != OrderPaymentStatus.PENDING)
                .toList();

        if (!invalidPayments.isEmpty()) {
            log.warn("결제 불가능한 상태의 결제가 발견 - 개수: {}", invalidPayments.size());
            throw new IllegalStateException("결제할 수 없는 상태의 결제가 포함되어 있습니다");
        }

        Wallet findWallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        List<OrderPayment> invalidPaymentsByWalletUuid = orderPayments.stream()
                .filter(payment -> payment.getWalletUuid() != findWallet.getWalletUuid())
                .toList();


        BigDecimal totalAmount = orderPayments.stream().map(OrderPayment::getPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try {

            WalletResponseDto wallet = walletPort.withdrawal(
                    customer.getCustomerUuid(),
                    findWallet.getWalletUuid(),
                    totalAmount
            );

            orderPayments.forEach(orderPaymentService::completeSecondOrderPayment);

            List<OrderPayment> savedOrderPayments = orderPaymentRepository.saveAll(orderPayments);


            return paymentMapper.completeOrderPayment(savedOrderPayments.get(0), wallet);
        } catch (InsufficientBalanceException e) {
            log.error("잔액 부족으로 인한 결제 실패 - 고객: {}, 요청 금액: {}",
                    customer.getCustomerUuid(), totalAmount);
            orderPaymentService.markAsFailed(orderPayments, "잔액 부족:" + e.getMessage());
            throw new OrderPaymentCompletionException("잔액이 부족합니다.");

        } catch (WalletNotActiveException e) {
            log.error("비활성화된 지갑으로 인한 결제 실패 - 고객: {}, 지갑: {}",
                    customer.getCustomerUuid(), findWallet.getWalletUuid(), e);
            orderPaymentService.markAsFailed(orderPayments, "비활성된 지갑: " + e.getMessage());
            throw new WalletNotActiveException("지갑이 비활성 상태입니다", e);

        } catch (OrderPaymentNotFoundException | OrderPaymentStatusException e) {
            log.error("유효하지 않은 주문 결제 - 결제 UUID들: {}", requestDto.getOrderPaymentUuids(), e);
            throw e;
        } catch (OptimisticLockException e) {
            log.error("동시성 충돌 발생 - 고객: {}, 결제 UUID들: {}",
                    customer.getCustomerUuid(), requestDto.getOrderPaymentUuids(), e);
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
        } catch (Exception e) {
            log.error("결제 완료 중 예상치 못한 오류 - 고객: {}, 결제 UUID들: {}",
                    customer.getCustomerUuid(), requestDto.getOrderPaymentUuids(), e);
            orderPaymentService.markAsFailed(orderPayments, "시스템 오류: " + e.getMessage());
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
        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        OrderPayment findByOrderPayment = findOrderPaymentByCustomerUuidAndPaymentUuid(customer.getCustomerUuid(), paymentUuid);
        OrderPayment orderPayment = orderPaymentService.getOrderPaymentInfo(findByOrderPayment);
        return paymentMapper.orderPaymentInfo(orderPayment);
    }

    /**
     * Order payment 상태, 조건 별 list
     *
     * @param userUuid
     * @param status
     * @param paymentType
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderPaymentResponseDto> getOrderPaymentsByStatusAndType(UUID userUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable) {
        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));

        Page<OrderPayment> orderPaymentsByPending = orderPaymentRepository.findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(customer.getCustomerUuid(), status, paymentType, pageable);

        return orderPaymentsByPending.map(paymentMapper::orderPaymentInfo);
    }

    @Override
    public Page<OrderPaymentResponseDto> getOrderPayments(UUID adminUuid, Pageable pageable) {
        User user = userPort.findUserByUserUuid(adminUuid);
        Customer customer = customerPort.findByUserUuid(user.getUserUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", user.getUserUuid())));

        Wallet wallet = walletPort.findByCustomerUuid(customer.getCustomerUuid());

        Page<OrderPayment> orderPayments = orderPaymentRepository.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return orderPayments.map( orderPayment ->  paymentMapper.orderPaymentDetailsInfo(orderPayment, wallet.getBalance()));
    }

    @Override
    public OrderPaymentCountResponseDto getOrderPaymentDashboardCount(UUID userUuid) {
        Customer customer = customerPort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));

        int firstPending = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.PRODUCT_PAYMENT);
        int firstCompleted = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.PRODUCT_PAYMENT);
        int secondPending = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);
        int secondCompleted = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);

        return paymentMapper.orderPaymentDashboardCount(firstPending, firstCompleted, secondPending, secondCompleted);
    }


    // helper method
    private DepositRequest findDepositRequestByRequestUuid(UUID requestUuid) {
        return depositRequestRepository.findByRefundUuid(requestUuid)
                .orElseThrow(() -> new DepositNotFoundException(String.format("Deposit request not found for request uuid: %s", requestUuid)));
    }

    private DepositRequest findDepositRequestByRequest(UUID requestUuid, Customer customer) {
        return depositRequestRepository.findByRefundUuidAndCustomerUuid(requestUuid, customer.getCustomerUuid())
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

    private WithdrawRequest findWithdrawRequestByRequestUuidAndUserUuid(UUID requestUuid, UUID customerUuid) {
        return withdrawRequestRepository.findByRequestUuidAndCustomerUuid(requestUuid, customerUuid)
                .orElseThrow(() -> new WithdrawRequestNotFoundException(String.format("Withdraw request not found for request uuid: %s", requestUuid)));
    }

    private RefundRequest findRefundRequestByRefundUuid(UUID refundUuid) {
        return refundRequestRepository.findByRefundUuid(refundUuid)
                .orElseThrow(() -> new RefundNotFoundException(String.format("Refund not found for request uuid: %s", refundUuid)));
    }

    private RefundRequest findRefundRequestByRefundUuidAndCustomer(UUID refundUuid, UUID customerUuid) {
        return refundRequestRepository.findByRefundUuidAndCustomerUuid(refundUuid, customerUuid)
                .orElseThrow(() -> new RefundNotFoundException(String.format("Refund request not found for request uuid: %s and customer uuid: %s", refundUuid, customerUuid)));
    }
}




