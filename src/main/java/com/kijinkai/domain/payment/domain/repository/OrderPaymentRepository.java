package com.kijinkai.domain.payment.domain.repository;

import com.kijinkai.domain.payment.domain.entity.DepositRequest;
import com.kijinkai.domain.payment.domain.entity.OrderPayment;
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

public interface OrderPaymentRepository {

    OrderPayment save(OrderPayment orderPayment);
    Optional<OrderPayment> findByCustomerUuidAndPaymentUuid(UUID customerUuid, UUID paymentUuid);
    Optional<OrderPayment> findByPaymentUuid(UUID paymentUuid);

    Page<OrderPayment> findAllByCustomerUuidAndOrderPaymentStatusAndPaymentTypeOrderByCreatedAtDesc(UUID customerUuid, OrderPaymentStatus status, PaymentType paymentType, Pageable pageable);
    Page<OrderPayment> findAllByCustomerUuid(UUID customerUuid, Pageable pageable);


    List<OrderPayment> findByPaymentUuidInAndCustomerUuid(List<UUID> paymentUuid, UUID customerUuid);
    List<OrderPayment> saveAll(List<OrderPayment> orderPayments);



    int findByOrderPaymentStatusCount(@Param("customerUuid") UUID customerUuid,  @Param("orderPaymentStatus") OrderPaymentStatus orderPaymentStatus, PaymentType  paymentType);

}