package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderPaymentRepository {

    OrderPayment save(OrderPayment orderPayment);
    Optional<OrderPayment> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);
    Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid);
}