package com.kijinkai.domain.payment.repository;

import com.kijinkai.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByCustomerCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);
    Optional<Payment> findByPaymentUuid(UUID paymentUuid);



    @Query("SELECT p FROM Payment p JOIN FETCH p.customer c JOIN FETCH p.wallet w WHERE p.paymentUuid = :paymentUuid")
    Optional<Payment> findByPaymentUuidWithCustomerAndWallet(@Param("paymentUuid") UUID paymentUuid);

    @Query("SELECT p FROM Payment p JOIN FETCH p.customer c JOIN FETCH p.wallet w WHERE c.customerUuid = :customerUuid AND p.paymentUuid = :paymentUuid")
    Optional<Payment> findByCustomerCustomerUuidAndPaymentUuidWithCustomerAndWallet(@Param("customerUuid") UUID customerUuid, @Param("paymentUuid") UUID paymentUuid);}