package com.kijinkai.domain.payment.infrastructure.adapter.repository;

import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataJpaOrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Repository
public class JpaOrderPaymentRepository implements OrderPaymentRepository {

    private final SpringDataJpaOrderPaymentRepository springDataJpaOrderPaymentRepository;

    @Override
    public OrderPayment save(OrderPayment orderPayment) {
        return springDataJpaOrderPaymentRepository.save(orderPayment);
    }

    @Override
    public Optional<OrderPayment> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid) {
        return springDataJpaOrderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid,paymentUuid);
    }

    @Override
    public Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid) {
        return springDataJpaOrderPaymentRepository.findByPaymentUuid(paymentUuid);
    }
}
