package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.enums.DepositStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepositRequestRepository {

    Optional<DepositRequest> findByRefundUuid(UUID reqeustUuid);
    List<DepositRequest> findByStatus(DepositStatus status);
    Optional<DepositRequest> findByRefundUuidAndCustomerUuid(UUID requestUuid, UUID customerUuid);
    DepositRequest save(DepositRequest depositRequestEntity);
    List<DepositRequest> saveAll(List<DepositRequest> depositRequestEntities);

    Page<DepositRequest> findByDepositPaymentUuidByStatus(@Param("customerUuid") UUID customerUuid, @Param("bankName") String bankName, @Param("status")DepositStatus status, Pageable pageable);

    Page<DepositRequest> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);

}