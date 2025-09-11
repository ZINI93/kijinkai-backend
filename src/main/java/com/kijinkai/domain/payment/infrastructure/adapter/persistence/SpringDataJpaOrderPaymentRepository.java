package com.kijinkai.domain.payment.infrastructure.adapter.persistence;

import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaOrderPaymentRepository extends JpaRepository<OrderPayment , Long> {

    Optional<OrderPayment> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);
    Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid);
    Page<OrderPayment> findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(UUID customerUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable);
    List<OrderPayment> findByPaymentUuidInAndCustomerUuid(List<UUID> orderPaymentUuid, UUID customerUuid);

    @Query("SELECT COUNT(op) FROM OrderPayment op WHERE op.customerUuid = :customerUuid AND op.orderPaymentStatus = :orderPaymentStatus AND op.paymentType = :paymentType")
    int findByOrderPaymentStatusCount(@Param("customerUuid") UUID customerUuid, @Param("orderPaymentStatus") OrderPaymentStatus orderPaymentStatus, @Param("paymentType") PaymentType paymentType);

    Page<OrderPayment> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);
}
