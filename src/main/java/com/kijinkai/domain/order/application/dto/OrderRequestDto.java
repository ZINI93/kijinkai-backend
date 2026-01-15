package com.kijinkai.domain.order.application.dto;

import com.kijinkai.domain.orderitem.application.dto.OrderItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private List<OrderItemRequestDto> orderItems;
    private String memo;
    private String convertedCurrency;
    private List<String> orderItemCodes;
    private Map<String,Boolean> inspectedPhotoRequest;
}
