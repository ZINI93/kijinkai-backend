package com.kijinkai.domain.common.service;


import com.kijinkai.domain.common.dto.MyPageResponseDto;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.exchange.dto.ExchangeRateResponseDto;
import com.kijinkai.domain.exchange.service.ExchangeRateService;
import com.kijinkai.domain.orderitem.adapter.out.persistence.entity.OrderItemStatus;
import com.kijinkai.domain.orderitem.application.port.in.GetOrderItemUseCase;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.shipment.entity.ShipmentStatus;
import com.kijinkai.domain.shipment.service.ShipmentService;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.port.in.GetUserUseCase;
import com.kijinkai.domain.wallet.application.dto.WalletBalanceResponseDto;
import com.kijinkai.domain.wallet.application.port.in.GetWalletUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MyPageService {


    private final GetUserUseCase getUserUseCase;
    private final GetWalletUseCase getWalletUseCase;
    private final GetOrderItemUseCase getOrderItemUseCase;
    private final ShipmentService shipmentService;

    private final ExchangeRateService exchangeRateService;


    public MyPageResponseDto myPage(UUID userUuid) {

        UserResponseDto userInfo = getUserUseCase.getUserInfo(userUuid);

        //원화 환률
        ExchangeRateResponseDto exchangeRate = exchangeRateService.getExchangeRateInfoByCurrency(Currency.KRW);


        // 지갑 관련
        WalletBalanceResponseDto walletBalance = getWalletUseCase.getWalletBalance(userUuid);


        //구매 가능금액
        BigDecimal availableBalance = walletBalance.getBalance().multiply(exchangeRate.getRate()).multiply(BigDecimal.valueOf(0.01)).setScale(0, RoundingMode.HALF_UP);


        // 미결제 금액
        List<OrderItem> orderItems = getOrderItemUseCase.getOrderItemsByCustomerAndOrderItemsStatus(userUuid, List.of(OrderItemStatus.PENDING, OrderItemStatus.PENDING_APPROVAL,
                OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST));

        BigDecimal outstandingBalance = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            outstandingBalance = outstandingBalance.add(orderItem.getPriceOriginal());
        }

        //미출고현황
        int undispatchedOrders = getOrderItemUseCase.countOrderItemByStatusIn(userUuid, List.of(OrderItemStatus.PRODUCT_PAYMENT_COMPLETED, OrderItemStatus.LOCAL_DELIVERY_COMPLETED,
                OrderItemStatus.PRODUCT_CONSOLIDATING, OrderItemStatus.DELIVERY_FEE_PAYMENT_REQUEST, OrderItemStatus.DELIVERY_FEE_PAYMENT_COMPLETED));

        //실패
        int failedOrders = getOrderItemUseCase.countOrderItemByStatusIn(userUuid, List.of(OrderItemStatus.CANCELLED, OrderItemStatus.REJECTED));

        //구매요청
        int purchaseRequestOrders = getOrderItemUseCase.countOrderItemsByStatus(userUuid, OrderItemStatus.PENDING);

        //구매승인
        int purchaseApprovedOrders = getOrderItemUseCase.countOrderItemsByStatus(userUuid, OrderItemStatus.PENDING_APPROVAL);

        //1차 결제 완료
        int firstPaymentCompletedOrders = getOrderItemUseCase.countOrderItemsByStatus(userUuid, OrderItemStatus.PRODUCT_PAYMENT_COMPLETED);

        //현지배송 완료
        int localDeliveryCompletedOrders = getOrderItemUseCase.countOrderItemsByStatus(userUuid, OrderItemStatus.LOCAL_DELIVERY_COMPLETED);

        //배송통합진행
        int orderItemByPending = getOrderItemUseCase.countOrderItemsByStatus(userUuid, OrderItemStatus.PRODUCT_CONSOLIDATING);

        //2차결제 요청(국제 배송비)
        int deliveryPaymentRequestedShipments = shipmentService.countShipmentByStatus(userUuid, ShipmentStatus.PAYMENT_PENDING);

        //2차결제 완료(국제 배송비)
        int deliveryPaymentCompletedShipments = shipmentService.countShipmentByStatus(userUuid, ShipmentStatus.PREPARING);

        //국제 배송중
        int internationalShippingOrders = shipmentService.countShipmentByStatus(userUuid, ShipmentStatus.SHIPPED);

        //배송완료
        int deliveredOrders = shipmentService.countShipmentByStatus(userUuid, ShipmentStatus.DELIVERED);

        return MyPageResponseDto.builder()
                //유저 정보관련
                .nickname(userInfo.getNickname())

                //지갑관련
                .depositBalance(walletBalance.getBalance().setScale(0,RoundingMode.HALF_UP)) //전체 잔액
                .availableBalance(availableBalance) //결제가능금액
                .outstandingBalance(outstandingBalance) // 미결제 금액

                //배송 출고 관련
                .undispatchedOrders(undispatchedOrders)

                .failedOrders(failedOrders)
                .purchaseRequestOrders(purchaseRequestOrders)
                .purchaseApprovedOrders(purchaseApprovedOrders)
                .firstPaymentCompletedOrders(firstPaymentCompletedOrders)
                .localDeliveryCompletedOrders(localDeliveryCompletedOrders)
                .combinedProcessingOrders(orderItemByPending)
                .secondPaymentRequestedOrders(deliveryPaymentRequestedShipments)
                .secondPaymentCompletedOrders(deliveryPaymentCompletedShipments)
                .internationalShippingOrders(internationalShippingOrders)
                .deliveredOrders(deliveredOrders)
                .build();
    }
}
