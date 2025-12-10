package com.kijinkai.domain.common;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.delivery.application.out.DeliveryPersistencePort;
import com.kijinkai.domain.delivery.domain.model.DeliveryStatus;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.port.out.OrderItemPersistencePort;
import com.kijinkai.domain.payment.application.port.out.OrderPaymentPersistencePort;
import com.kijinkai.domain.payment.domain.enums.OrderPaymentStatus;
import com.kijinkai.domain.payment.domain.enums.PaymentType;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DashBoardService {

    private final CustomerPersistencePort customerPersistencePort;
    private final WalletPersistencePort walletPersistencePort;
    private final OrderPaymentPersistencePort orderPaymentPersistencePort;

    private final DeliveryPersistencePort deliveryPersistencePort;
    private final OrderItemPersistencePort orderItemPersistencePort;

    private final DashBoardMapper dashBoardMapper;


    /**
     * 메인 화면 dashboard
     * 문제 - customer 가 있어야 등록이 보임 등록이 되어 있지 않아도 화면을 볼수 있어야 함
     * @param userUuid
     * @return
     */
    public DashBoardCountResponseDto  getDashboardCount(UUID userUuid){

        Optional<Customer> customerOpt = customerPersistencePort.findByUserUuid(userUuid);
//                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found exception for user uuid: %s", userUuid)));

        if (customerOpt.isEmpty()) {
            // Customer 미등록 상태 — 빈 대시보드 반환
            return dashBoardMapper.getDashboardCountResponse(
                    BigDecimal.ZERO,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0
            );
        }

        Customer customer = customerOpt.get();

        Wallet wallet = walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found exception for user uuid: %s", userUuid)));

        int shippedCount = deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.SHIPPED);
        int deliveredCount = deliveryPersistencePort.findByDeliveryStatusCount(customer.getCustomerUuid(), DeliveryStatus.DELIVERED);

        int firstCompleted = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.PRODUCT_PAYMENT);
        int secondPending = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.PENDING, PaymentType.SHIPPING_PAYMENT);
        int secondCompleted = orderPaymentPersistencePort.findByOrderPaymentStatusCount(customer.getCustomerUuid(), OrderPaymentStatus.COMPLETED, PaymentType.SHIPPING_PAYMENT);

        int orderItemPendingCount = orderItemPersistencePort.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING);
        int orderItemPendingApprovalCount = orderItemPersistencePort.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PENDING_APPROVAL);
        int orderItemProductPaymentCompletedCount = orderItemPersistencePort.findOrderItemCountByStatus(customer.getCustomerUuid(), OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);
        int orderItemAllCount = orderItemPersistencePort.findOrderItemCount(customer.getCustomerUuid());


        return dashBoardMapper.getDashboardCountResponse(wallet.getBalance(), shippedCount,deliveredCount, firstCompleted, secondPending ,secondCompleted,
                orderItemAllCount, orderItemPendingCount, orderItemPendingApprovalCount, orderItemProductPaymentCompletedCount);
    }


}
