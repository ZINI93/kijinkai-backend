package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.payment.domain.entity.RefundRequest;
import com.kijinkai.domain.payment.domain.entity.WithdrawRequest;
import com.kijinkai.domain.payment.domain.enums.WithdrawStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WithdrawRequestRepository {

    Optional<WithdrawRequest> findByRequestUuid(UUID requestUuid);
    Optional<WithdrawRequest> findByRequestUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    WithdrawRequest save(WithdrawRequest request);

    Page<WithdrawRequest> findByWithdrawPaymentUuidByStatus(@Param("customerUuid") UUID custoemrUuid, @Param("bankName") String bankName, @Param("status")WithdrawStatus status, Pageable pageable);
    Page<WithdrawRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);




}

