package com.kijinkai.domain.payment.adapter.out.persistence.repository;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
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

public interface SpringDataJpaOrderPaymentRepository extends JpaRepository<OrderPaymentJpaEntity, Long> {

    Optional<OrderPaymentJpaEntity> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);
    Optional<OrderPaymentJpaEntity> findByPaymentUuid(UUID paymentUuid);
    Page<OrderPaymentJpaEntity> findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(UUID customerUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable);
    List<OrderPaymentJpaEntity> findByPaymentUuidInAndCustomerUuid(List<UUID> orderPaymentUuid, UUID customerUuid);

    @Query("SELECT COUNT(op) FROM OrderPaymentJpaEntity op WHERE op.customerUuid = :customerUuid AND op.orderPaymentStatus = :orderPaymentStatus AND op.paymentType = :paymentType")
    int findByOrderPaymentStatusCount(@Param("customerUuid") UUID customerUuid, @Param("orderPaymentStatus") OrderPaymentStatus orderPaymentStatus, @Param("paymentType") PaymentType paymentType);

    Page<OrderPaymentJpaEntity> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);
}
