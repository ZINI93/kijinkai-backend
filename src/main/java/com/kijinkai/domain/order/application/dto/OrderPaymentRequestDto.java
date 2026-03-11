package com.kijinkai.domain.order.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class OrderPaymentRequestDto {

    private UUID userCouponUuid;
    private List<OrderItemRequest> orderItemRequests;


    @NoArgsConstructor
    @Getter
    public static class OrderItemRequest{

        private UUID orderItemUuid;
        private boolean inspectedPhotoRequest;

    }
}

