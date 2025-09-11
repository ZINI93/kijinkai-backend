package com.kijinkai.domain.payment.application.dto.request;

import com.kijinkai.domain.orderitem.entity.OrderItem;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderPaymentRequestDto {

    List<UUID> orderItemUuids;
    BigDecimal deliveryFee;

}
