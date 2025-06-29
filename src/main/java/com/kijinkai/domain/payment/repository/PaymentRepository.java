package com.kijinkai.domain.payment.repository;

import com.kijinkai.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByCustomerCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);
}