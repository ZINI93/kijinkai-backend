package com.kijinkai.domain.payment.infrastructure.adapter.repository;

import com.kijinkai.domain.payment.domain.entity.OrderPayment;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.payment.infrastructure.adapter.persistence.SpringDataJpaOrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        return springDataJpaOrderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid, paymentUuid);
    }

    @Override
    public Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid) {
        return springDataJpaOrderPaymentRepository.findByPaymentUuid(paymentUuid);
    }

    @Override
    public Page<OrderPayment> findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(UUID customerUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable) {
        return springDataJpaOrderPaymentRepository.findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(customerUuid, status, paymentType, pageable);
    }

    @Override
    public Page<OrderPayment> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return springDataJpaOrderPaymentRepository.findAllByCustomerUuid(customerUuid,pageable);
    }

    @Override
    public List<OrderPayment> findByPaymentUuidInAndCustomerUuid(List<UUID> PaymentUuid, UUID customerUuid) {
        return springDataJpaOrderPaymentRepository.findByPaymentUuidInAndCustomerUuid(PaymentUuid, customerUuid);
    }

    @Override
    public List<OrderPayment> saveAll(List<OrderPayment> orderPayments) {
        return springDataJpaOrderPaymentRepository.saveAll(orderPayments);
    }

    @Override
    public int findByOrderPaymentStatusCount(UUID customerUuid, OrderPaymentStatus orderPaymentStatus, PaymentType paymentType) {
        return springDataJpaOrderPaymentRepository.findByOrderPaymentStatusCount(customerUuid, orderPaymentStatus, paymentType);
    }


}
