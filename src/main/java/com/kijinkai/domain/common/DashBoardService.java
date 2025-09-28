package com.kijinkai.domain.common;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.adapter.out.persistence.repository.CustomerRepository;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import com.kijinkai.domain.delivery.repository.DeliveryRepository;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DashBoardService {


    private final CustomerRepository customerRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final WalletRepository walletRepository;
    private final OrderItemRepository orderItemRepository;


    private final DashBoardMapper dashBoardMapper;



    public DashBoardCountResponseDto  getDashboardCount(UUID userUuid){

        CustomerJpaEntity customerJpaEntity = customerRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(""));

        Wallet wallet = walletRepository.findByCustomerUuid(customerJpaEntity.getCustomerUuid())
                .orElseThrow(() -> new WalletNotFoundException(""));

        int shippedCount = deliveryRepository.findByDeliveryStatusCount(customerJpaEntity.getCustomerUuid(), DeliveryStatus.SHIPPED);
        int deliveredCount = deliveryRepository.findByDeliveryStatusCount(customerJpaEntity.getCustomerUuid(), DeliveryStatus.DELIVERED);


        int firstCompleted = orderPaymentRepository.findByOrderPaymentStatusCount(customerJpaEntity.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.PRODUCT_PAYMENT);
        int secondPending = orderPaymentRepository.findByOrderPaymentStatusCount(customerJpaEntity.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);
        int secondCompleted = orderPaymentRepository.findByOrderPaymentStatusCount(customerJpaEntity.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.SHIPPING_PAYMENT);


        int orderItemPendingCount = orderItemRepository.findOrderItemCountByStatus(customerJpaEntity.getCustomerUuid(), OrderItemStatus.PENDING);
        int orderItemPendingApprovalCount = orderItemRepository.findOrderItemCountByStatus(customerJpaEntity.getCustomerUuid(), OrderItemStatus.PENDING_APPROVAL);
        int orderItemProductPaymentCompletedCount = orderItemRepository.findOrderItemCountByStatus(customerJpaEntity.getCustomerUuid(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
        int orderItemAllCount = orderItemRepository.findOrderItemCount(customerJpaEntity.getCustomerUuid());


        return dashBoardMapper.getDashboardCountResponse(wallet.getBalance(), shippedCount,deliveredCount, firstCompleted, secondPending ,secondCompleted,
                orderItemAllCount, orderItemPendingCount, orderItemPendingApprovalCount, orderItemProductPaymentCompletedCount);
    }


}
