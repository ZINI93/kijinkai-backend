package com.kijinkai.domain.payment.application.dto.command;


import com.kijinkai.domain.customer.adapter.out.persistence.entity.CustomerJpaEntity;
import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CreateRefundCommand {

    UUID customerUuid;
    UUID walletUuid;
    UUID orderItemUuid;
    String reason;
    RefundType refundType;

    public static CreateRefundCommand from(WalletJpaEntity wallet, CustomerJpaEntity customerJpaEntity, OrderItem orderItem, RefundRequestDto requestDto){

        return CreateRefundCommand.builder()
                .customerUuid(customerJpaEntity.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderItemUuid(orderItem.getOrderItemUuid())
                .reason(requestDto.getRefundReason())
                .refundType(requestDto.getRefundType())
                .build();
    }
}
