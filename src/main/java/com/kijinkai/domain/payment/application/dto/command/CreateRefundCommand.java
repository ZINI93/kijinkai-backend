package com.kijinkai.domain.payment.application.dto.command;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.orderitem.entity.OrderItem;
import com.kijinkai.domain.payment.domain.enums.RefundType;
import com.kijinkai.domain.payment.application.dto.request.RefundRequestDto;
import com.kijinkai.domain.wallet.entity.Wallet;
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

    public static CreateRefundCommand from(Wallet wallet, Customer customer, OrderItem orderItem, RefundRequestDto requestDto){

        return CreateRefundCommand.builder()
                .customerUuid(customer.getCustomerUuid())
                .walletUuid(wallet.getWalletUuid())
                .orderItemUuid(orderItem.getOrderItemUuid())
                .reason(requestDto.getRefundReason())
                .refundType(requestDto.getRefundType())
                .build();
    }
}
