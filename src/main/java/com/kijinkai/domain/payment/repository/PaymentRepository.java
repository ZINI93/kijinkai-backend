package com.kijinkai.domain.payment.repository;

import com.kijinkai.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByCustomerCustomerUuidAndPaymentUuid(String customerUuid, String paymentUuid);
}