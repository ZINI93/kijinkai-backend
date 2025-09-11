package com.kijinkai.domain.common;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.delivery.entity.DeliveryStatus;
import com.kijinkai.domain.delivery.repository.DeliveryRepository;
import com.kijinkai.domain.orderitem.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.repository.OrderItemRepository;
import com.kijinkai.domain.payment.application.dto.response.OrderPaymentCountResponseDto;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.payment.domain.repository.OrderPaymentRepository;
import com.kijinkai.domain.payment.domain.service.OrderPaymentService;
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

        Customer customer = customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(""));

        Wallet wallet = walletRepository.findByCustomerCustomerUuid(customer.getCustomerUuid())
                .orElseThrow(() -> new WalletNotFoundException(""));

        int shippedCount = deliveryRepository.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.SHIPPED);
        int deliveredCount = deliveryRepository.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.DELIVERED);


        int firstCompleted = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.PRODUCT_PAYMENT);
        int secondPending = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);
        int secondCompleted = orderPaymentRepository.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.SHIPPING_PAYMENT);


        int orderItemPendingCount = orderItemRepository.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING);
        int orderItemPendingApprovalCount = orderItemRepository.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING_APPROVAL);
        int orderItemProductPaymentCompletedCount = orderItemRepository.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
        int orderItemAllCount = orderItemRepository.findOrderItemCount(customer.getCustomerUuid());


        return dashBoardMapper.getDashboardCountResponse(wallet.getBalance(), shippedCount,deliveredCount, firstCompleted, secondPending ,secondCompleted,
                orderItemAllCount, orderItemPendingCount, orderItemPendingApprovalCount, orderItemProductPaymentCompletedCount);
    }


}
