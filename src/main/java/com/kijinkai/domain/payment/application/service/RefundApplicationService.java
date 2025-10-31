package com.kijinkai.domain.payment.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.orderitem.domain.exception.OrderItemNotFoundException;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.payment.application.dto.response.RefundResponseDto;
import com.kijinkai.domain.payment.application.mapper.PaymentMapper;
import com.kijinkai.domain.payment.application.port.in.refund.CreateRefundUseCase;
import com.kijinkai.domain.payment.application.port.in.refund.DeleteRefundUseCase;
import com.kijinkai.domain.payment.application.port.in.refund.GetRefundUseCase;
import com.kijinkai.domain.payment.application.port.in.refund.UpdateRefundUseCase;
import com.kijinkai.domain.payment.application.port.out.RefundPersistencePort;
import com.kijinkai.domain.payment.domain.exception.PaymentProcessingException;
import com.kijinkai.domain.payment.domain.exception.RefundNotFoundException;
import com.kijinkai.domain.payment.domain.factory.PaymentFactory;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import com.kijinkai.domain.payment.domain.service.RefundRequestService;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RefundApplicationService implements CreateRefundUseCase, GetRefundUseCase, UpdateRefundUseCase, DeleteRefundUseCase {

    private final OrderItemPersistencePort orderItemPersistencePort;
    private final CustomerPersistencePort customerPersistencePort;
    private final WalletPersistencePort walletPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final UpdateWalletUseCase updateWalletUseCase;


    private final RefundPersistencePort refundPersistencePort;

    private final RefundRequestService refundRequestService;
    private final PaymentMapper paymentMapper;
    private final PaymentFactory paymentFactory;

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

        OrderItem orderItem = orderItemPersistencePort.findByOrderItemUuid(orderItemUuid)
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem not found for OrderItemUuid: %s", orderItemUuid)));
        orderItem.isCancel();

        Customer customer = customerPersistencePort.findByCustomerUuid(orderItem.getCustomerUuid())
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", adminUuid)));

        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        RefundRequest refundPayment = paymentFactory.createRefundPayment(
                customer, wallet, orderItem, orderItem.getPriceOriginal(), adminUuid,
                requestDto.getRefundReason(), requestDto.getRefundType()
        );

        RefundRequest savedRefundRequest = refundPersistencePort.save(refundPayment);

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

        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        RefundRequest request = findRefundRequestByRefundUuid(refundUuid);
        request.complete(memo);

        try {

            WalletResponseDto wallet = updateWalletUseCase.deposit(
                    request.getCustomerUuid(),
                    request.getWalletUuid(),
                    request.getRefundAmount()
            );
            RefundRequest savedRefundRequest = refundPersistencePort.save(request);

            log.info("Completed refund approval process for refund uuid: {}", savedRefundRequest.getRefundUuid());
            return paymentMapper.processRefundResponse(savedRefundRequest, wallet);
        } catch (OptimisticLockException e) {  // 이 부분 추가 필요
            throw new ConcurrentModificationException("다른 관리자가 동시에 처리 중 입니다. 새로고침 후 다시 시도해주세요");
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

        User admin = findUserByUserUuid(adminUuid);
        admin.validateAdminRole();

        RefundRequest refundRequest = findRefundRequestByRefundUuid(refundUuid);
        log.info("retrieved refund request for refund uuid by admin: {}", refundUuid);
        return paymentMapper.refundInfoResponse(refundRequest);
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

        Customer customer = customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for UserUuid: %s", userUuid)));
        RefundRequest request = findRefundRequestByRefundUuidAndCustomer(refundUuid, customer.getCustomerUuid());

        log.info("retrieved refund request for refund uuid: {}", refundUuid);
        return paymentMapper.refundInfoResponse(request);
    }

    /**
     * 유저 - 환전 리스트 전체조회
     *
     * @param adminUuid
     * @param pageable
     * @return
     */
    @Override
    public Page<RefundResponseDto> getRefunds(UUID userUuid, Pageable pageable) {

        Customer customer = customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for userUuid: %s", userUuid)));

        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        Page<RefundRequest> refunds = refundPersistencePort.findAllByCustomerUuid(customer.getCustomerUuid(), pageable);

        return refunds.map(refundRequest -> paymentMapper.refundDetailsInfo(refundRequest, wallet.getBalance()));
    }


    //helper method
    private Wallet findWalletByCustomerUuid(UUID customerUuid) {
        return walletPersistencePort.findByCustomerUuid(customerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found exception for customerUuid: %s", customerUuid)));
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found exception for userUuid: %s", userUuid)));
    }

    private RefundRequest findRefundRequestByRefundUuid(UUID refundUuid) {
        return refundPersistencePort.findByRefundUuid(refundUuid)
                .orElseThrow(() -> new RefundNotFoundException(String.format("Refund not found for request uuid: %s", refundUuid)));
    }

    private RefundRequest findRefundRequestByRefundUuidAndCustomer(UUID refundUuid, UUID customerUuid) {
        return refundPersistencePort.findByRefundUuidAndCustomerUuid(refundUuid, customerUuid)
                .orElseThrow(() -> new RefundNotFoundException(String.format("Refund request not found for request uuid: %s and customer uuid: %s", refundUuid, customerUuid)));
    }


}
