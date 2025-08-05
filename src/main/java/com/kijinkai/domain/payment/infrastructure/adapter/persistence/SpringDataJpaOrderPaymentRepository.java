package com.kijinkai.domain.payment.infrastructure.adapter.persistence;

import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaOrderPaymentRepository extends JpaRepository<OrderPayment , Long> {

    Optional<OrderPayment> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);

    Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid);
}
