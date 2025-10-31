package com.kijinkai.domain.payment.adapter.out.persistence.mapper;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.OrderPaymentJpaEntity;
import com.kijinkai.domain.payment.domain.model.OrderPayment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderPaymentPersistenceMapper {

    OrderPayment toOrderPayment(OrderPaymentJpaEntity orderPaymentJpaEntity);
    OrderPaymentJpaEntity toOrderPaymentJpaEntity(OrderPayment orderPayment);

    List<OrderPayment> toOrderPayments(List<OrderPaymentJpaEntity> orderPaymentJpaEntities);
    List<OrderPaymentJpaEntity> toOrderPaymentsJpaEntity(List<OrderPayment> orderPayments);
}
