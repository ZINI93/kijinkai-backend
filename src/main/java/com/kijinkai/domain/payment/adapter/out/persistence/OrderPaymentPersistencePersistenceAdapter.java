package com.kijinkai.domain.payment.adapter.out.persistence;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import com.kijinkai.domain.payment.adapter.out.persistence.mapper.OrderPaymentPersistenceMapper;
import com.kijinkai.domain.payment.adapter.out.persistence.repository.SpringDataJpaOrderPaymentRepository;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderPaymentPersistencePersistenceAdapter implements OrderPaymentPersistencePort {

    private final SpringDataJpaOrderPaymentRepository springDataJpaOrderPaymentRepository;
    private final OrderPaymentPersistenceMapper orderPaymentPersistenceMapper;

    @Override
    public OrderPayment saveOrderPayment(OrderPayment orderPayment) {
        OrderPaymentJpaEntity orderPaymentJpaEntity = orderPaymentPersistenceMapper.toOrderPaymentJpaEntity(orderPayment);
        orderPaymentJpaEntity = springDataJpaOrderPaymentRepository.save(orderPaymentJpaEntity);
        return orderPaymentPersistenceMapper.toOrderPayment(orderPaymentJpaEntity);
    }

    @Override
    public Optional<OrderPayment> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid) {
        return springDataJpaOrderPaymentRepository.findByCustomerUuidAndPaymentUuid(customerUuid,paymentUuid)
                .map(orderPaymentPersistenceMapper::toOrderPayment);
    }

    @Override
    public Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid) {
        return springDataJpaOrderPaymentRepository.findByPaymentUuid(paymentUuid)
                .map(orderPaymentPersistenceMapper::toOrderPayment);    }

    @Override
    public Page<OrderPayment> findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(UUID customerUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable) {
        return springDataJpaOrderPaymentRepository.findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(customerUuid,status,paymentType,pageable)
                .map(orderPaymentPersistenceMapper::toOrderPayment);

    }

    @Override
    public Page<OrderPayment> findAllByCustomerUuid(UUID customerUuid, Pageable pageable) {
        return springDataJpaOrderPaymentRepository.findAllByCustomerUuid(customerUuid,pageable)
                .map(orderPaymentPersistenceMapper::toOrderPayment);
    }

    @Override
    public List<OrderPayment> findByPaymentUuidInAndCustomerUuid(List<UUID> paymentUuid, UUID customerUuid) {
        return springDataJpaOrderPaymentRepository.findByPaymentUuidInAndCustomerUuid(paymentUuid,customerUuid)
                .stream().map(orderPaymentPersistenceMapper::toOrderPayment)
                .toList();
    }

    @Override
    public int findByOrderPaymentStatusCount(UUID customerUuid, OrderPaymentStatus orderPaymentStatus, PaymentType paymentType) {
        return springDataJpaOrderPaymentRepository.findByOrderPaymentStatusCount(customerUuid, orderPaymentStatus, paymentType);
    }

    @Override
    public List<OrderPayment> saveAll(List<OrderPayment> orderPayments) {
        List<OrderPaymentJpaEntity> orderPaymentsJpaEntity = orderPaymentPersistenceMapper.toOrderPaymentsJpaEntity(orderPayments);
        orderPaymentsJpaEntity = springDataJpaOrderPaymentRepository.saveAll(orderPaymentsJpaEntity);
        return orderPaymentPersistenceMapper.toOrderPayments(orderPaymentsJpaEntity);
    }
}
